package com.swacky.ohmega.common.accessorytype;

import com.google.common.collect.ImmutableMap;
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
import java.util.HashMap;
import java.util.Map;

public final class AccessoryTypeManager extends SimplePreparableReloadListener<ImmutableMap<Identifier, AccessoryType>> {
    private static final AccessoryTypeManager INSTANCE = new AccessoryTypeManager();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String LOCATION = Ohmega.MODID + "/accessory_types.json";
    private static final TypeToken<Map<String, AccessoryType.Builder>> TOKEN = new TypeToken<>() {};
    private static final HashMap<Identifier, AccessoryType> TYPES = new HashMap<>();
    private static final int DEFAULT_SIZE = 5;
    private static Map<Item, Pair<AccessoryType, Boolean>> ACCESSORY_TYPE_OVERRIDES;
    private static Runnable DEFERRED_APPLY = null;
    private static Runnable DEFERRED_CONFIG_LOAD = null;

    private AccessoryTypeManager() {}

    public static AccessoryTypeManager getInstance() {
        return INSTANCE;
    }

    @Override
    protected @NonNull ImmutableMap<Identifier, AccessoryType> prepare(@NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        ImmutableMap.Builder<Identifier, AccessoryType> builder = ImmutableMap.builderWithExpectedSize(DEFAULT_SIZE);

        for (String namespace : manager.getNamespaces()) {
            for (Resource resource : manager.getResourceStack(Identifier.fromNamespaceAndPath(namespace, LOCATION))) {
                try (Reader reader = resource.openAsReader()) {
                    Map<String, AccessoryType.Builder> map = GsonHelper.fromJson(AccessoryType.Deserializer.GSON, reader, TOKEN);

                    for (Map.Entry<String, AccessoryType.Builder> entry : map.entrySet()) {
                        AccessoryType type = entry.getValue().build(namespace, entry.getKey());

                        builder.put(type.getId(), type);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Could not read '{}' in DataPack: '{}'", LOCATION, resource.sourcePackId(), e);
                }
            }
        }

        return builder.build();
    }

    private static void apply(ImmutableMap<Identifier, AccessoryType> types) {
        TYPES.clear();
        TYPES.put(AccessoryType.NONE.getId(), AccessoryType.NONE);
        TYPES.putAll(types);
        TYPES.putAll(OhmegaHooks.registerAccessoryTypesEvent());

        ACCESSORY_TYPE_OVERRIDES = OhmegaHooks.accessoryOverrideTypesEvent();

        if (DEFERRED_APPLY != null) {
            if (OhmegaConfig.Server.isLoaded()) {
                DEFERRED_APPLY.run();
            } else {
                DEFERRED_CONFIG_LOAD = DEFERRED_APPLY;
            }

            DEFERRED_APPLY = null;
        }

        OhmegaTags.refresh();
    }

    public static void apply(ImmutableSet<AccessoryType> types) {
        ImmutableMap.Builder<Identifier, AccessoryType> builder = ImmutableMap.builderWithExpectedSize(types.size());

        for (AccessoryType type : types) {
            builder.put(type.getId(), type);
        }

        apply(builder.build());
    }

    @Override
    protected void apply(@NonNull ImmutableMap<Identifier, AccessoryType> types, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profiler) {
        apply(types);
    }

    public static void applyClient(Runnable onConfigLoad, boolean shouldDefer) {
        if (shouldDefer) {
            DEFERRED_CONFIG_LOAD = onConfigLoad;
        } else {
            onConfigLoad.run();
        }
    }

    public static void clear() {
        TYPES.clear();
        ACCESSORY_TYPE_OVERRIDES.clear();
    }

    public static void deferApply(Runnable runnable) {
        DEFERRED_APPLY = runnable;
    }

    public static void runDeferredAwaitingConfigLoad() {
        if (DEFERRED_CONFIG_LOAD != null) {
            DEFERRED_CONFIG_LOAD.run();
            DEFERRED_CONFIG_LOAD = null;
        }
    }

    public static @Nullable Pair<AccessoryType, Boolean> getTypeOverride(Item item) {
        return ACCESSORY_TYPE_OVERRIDES.get(item);
    }

    public static ImmutableSet<AccessoryType> getTypes() {
        return ImmutableSet.copyOf(TYPES.values());
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
