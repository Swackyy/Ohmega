package com.swacky.ohmega.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jspecify.annotations.NonNull;

public record AccessoryRenderPreEvent(LivingEntityRenderState state, PoseStack stack) implements RecordEvent, Cancellable {
    public static final CancellableEventBus<@NonNull AccessoryRenderPreEvent> BUS = CancellableEventBus.create(AccessoryRenderPreEvent.class);
}
