package com.swacky.ohmega.common.init;

import com.swacky.ohmega.client.OhmegaClient;
import net.minecraft.world.item.Item;

public final class OhmegaItems {
    private static final Service IMPL = OhmegaClient.loadService(Service.class);

    public static void bootstrap() {}

    public static Item getAngelRing() {
        return IMPL.getAngelRing();
    }

    public interface Service {
        Item getAngelRing();
    }
}
