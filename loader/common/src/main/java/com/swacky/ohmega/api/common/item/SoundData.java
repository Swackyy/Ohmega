package com.swacky.ohmega.api.common.item;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import org.jspecify.annotations.NonNull;

public record SoundData(@NonNull Holder<SoundEvent> sound, float volume, float pitch) {
    public SoundData(@NonNull Holder<SoundEvent> sound) {
        this(sound, 1, 1);
    }
}
