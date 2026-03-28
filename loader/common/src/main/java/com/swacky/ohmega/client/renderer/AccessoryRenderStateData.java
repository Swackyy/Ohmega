package com.swacky.ohmega.client.renderer;

import com.swacky.ohmega.client.OhmegaClient;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record AccessoryRenderStateData(NonNullList<ItemStack> stacks, LivingEntity entity) {
    private static final Service IMPL = OhmegaClient.loadService(Service.class);

    public static void bootstrap() {}

    public static AccessoryRenderStateData getData(LivingEntityRenderState state) {
        return IMPL.getData(state);
    }

    public interface Service {
        AccessoryRenderStateData getData(LivingEntityRenderState state);
    }
}
