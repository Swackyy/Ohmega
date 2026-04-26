package com.swacky.ohmega.api.client.renderer;

import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.common.item.Accessory;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class AccessoryRenderers {
    private static final Map<Accessory, RendererFactory> RENDERERS = new HashMap<>();
    private static final Map<Accessory, Predicate<EntityType<?>>> NO_RENDERS = new HashMap<>();

    private static void register(Accessory key, RendererFactory factory) {
        RENDERERS.put(key, factory);
    }

    private static boolean registerNoRender(Accessory key, Predicate<EntityType<?>> type) {
        if (!NO_RENDERS.containsKey(key)) {
            NO_RENDERS.put(key, type);
            return true;
        }

        return false;
    }

    public static void register(Item item, RendererFactory factory) {
        register(Accessories.get(item), factory);
    }

    public static boolean registerNoRender(Item item, Predicate<EntityType<?>> type) {
        return registerNoRender(Accessories.get(item), type);
    }

    public static boolean registerNoRender(Item item) {
        return registerNoRender(item, _ -> true);
    }

    public static RendererFactory getFactoryFor(Accessory key) {
        return RENDERERS.get(key);
    }

    public static boolean isNoRender(Accessory key, EntityType<?> type) {
        if (NO_RENDERS.containsKey(key)) {
            return NO_RENDERS.get(key).test(type);
        }

        return false;
    }

    public interface RendererFactory {
        IAccessoryRenderer create(EntityRendererProvider.Context context);
    }
}
