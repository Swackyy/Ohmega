package com.swacky.ohmega.api.common.item;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import org.jspecify.annotations.NonNull;

/**
 * A more easily passable wrapper for sound data
 * @param sound the sound to play
 * @param volume the volume modifier of the sound
 * @param pitch the pitch modifier of the sound
 */
public record SoundData(@NonNull Holder<SoundEvent> sound, float volume, float pitch) {
    /**
     * Creates a new instance with default volume and pitch ({@code 1})
     * @param sound the sound to play
     */
    public SoundData(@NonNull Holder<SoundEvent> sound) {
        this(sound, 1, 1);
    }
}
