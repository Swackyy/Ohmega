package com.swacky.ohmega.api.client.renderer;

import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class AccessoryRenderers {
    private static final @NonNull Map<Accessory, IHumanoidAccessoryRenderer.Factory> HUMANOID_RENDERERS = new IdentityHashMap<>();
    private static final @NonNull Map<Accessory, ILivingAccessoryRenderer.Factory> LIVING_RENDERERS = new IdentityHashMap<>();
    private static final @NonNull Map<Accessory, Predicate<EntityType<?>>> NO_RENDERS = new IdentityHashMap<>();

    private static boolean registerNoRender(@NonNull Accessory key, @NonNull Predicate<EntityType<?>> type) {
        if (!NO_RENDERS.containsKey(key)) {
            NO_RENDERS.put(key, type);
            return true;
        }

        return false;
    }

    public static void registerHumanoid(@NonNull Item item, IHumanoidAccessoryRenderer.@NonNull Factory factory) {
        Accessory key = Accessories.get(item);

        LIVING_RENDERERS.remove(key);
        HUMANOID_RENDERERS.put(key, factory);
    }

    public static void registerLiving(@NonNull Item item, ILivingAccessoryRenderer.@NonNull Factory factory) {
        Accessory key = Accessories.get(item);

        HUMANOID_RENDERERS.remove(key);
        LIVING_RENDERERS.put(key, factory);
    }

    public static boolean registerNoRender(@NonNull Item item, @NonNull Predicate<EntityType<?>> type) {
        Accessory accessory = Accessories.get(item);

        if (accessory != null) {
            return registerNoRender(accessory, type);
        }

        return false;
    }

    public static boolean registerNoRender(@NonNull Item item) {
        return registerNoRender(item, _ -> true);
    }

    public static IHumanoidAccessoryRenderer.@Nullable Factory getHumanoidFactory(@NonNull Accessory key) {
        return HUMANOID_RENDERERS.get(key);
    }

    public static ILivingAccessoryRenderer.@Nullable Factory getLivingFactory(@NonNull Accessory key) {
        return LIVING_RENDERERS.get(key);
    }

    public static boolean isNoRender(@NonNull Accessory key, @NonNull EntityType<?> type) {
        if (NO_RENDERS.containsKey(key)) {
            return NO_RENDERS.get(key).test(type);
        }

        return false;
    }
}
