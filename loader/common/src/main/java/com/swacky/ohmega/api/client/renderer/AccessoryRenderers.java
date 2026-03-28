package com.swacky.ohmega.api.client.renderer;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class AccessoryRenderers {
    private static final Map<IAccessory, Function<EntityRendererProvider.Context, IAccessoryRenderer>> RENDERERS = new HashMap<>();

    public static boolean register(IAccessory accessory, Function<EntityRendererProvider.Context, IAccessoryRenderer> factory) {
        if (!RENDERERS.containsKey(accessory)) {
            RENDERERS.put(accessory, factory);
            return true;
        }

        return false;
    }

    public static boolean register(Item item, Function<EntityRendererProvider.Context, IAccessoryRenderer> factory) {
        return register(AccessoryHelper.getBoundAccessory(item), factory);
    }

    public static IAccessoryRenderer getRendererFor(IAccessory accessory, EntityRendererProvider.Context context) {
        return RENDERERS.get(accessory).apply(context);
    }

    public static IAccessoryRenderer getRendererFor(Item item, EntityRendererProvider.Context context) {
        return getRendererFor(AccessoryHelper.getBoundAccessory(item), context);
    }
}
