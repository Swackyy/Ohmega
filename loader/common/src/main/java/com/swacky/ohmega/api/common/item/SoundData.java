package com.swacky.ohmega.api.common.item;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public record SoundData(Holder<SoundEvent> sound, float volume, float pitch) {
    public SoundData(Holder<SoundEvent> sound) {
        this(sound, 1, 1);
    }
}
