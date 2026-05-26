package com.swacky.ohmega.api.util;

import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Implements a 2-way lazy saved value scheme, allowing for lazily fetching an initial value and having deferred serialisation
 * @param <T> the value type to store
 */
// todo: create primitive implementations that avoid 64 bit boxing
public final class LazySavedValue<T> {
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private boolean initialised = false;
    private T value = null;

    /**
     * Construct a new {@link LazySavedValue}
     * @param getter the initial value supplier
     * @param setter the serialisation value acceptor
     */
    public LazySavedValue(@Nullable Supplier<T> getter, @Nullable Consumer<T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public static <T> LazySavedValue<T> constant(T value) {
        LazySavedValue<T> instance = new LazySavedValue<>(null, null);
        instance.value = value;

        return instance;
    }

    /**
     * Lazily get the stored value
     * @return the current value if it has already been fetched, else calls the {@link #getter} to initialise to the stored or default value
     */
    public T get() {
        if (!initialised) {
            initialised = true;

            if (getter != null) {
                value = getter.get();
            }
        }

        return value;
    }

    /**
     * Refreshes the cached value with the {@link #getter}
     */
    public void pull() {
        if (getter != null) {
            value = getter.get();
        }
    }

    /**
     * Sets a value but strictly does not call the serialiser
     * @param value the new value to set to
     */
    public void set(T value) {
        initialised = true;
        this.value = value;
    }

    /**
     * Calls the serialiser with the currently stored value in memory
     */
    public void serialise() {
        if (setter != null) {
            setter.accept(value);
        }
    }
}
