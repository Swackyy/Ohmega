package com.swacky.ohmega.api.common.accessorytype;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaTags;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Data holder class for {@link AccessoryType}s, a resource listener that will fetch types from found JSONs
 * <p>
 * Types are stored as a {@link HashMap} with their {@link Identifier}s as the keys to reduce fetch time
 */
public final class AccessoryTypeManager extends SimplePreparableReloadListener<Map<Identifier, AccessoryType>> {
    private static final @NonNull AccessoryTypeManager INSTANCE = new AccessoryTypeManager();
    private static final @NonNull Logger LOGGER = LogManager.getLogger();
    public static final @NonNull String LOCATION = Ohmega.MODID + "/accessory_types.json";
    private static final @NonNull Map<Identifier, AccessoryType> TYPES = new HashMap<>();
    private static final @NonNull Set<Identifier> REFERENCEABLE_TYPES = new HashSet<>();
    private static final @NonNull List<Runnable> APPLY_TASKS = new ArrayList<>();
    private static final @NonNull List<Runnable> CONFIG_LOAD_TASKS = new ArrayList<>();
    private static final ThreadLocal<Boolean> ALLOW_POST_EVENTS = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private static @Nullable Map<Item, BooleanObjectPair<AccessoryType>> ACCESSORY_TYPE_OVERRIDES;

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
        ACCESSORY_TYPE_OVERRIDES = OhmegaHooks.overrideTypes();
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

        for (String namespace : namespaces) {
            int typeCount = 0;

            for (Resource resource : manager.getResourceStack(Identifier.fromNamespaceAndPath(namespace, LOCATION))) {
                try (Reader reader = resource.openAsReader()) {
                    Optional<Pair<Map<String, AccessoryType.Builder>, JsonElement>> opt = AccessoryType.Builder.MAP_CODEC.decode(
                            JsonOps.INSTANCE,
                            GsonHelper.parse(reader)
                    ).resultOrPartial(message -> LOGGER.warn("Could not completely parse JSON '{}' in DataPack '{}', message:{}",
                            LOCATION,
                            resource.sourcePackId(),
                            message));

                    if (opt.isPresent()) {
                        for (Map.Entry<String, AccessoryType.Builder> entry : opt.get().getFirst().entrySet()) {
                            AccessoryType type = entry.getValue().build(namespace, entry.getKey());

                            map.put(type.getId(), type);

                            if (typeCount++ == 0) {
                                namespaceCount++;
                            }
                        }
                    } else {
                        LOGGER.warn("Could not decode JSON '{}' in DataPack: '{}'", LOCATION, resource.sourcePackId());
                    }
                } catch (Exception e) {
                    LOGGER.warn("Could not read '{}' in DataPack: '{}'", LOCATION, resource.sourcePackId(), e);
                }
            }

            if (typeCount > 0) {
                LOGGER.debug("Loaded {} accessory type(s) from namespace '{}'", typeCount, namespace);
            }
        }

        LOGGER.info("Loaded {} accessory type(s) from {} namespace(s)", map.size(), namespaceCount);
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

        for (AccessoryType type : TYPES.values()) {
            if (type.allowReference()) {
                REFERENCEABLE_TYPES.add(type.getId());
            }
        }

        if (ALLOW_POST_EVENTS.get()) {
            postOverrideTypes();
        }

        if (!APPLY_TASKS.isEmpty()) {
            if (OhmegaConfig.Server.isLoaded()) {
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
        lockEvents();

        ACCESSORY_TYPE_OVERRIDES = null;
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
     * Retrieve the possible type override on an item
     * @param item the item to query
     * @return the override data as a pair of boolean hard ({@code true}) or soft ({@code false}) override and {@link AccessoryType},
     * or {@code null} if no override is present for the provided {@link Item}
     */
    public static @Nullable BooleanObjectPair<AccessoryType> getTypeOverride(@NonNull Item item) {
        if (ACCESSORY_TYPE_OVERRIDES != null) {
            return ACCESSORY_TYPE_OVERRIDES.get(item);
        }

        return null;
    }

    /**
     * Retrieve all known {@link AccessoryType}s
     * @return all accessory types located and stored by the {@link AccessoryTypeManager} at the given instant.
     * It is data-based and so should be empty when not in-world
     */
    public static @NonNull List<AccessoryType> getTypes() {
        return new ArrayList<>(TYPES.values());
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
}
