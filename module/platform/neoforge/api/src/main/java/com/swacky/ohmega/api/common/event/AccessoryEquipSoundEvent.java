package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.item.SoundData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class AccessoryEquipSoundEvent extends Event {
    public final ItemStack stack;
    public SoundData returnValue;

    public AccessoryEquipSoundEvent(ItemStack stack, SoundData original) {
        this.stack = stack;
        this.returnValue = original;
    }
}
