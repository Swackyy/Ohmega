package com.swacky.ohmega.common.accessorytype;

import com.google.common.collect.ImmutableSet;
import com.google.gson.reflect.TypeToken;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaTags;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.tuple.Pair;
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

public final class AccessoryTypeManager extends SimplePreparableReloadListener<Map<Identifier, AccessoryType>> {
    private static final AccessoryTypeManager INSTANCE = new AccessoryTypeManager();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String LOCATION = Ohmega.MODID + "/accessory_types.json";
    private static final TypeToken<Map<String, AccessoryType.Builder>> TOKEN = new TypeToken<>() {};
    private static final Map<Identifier, AccessoryType> TYPES = new HashMap<>();
    private static final List<Runnable> DEFERRED_APPLY = new ArrayList<>();
    private static final List<Runnable> DEFERRED_CONFIG_LOAD = new ArrayList<>();

    private static Map<Item, Pair<AccessoryType, Boolean>> ACCESSORY_TYPE_OVERRIDES;

    private AccessoryTypeManager() {}

    public static AccessoryTypeManager getInstance() {
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

    private static void apply(Map<Identifier, AccessoryType> types) {
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

    public static void apply(Collection<AccessoryType> types) {
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

    public static void deferApply(Runnable runnable) {
        DEFERRED_APPLY.add(runnable);
    }

    public static void runDeferredAwaitingConfigLoad() {
        if (DEFERRED_CONFIG_LOAD != null) {
            DEFERRED_CONFIG_LOAD.forEach(Runnable::run);
            DEFERRED_CONFIG_LOAD.clear();
        }
    }

    public static @Nullable Pair<AccessoryType, Boolean> getTypeOverride(Item item) {
        if (ACCESSORY_TYPE_OVERRIDES != null) {
            return ACCESSORY_TYPE_OVERRIDES.get(item);
        }

        return null;
    }

    public static Collection<AccessoryType> getTypes() {
        return TYPES.values();
    }

    public static ImmutableSet<Identifier> getTypeIdentifiers() {
        return ImmutableSet.copyOf(TYPES.keySet());
    }

    public static @NonNull AccessoryType get(Identifier id) {
        AccessoryType candidate = TYPES.get(id);

        if (candidate != null) {
            return candidate;
        }

        return AccessoryType.NONE;
    }

    public static boolean exists(Identifier id) {
        if (id != null) {
            return TYPES.containsKey(id);
        }

        return false;
    }
}
