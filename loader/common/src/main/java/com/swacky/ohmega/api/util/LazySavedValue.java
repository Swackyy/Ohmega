package com.swacky.ohmega.api.util;

import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Possibly-boxing templated implementation of {@link AbstractLazySavedValue}
 */
public final class LazySavedValue<T> extends AbstractLazySavedValue<T> {
    private final @Nullable Supplier<T> getter;
    private final @Nullable Consumer<T> setter;

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

    /**
     * Construct a constant-value {@link LazySavedValue}
     * @param value the constant value to set as
     * @return newly constructed constant instance
     * @param <T> object type to hold as a value
     */
    public static <T> LazySavedValue<T> constant(T value) {
        LazySavedValue<T> instance = new LazySavedValue<>(null, null);
        instance.value = value;

        return instance;
    }

    @Override
    public T getObject() {
        if (!initialised) {
            initialised = true;

            if (getter != null) {
                value = getter.get();
            }
        }

        return value;
    }

    @Override
    public void setObject(T value) {
        initialised = true;
        this.value = value;
    }

    /**
     * Refreshes the cached value with the {@link #getter} if non-null
     */
    public void pull() {
        if (getter != null) {
            value = getter.get();
        }
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
