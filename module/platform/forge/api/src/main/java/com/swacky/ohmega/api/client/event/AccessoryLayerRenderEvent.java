package com.swacky.ohmega.api.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NonNull;

public record AccessoryLayerRenderEvent(LivingEntityRenderState state, PoseStack stack) implements RecordEvent, Cancellable {
    public static final CancellableEventBus<@NonNull AccessoryLayerRenderEvent> BUS = CancellableEventBus.create(AccessoryLayerRenderEvent.class);
}
