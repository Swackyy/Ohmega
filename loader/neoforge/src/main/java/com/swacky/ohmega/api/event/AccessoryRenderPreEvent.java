package com.swacky.ohmega.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public final class AccessoryRenderPreEvent extends Event implements ICancellableEvent {
    public final LivingEntityRenderState state;
    public final PoseStack stack;

    public AccessoryRenderPreEvent(LivingEntityRenderState state, PoseStack stack) {
        this.state = state;
        this.stack = stack;
    }
}
