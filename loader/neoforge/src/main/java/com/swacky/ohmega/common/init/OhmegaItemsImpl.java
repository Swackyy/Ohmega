package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.item.AngelRing;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public final class OhmegaItemsImpl implements OhmegaItems.Service {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Ohmega.MODID);

    private static final DeferredItem<@NonNull Item> ANGEL_RING = register("angel_ring", AngelRing::new, new Item.Properties().stacksTo(1));

    private static <T extends Item> DeferredItem<T> register(String id, Function<Item.Properties, T> function, Item.Properties props) {
        return ITEMS.registerItem(id, function, () -> props);
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    @Override
    public Item getAngelRing() {
        return ANGEL_RING.value();
    }
}
