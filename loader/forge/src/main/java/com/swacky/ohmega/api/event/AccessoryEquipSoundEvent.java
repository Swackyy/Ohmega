package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.common.item.SoundData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jspecify.annotations.NonNull;

public final class AccessoryEquipSoundEvent extends MutableEvent {
    public static final EventBus<@NonNull AccessoryEquipSoundEvent> BUS = EventBus.create(AccessoryEquipSoundEvent.class);

    public final ItemStack stack;
    public SoundData returnValue;

    public AccessoryEquipSoundEvent(ItemStack stack, SoundData original) {
        this.stack = stack;
        this.returnValue = original;
    }
}
