package com.swacky.ohmega.api.common.accessorytype;

import com.google.gson.reflect.TypeToken;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaTags;
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
import java.util.List;
import java.util.Map;
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
    private static final @NonNull TypeToken<Map<String, AccessoryType.Builder>> TOKEN = new TypeToken<>() {};
    private static final @NonNull Map<Identifier, AccessoryType> TYPES = new HashMap<>();
    private static final @NonNull List<Runnable> DEFERRED_APPLY = new ArrayList<>();
    private static final @NonNull List<Runnable> DEFERRED_CONFIG_LOAD = new ArrayList<>();

    private static @Nullable Map<Item, BooleanObjectPair<AccessoryType>> ACCESSORY_TYPE_OVERRIDES;

    private AccessoryTypeManager() {}

    /**
     * Retrieve the singleton instance of the accessory type manager.
     * You shouldn't need to use this as all methods are {@code static}
     * @return the singleton {@link AccessoryTypeManager} instance
     */
    public static @NonNull AccessoryTypeManager getInstance() {
        return INSTANCE;
    }

    @Override
    protected @NonNull Map<Identifier, AccessoryType> prepare(@NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        Map<Identifier, AccessoryType> map = new HashMap<>(5);
        Set<String> namespaces = manager.getNamespaces();

        int namespaceCount = 0;

        for (String namespace : namespaces) {
            int typeCount = 0;

            for (Resource resource : manager.getResourceStack(Identifier.fromNamespaceAndPath(namespace, LOCATION))) {
                try (Reader reader = resource.openAsReader()) {
                    Map<String, AccessoryType.Builder> jsonMap = GsonHelper.fromJson(AccessoryType.Deserializer.GSON, reader, TOKEN);

                    for (Map.Entry<String, AccessoryType.Builder> entry : jsonMap.entrySet()) {
                        AccessoryType type = entry.getValue().build(namespace, entry.getKey());

                        map.put(type.getId(), type);

                        if (typeCount++ == 0) {
                            namespaceCount++;
                        }
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

    private static void apply(@NonNull Map<Identifier, AccessoryType> types) {
        TYPES.clear();
        TYPES.put(AccessoryType.NONE.getId(), AccessoryType.NONE);
        TYPES.putAll(types);
        TYPES.putAll(OhmegaHooks.registerAccessoryTypes());

        ACCESSORY_TYPE_OVERRIDES = OhmegaHooks.overrideTypes();

        if (!DEFERRED_APPLY.isEmpty()) {
            if (OhmegaConfig.Server.isLoaded()) {
                DEFERRED_APPLY.forEach(Runnable::run);
            } else {
                DEFERRED_CONFIG_LOAD.addAll(DEFERRED_APPLY);
            }

            DEFERRED_APPLY.clear();
        }

        OhmegaTags.refresh();
    }

    public static void apply(@NonNull Collection<AccessoryType> types) {
        Map<Identifier, AccessoryType> map = new HashMap<>(types.size());

        for (AccessoryType type : types) {
            map.put(type.getId(), type);
        }

        apply(map);
    }


    @Override
    protected void apply(@NonNull Map<Identifier, AccessoryType> types, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profiler) {
        apply(types);
    }

    public static void applyClient(Runnable onConfigLoad, boolean shouldDefer) {
        if (shouldDefer) {
            DEFERRED_CONFIG_LOAD.add(onConfigLoad);
        } else {
            onConfigLoad.run();
        }
    }

    public static void clear() {
        TYPES.clear();
        ACCESSORY_TYPE_OVERRIDES = null;
    }

    public static void deferApply(@NonNull Runnable runnable) {
        DEFERRED_APPLY.add(runnable);
    }

    public static void runDeferredAwaitingConfigLoad() {
        DEFERRED_CONFIG_LOAD.forEach(Runnable::run);
        DEFERRED_CONFIG_LOAD.clear();
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
    public static @NonNull Collection<AccessoryType> getTypes() {
        return TYPES.values();
    }

    /**
     * Get the {@link AccessoryType} identifier keyset
     * @return the backing keyset for the type map
     */
    public static @NonNull Set<Identifier> getTypeIdentifiers() {
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
