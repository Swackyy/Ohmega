package com.swacky.ohmega.api.client.renderer;

import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.common.item.Accessory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class AccessoryRenderers {
    private static final Map<Accessory, IHumanoidAccessoryRenderer.Factory> HUMANOID_RENDERERS = new HashMap<>();
    private static final Map<Accessory, ILivingAccessoryRenderer.Factory> LIVING_RENDERERS = new HashMap<>();
    private static final Map<Accessory, Predicate<EntityType<?>>> NO_RENDERS = new HashMap<>();

    private static boolean registerNoRender(Accessory key, Predicate<EntityType<?>> type) {
        if (!NO_RENDERS.containsKey(key)) {
            NO_RENDERS.put(key, type);
            return true;
        }

        return false;
    }

    public static void registerHumanoid(Item item, IHumanoidAccessoryRenderer.Factory factory) {
        Accessory key = Accessories.get(item);

        LIVING_RENDERERS.remove(key);
        HUMANOID_RENDERERS.put(key, factory);
    }

    public static void registerLiving(Item item, ILivingAccessoryRenderer.Factory factory) {
        Accessory key = Accessories.get(item);

        HUMANOID_RENDERERS.remove(key);
        LIVING_RENDERERS.put(key, factory);
    }

    public static boolean registerNoRender(Item item, Predicate<EntityType<?>> type) {
        return registerNoRender(Accessories.get(item), type);
    }

    public static boolean registerNoRender(Item item) {
        return registerNoRender(item, _ -> true);
    }

    public static IHumanoidAccessoryRenderer.@Nullable Factory getHumanoidFactory(Accessory key) {
        return HUMANOID_RENDERERS.get(key);
    }

    public static ILivingAccessoryRenderer.@Nullable Factory getLivingFactory(Accessory key) {
        return LIVING_RENDERERS.get(key);
    }

    public static boolean isNoRender(Accessory key, EntityType<?> type) {
        if (NO_RENDERS.containsKey(key)) {
            return NO_RENDERS.get(key).test(type);
        }

        return false;
    }
}
