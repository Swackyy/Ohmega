package com.swacky.ohmega.api.event;

import it.unimi.dsi.fastutil.booleans.BooleanBooleanPair;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface AccessoryUseEvent {
    Event<AccessoryUseEvent> EVENT = EventFactory.createArrayBacked(AccessoryUseEvent.class,
        listeners -> (entity, stack) -> {
            boolean shouldNotifyServer = false;

            for (AccessoryUseEvent listener : listeners) {
                BooleanBooleanPair pair = listener.process(entity, stack);

                if (pair.firstBoolean()) {
                    return pair;
                }

                if (pair.secondBoolean()) {
                    shouldNotifyServer = true;
                }
            }

            return BooleanBooleanPair.of(false, shouldNotifyServer);
        }
    );

    /**
     * @return a boolean pair of which elements correspond with:
     * <ul>
     *     <li>first: {@code true} for cancelling the event, {@code false} to let it continue</li>
     *     <li>
     *         second: {@code true} to synchronise this with the server and other clients, {@code false} otherwise.
     *         This will be {@code true} if <strong>any</strong> listener decides to synchronise, not just the last one to be processed
     *     </li>
     * </ul>
     */
    BooleanBooleanPair process(LivingEntity entity, ItemStack stack);
}
