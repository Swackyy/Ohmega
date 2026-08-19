package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.api.util.IntLazySavedValue;
import org.jspecify.annotations.NonNull;

/**
 * Represents a general position with mutable {@code x} and {@code y} component,
 * held in the form of {@link IntLazySavedValue}s to allow for serialisation
 * @param x {@link IntLazySavedValue} instance for the x-coordinate
 * @param y {@link IntLazySavedValue} instance for the y-coordinate
 */
public record LazyPosition(@NonNull IntLazySavedValue x, @NonNull IntLazySavedValue y) {
    /**
     * Constructs a new {@link LazyPosition} with non-serialisable {@code x} and {@code y} values
     * @param x initial x value
     * @param y initial y value
     */
    public LazyPosition(int x, int y) {
        this(IntLazySavedValue.of(x), IntLazySavedValue.of(y));
    }

    public void set(int x, int y) {
        this.x.set(x);
        this.y.set(y);
    }

    /**
     * Check if both the {@link #x} and {@link #y} can be serialised
     * @return {@code true} if both the values are serialisable, {@code false} otherwise
     */
    public boolean isSerialisable() {
        return x.isSerialisable() && y.isSerialisable();
    }
}
