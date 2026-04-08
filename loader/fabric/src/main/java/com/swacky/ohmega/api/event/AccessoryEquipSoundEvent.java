package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.SoundData;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface AccessoryEquipSoundEvent {
    Event<AccessoryEquipSoundEvent> EVENT = EventFactory.createArrayBacked(AccessoryEquipSoundEvent.class,
        listeners -> (stack, ret) -> {
            for (AccessoryEquipSoundEvent listener : listeners) {
                ret = listener.process(stack, ret);
            }

            return ret;
        }
    );

    SoundData process(ItemStack stack, SoundData original);
}
