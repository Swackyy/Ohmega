package com.swacky.ohmega.api.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public interface AccessoryLayerRenderEvent {
    Event<AccessoryLayerRenderEvent> EVENT = EventFactory.createArrayBacked(AccessoryLayerRenderEvent.class,
        listeners -> (state, stack) -> {
            for (AccessoryLayerRenderEvent listener : listeners) {
                if (listener.process(state, stack)) {
                    return true;
                }
            }

            return false;
        }
    );

    boolean process(LivingEntityRenderState state, PoseStack stack);
}
