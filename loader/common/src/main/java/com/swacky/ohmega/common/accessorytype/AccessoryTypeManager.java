package com.swacky.ohmega.common.accessorytype;

import com.google.common.collect.ImmutableSet;
import com.google.gson.reflect.TypeToken;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaTags;
import com.swacky.ohmega.event.OhmegaHooks;
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
import java.util.HashSet;
import java.util.Map;

public final class AccessoryTypeManager extends SimplePreparableReloadListener<ImmutableSet<AccessoryType>> {
    private static final AccessoryTypeManager INSTANCE = new AccessoryTypeManager();
    private static final int DEFAULT_SIZE = 4;
    public static final String LOCATION = OhmegaCommon.MODID + "/accessory_types.json";
    private static final TypeToken<Map<String, AccessoryType.Builder>> TOKEN = new TypeToken<>() {};
    private static final HashSet<AccessoryType> TYPES = new HashSet<>();
    private static Map<Item, AccessoryType> ACCESSORY_TYPE_OVERRIDES;

    // Both of these are required as the configs are not guaranteed to load before dependent actions have been carried out.
    private static Runnable DEFERRED_APPLY = null;
    private static Runnable DEFERRED_CONFIG_LOAD = null;


    private AccessoryTypeManager() {}

    public static AccessoryTypeManager getInstance() {
        return INSTANCE;
    }

    @Override
    protected @NonNull ImmutableSet<AccessoryType> prepare(@NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        ImmutableSet.Builder<AccessoryType> builder = ImmutableSet.builderWithExpectedSize(DEFAULT_SIZE);

        for (String namespace : manager.getNamespaces()) {
            for (Resource resource : manager.getResourceStack(Identifier.fromNamespaceAndPath(namespace, LOCATION))) {
                try (Reader reader = resource.openAsReader()) {
                    Map<String, AccessoryType.Builder> map = GsonHelper.fromJson(AccessoryType.Deserializer.GSON, reader, TOKEN);

                    for (Map.Entry<String, AccessoryType.Builder> entry : map.entrySet()) {
                        builder.add(entry.getValue().build(namespace, entry.getKey()));
                    }
                } catch (Exception e) {
                    OhmegaCommon.LOGGER.warn("Could not read '{}' in DataPack: '{}'", LOCATION, resource.sourcePackId(), e);
                }
            }
        }

        return builder.addAll(OhmegaHooks.registerAccessoryTypesEvent()).build();
    }

    public static void apply(ImmutableSet<AccessoryType> types) {
        TYPES.clear();
        TYPES.addAll(types);

        if (DEFERRED_APPLY != null) {
            DEFERRED_APPLY.run();
            DEFERRED_APPLY = null;
        }

        ACCESSORY_TYPE_OVERRIDES = OhmegaHooks.accessoryOverrideTypesEvent();

        OhmegaTags.refresh();
    }

    @Override
    protected void apply(@NonNull ImmutableSet<AccessoryType> types, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profiler) {
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

    public static @Nullable AccessoryType getTypeOverride(Item item) {
        return ACCESSORY_TYPE_OVERRIDES.get(item);
    }

    public static ImmutableSet<AccessoryType> getTypes() {
        return ImmutableSet.copyOf(TYPES);
    }

    public static @NonNull AccessoryType get(Identifier id) {
        for (AccessoryType type : TYPES) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        return AccessoryType.NORMAL.get();
    }

    public static boolean exists(String id) {
        for (AccessoryType type : TYPES) {
            if (type.getId().equals(Identifier.parse(id))) {
                return true;
            }
        }

        return false;
    }
}
