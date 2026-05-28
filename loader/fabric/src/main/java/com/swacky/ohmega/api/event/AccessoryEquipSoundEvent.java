package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.common.item.SoundData;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryEquipSoundEvent {
    Event<AccessoryEquipSoundEvent> EVENT = EventFactory.createArrayBacked(AccessoryEquipSoundEvent.class,
        listeners -> (stack, returnValue) -> {
            for (AccessoryEquipSoundEvent listener : listeners) {
                returnValue = listener.process(stack, returnValue);
            }

            return returnValue;
        }
    );

    SoundData process(ItemStack stack, SoundData original);
}
