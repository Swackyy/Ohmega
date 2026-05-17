package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.item.AngelRing;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class OhmegaItemsImpl implements OhmegaItems.Service {
    private static final Item ANGEL_RING = register("angel_ring", AngelRing::new, OhmegaItems.getAngelRingProperties());

    private static <T extends Item> T register(String id, Function<Item.Properties, T> function, Item.Properties props) {
        return Registry.register(BuiltInRegistries.ITEM, Ohmega.id(id), function.apply(props.setId(ResourceKey.create(Registries.ITEM, Ohmega.id(id)))));
    }

    @Override
    public Item getAngelRing() {
        return ANGEL_RING;
    }
}
