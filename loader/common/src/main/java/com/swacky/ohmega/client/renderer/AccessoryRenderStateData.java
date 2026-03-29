package com.swacky.ohmega.client.renderer;

import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record AccessoryRenderStateData(NonNullList<ItemStack> stacks) {
    private static final Service IMPL = OhmegaClient.loadService(Service.class);

    public static void bootstrap() {}

    public static AccessoryRenderStateData getData(LivingEntityRenderState state) {
        return IMPL.getData(state);
    }

    public interface Service {
        Identifier ID = Ohmega.id("accessory_data");

        AccessoryRenderStateData getData(LivingEntityRenderState state);
    }
}
