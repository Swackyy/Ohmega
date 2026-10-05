package com.swacky.ohmega.api.common.accessorytype;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.config.OhmegaServerConfig;
import com.swacky.ohmega.api.common.event.OhmegaHooks;
import com.swacky.ohmega.api.common.init.OhmegaTags;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Data holder class for {@link AccessoryType}s, a resource listener that will fetch types from found JSONs
 * <p>
 * Types are stored as a {@link HashMap} with their {@link Identifier}s as the keys to reduce fetch time,
 * but a linear {@link ArrayList} version is cached for iterative use and can be fetched with {@link #getTypes()}
 * <p>
 * This class is slightly more complex as I would've liked due to general vanilla multithreading shenanigans,
 * and the subtle differences in config APIs used across loaders
 */
public final class AccessoryTypeManager extends SimplePreparableReloadListener<Map<Identifier, AccessoryType>> {
    private static final @NonNull AccessoryTypeManager INSTANCE = new AccessoryTypeManager();
    private static final @NonNull HashMap<@NonNull Identifier, @NonNull AccessoryType> TYPES = new HashMap<>();
    private static final @NonNull HashMap<@NonNull Identifier, @NonNull ArrayList<@NonNull ObjectIntImmutablePair<@NonNull AccessoryType>>> ADVANCEMENT_REWARDS = new HashMap<>();
    private static final @NonNull ArrayList<@NonNull AccessoryType> DEFAULT_SLOTS = new ArrayList<>();
    private static final @NonNull ArrayList<@NonNull AccessoryType> TYPES_LIST = new ArrayList<>();
    private static final @NonNull HashSet<@NonNull Identifier> REFERENCEABLE_TYPES = new HashSet<>();
    private static final @NonNull ArrayList<@NonNull String> DEFAULT_SLOTS_STRING_LIST = new ArrayList<>();
    private static final @NonNull ThreadLocal<@NonNull Boolean> ALLOW_POST_EVENTS = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final @NonNull ArrayList<@NonNull Runnable> APPLY_TASKS = new ArrayList<>();
    private static final @NonNull ArrayList<@NonNull Runnable> CONFIG_LOAD_TASKS = new ArrayList<>();
    public static final @NonNull String LOCATION = Ohmega.MODID + "/accessory_types.json";

    // todo: maybe move this to Accessories instead as it makes more sense
    private static @Nullable Map<Item, BooleanObjectPair<AccessoryType>> accessoryTypeOverrides;

    /**
     * Constructs the accessory manager singleton instance
     */
    private AccessoryTypeManager() {}

    /**
     * Retrieve the singleton instance of the accessory type manager.
     * You shouldn't need to use this as all methods are {@code static}
     * @return the singleton {@link AccessoryTypeManager} instance
     */
    public static @NonNull AccessoryTypeManager getInstance() {
        return INSTANCE;
    }

    /**
     * An internal function to control event posting, do not call this.
     */
    public static void lockEvents() {
        ALLOW_POST_EVENTS.set(Boolean.FALSE);
    }

    /**
     * An internal function to control event posting, do not call this.
     */
    public static void unlockEvents() {
        ALLOW_POST_EVENTS.set(Boolean.TRUE);
    }

    /**
     * Posts the override types event and binds its result to the accessory manager
     */
    public static void postOverrideTypes() {
        accessoryTypeOverrides = OhmegaHooks.overrideTypes();
    }

    /**
     * Called by vanilla for reload listeners, searches {@code data/} directories for files matching {@link #LOCATION}
     * @param manager holder for server resources, {@code data}
     * @param profiler a telemetry filler
     * @return a map of {@link Identifier}s to their corresponding {@link AccessoryType}s
     */
    @Override
    protected @NonNull Map<Identifier, AccessoryType> prepare(@NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        Map<Identifier, AccessoryType> map = new HashMap<>(5);
        Set<String> namespaces = manager.getNamespaces();
        int namespaceCount = 0;

        ADVANCEMENT_REWARDS.clear();
        DEFAULT_SLOTS.clear();

        for (String namespace : namespaces) {
            int typeCount = 0;

            for (Resource resource : manager.getResourceStack(Identifier.fromNamespaceAndPath(namespace, LOCATION))) {
                try (Reader reader = resource.openAsReader()) {
                    Optional<Pair<Map<String, AccessoryType.Builder>, JsonElement>> optional = AccessoryType.Builder.MAP_CODEC.decode(
                            JsonOps.INSTANCE,
                            GsonHelper.parse(reader)
                    ).resultOrPartial(message -> Ohmega.LOGGER.warn("Could not parse JSON '{}' in DataPack '{}', message: {}",
                            LOCATION,
                            resource.sourcePackId(),
                            message));

                    if (optional.isPresent()) {
                        for (Map.Entry<String, AccessoryType.Builder> entry : optional.get().getFirst().entrySet()) {
                            AccessoryType type = entry.getValue().build(namespace, entry.getKey());
                            Identifier id = type.getId();

                            // todo: prioritise namespaces better here
                            // this will probably mean allowing for full identifier specification in accessory type definitions,
                            // including for the data generator
                            map.put(id, type);

                            for (Object2IntMap.Entry<Identifier> reward : type.getAdvancementRewards().entrySet()) {
                                ADVANCEMENT_REWARDS.computeIfAbsent(reward.getKey(), _ -> new ArrayList<>())
                                        .add(ObjectIntImmutablePair.of(type, reward.getIntValue()));
                            }

                            int defaultSlotsSize = type.getDefaultSlots();

                            for (int i = 0; i < defaultSlotsSize; i++) {
                                DEFAULT_SLOTS.add(type);
                            }

                            if (typeCount++ == 0) {
                                namespaceCount++;
                            }
                        }
                    } else {
                        Ohmega.LOGGER.warn("Could not decode JSON '{}' in DataPack: '{}'", LOCATION, resource.sourcePackId());
                    }
                } catch (Exception e) {
                    Ohmega.LOGGER.warn("Could not read '{}' in DataPack: '{}'", LOCATION, resource.sourcePackId(), e);
                }
            }

            if (typeCount > 0) {
                Ohmega.LOGGER.debug("Loaded {} accessory type(s) from namespace '{}'", typeCount, namespace);
            }
        }

        Ohmega.LOGGER.info("Loaded {} accessory type(s) from {} namespace(s)", map.size(), namespaceCount);
        return map;
    }

    /**
     * Reset the types and fill with the new data, performing important regulatory behaviour
     * @param types the new types to set as
     */
    private static void apply(@NonNull Map<Identifier, AccessoryType> types) {
        TYPES.clear();
        TYPES.put(AccessoryType.NONE.getId(), AccessoryType.NONE);
        TYPES.putAll(types);
        TYPES.putAll(OhmegaHooks.registerAccessoryTypes());
        TYPES_LIST.clear();
        TYPES_LIST.addAll(TYPES.values());

        for (AccessoryType type : TYPES.values()) {
            if (type.allowReference()) {
                REFERENCEABLE_TYPES.add(type.getId());
            }
        }

        DEFAULT_SLOTS.sort(Comparator.comparingInt(AccessoryType::getSlotPriority));
        DEFAULT_SLOTS_STRING_LIST.clear();
        DEFAULT_SLOTS_STRING_LIST.ensureCapacity(TYPES.size());

        for (AccessoryType type : DEFAULT_SLOTS) {
            DEFAULT_SLOTS_STRING_LIST.add(type.getId().toString());
        }

        if (ALLOW_POST_EVENTS.get()) {
            postOverrideTypes();
        }

        if (!APPLY_TASKS.isEmpty()) {
            if (OhmegaServerConfig.isLoaded()) {
                APPLY_TASKS.forEach(Runnable::run);
            } else {
                CONFIG_LOAD_TASKS.addAll(APPLY_TASKS);
            }

            APPLY_TASKS.clear();
        }

        OhmegaTags.refresh();
    }

    /**
     * Publicly exposed method to apply new accessory types, used in syncing
     * @param types the new types to set as
     */
    public static void apply(@NonNull Collection<AccessoryType> types) {
        Map<Identifier, AccessoryType> map = new HashMap<>(types.size());

        for (AccessoryType type : types) {
            map.put(type.getId(), type);
        }

        apply(map);
    }

    /**
     * Vanilla apply, defers to {@link #apply(Map)}
     * @param types new types to set
     * @param manager holder for server resources, {@code data}
     * @param profiler a telemetry filler
     */
    @Override
    protected void apply(@NonNull Map<Identifier, AccessoryType> types, @NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        apply(types);
    }

    /**
     * Remove all known accessory types
     */
    public static void clear() {
        TYPES.clear();
        TYPES_LIST.clear();
        ADVANCEMENT_REWARDS.clear();
        DEFAULT_SLOTS.clear();
        REFERENCEABLE_TYPES.clear();
        DEFAULT_SLOTS_STRING_LIST.clear();
        lockEvents();

        accessoryTypeOverrides = null;
    }

    /**
     * Defer a task to be executed following type application
     * @param runnable task to enqueue
     */
    public static void deferApply(@NonNull Runnable runnable) {
        APPLY_TASKS.add(runnable);
    }

    /**
     * Execute config load tasks
     */
    public static void runConfigLoadTasks() {
        CONFIG_LOAD_TASKS.forEach(Runnable::run);
        CONFIG_LOAD_TASKS.clear();
    }

    /**
     * Check whether a type with the specified {@link Identifier} key exists
     * @param id unique identifier for the type
     * @return {@code true} if found, {@code false} otherwise
     */
    public static boolean exists(@Nullable Identifier id) {
        if (id != null) {
            return TYPES.containsKey(id);
        }

        return false;
    }

    /**
     * Attempt to find the known accessory type with the given unique ID
     * @param id unique identifier for the type
     * @return the {@link AccessoryType} if found, {@link AccessoryType#NONE} if no type is present with the given ID
     */
    public static @NonNull AccessoryType get(@NonNull Identifier id) {
        AccessoryType candidate = TYPES.get(id);

        if (candidate != null) {
            return candidate;
        }

        return AccessoryType.NONE;
    }

    /**
     * A safe empty check that accommodates for the manager always holding a reference to the fallback {@link AccessoryType#NONE} type
     * @return {@code true} if the manager contains one, and only one type, that being {@link AccessoryType#NONE},
     * or fallbacks to calling {@link HashMap#isEmpty()} on the {@link #TYPES} (this is done first; although it is the more unlikely option, it is faster)
     */
    public static boolean isEmpty() {
        return TYPES.isEmpty() || (TYPES.size() == 1 && TYPES.containsKey(AccessoryType.NONE_ID));
    }

    /**
     * Retrieve the advancement reward(s) for a given advancement {@link Identifier}
     * @param id advancement ID
     * @return a possibly empty, but never {@code null}, list of pairings of {@link AccessoryType} and {@code int}s to reward advancement holders with
     */
    public static @NonNull List<@NonNull ObjectIntImmutablePair<@NonNull AccessoryType>> getAdvancementReward(@NonNull Identifier id) {
        ArrayList<@NonNull ObjectIntImmutablePair<@NonNull AccessoryType>> list = ADVANCEMENT_REWARDS.get(id);

        if (list != null) {
            return list;
        }

        return List.of();
    }

    /**
     * Retrieve the default slot types list, composed from {@link AccessoryType#getDefaultSlots()}
     * @return the default accessory slot types list
     */
    public static @NonNull ArrayList<@NonNull AccessoryType> getDefaultSlots() {
        return DEFAULT_SLOTS;
    }

    /**
     * Retrieve all known {@link AccessoryType}s
     * @return all accessory types located and stored by the {@link AccessoryTypeManager} at the given instant.
     * It is data-based and so should be empty when not in-world
     */
    public static @NonNull ArrayList<AccessoryType> getTypes() {
        return TYPES_LIST;
    }

    /**
     * Get the {@link AccessoryType} identifier keyset
     * @param referenceableOnly {@code true} to give types where {@link AccessoryType#allowReference()} returns {@code true},
     *                                      and {@code false} to not check and simply give all types
     * @return the backing keyset for the type map
     */
    public static @NonNull Set<Identifier> getTypeIdentifiers(boolean referenceableOnly) {
        if (referenceableOnly) {
            return REFERENCEABLE_TYPES;
        }

        return TYPES.keySet();
    }

    /**
     * Retrieve the default slot types list, composed from {@link AccessoryType#getDefaultSlots()}
     * @return the default accessory slot types list
     */
    public static @NonNull ArrayList<@NonNull String> getDefaultSlotsStringList() {
        return DEFAULT_SLOTS_STRING_LIST;
    }

    /**
     * Retrieve the possible type override on an item
     * @param item the item to query
     * @return the override data as a pair of boolean hard ({@code true}) or soft ({@code false}) override and {@link AccessoryType},
     * or {@code null} if no override is present for the provided {@link Item}
     */
    public static @Nullable BooleanObjectPair<AccessoryType> getTypeOverride(@NonNull Item item) {
        if (accessoryTypeOverrides != null) {
            return accessoryTypeOverrides.get(item);
        }

        return null;
    }
}
