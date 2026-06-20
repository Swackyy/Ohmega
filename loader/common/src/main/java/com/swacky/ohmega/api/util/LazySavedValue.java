package com.swacky.ohmega.api.util;

import it.unimi.dsi.fastutil.objects.ObjectBooleanBiConsumer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Possibly-boxing templated implementation of {@link AbstractLazySavedValue}
 */
public final class LazySavedValue<T> extends AbstractLazySavedValue<T> {
    private final @Nullable Supplier<T> getter;
    private final @Nullable ObjectBooleanBiConsumer<T> setter;

    private @Nullable T value = null;

    /**
     * Construct a new {@link LazySavedValue}
     * @param getter the initial value supplier
     * @param setter the serialisation value acceptor
     */
    public LazySavedValue(@Nullable Supplier<T> getter, @Nullable ObjectBooleanBiConsumer<T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Construct a new variable-value {@link LazySavedValue}
     * @param value the initial value to set as
     * @return newly constructed instance
     */
    public static <T> @NonNull LazySavedValue<T> of(T value) {
        LazySavedValue<T> instance = new LazySavedValue<>(null, null);
        instance.value = value;

        return instance;
    }

    @Override
    public @Nullable T getObject() {
        if (!initialised) {
            initialised = true;

            if (getter != null) {
                value = getter.get();
            }
        }

        return value;
    }

    @Override
    public void setObject(@Nullable T value) {
        initialised = true;
        this.value = value;
    }

    @Override
    public boolean isSerialisable() {
        return setter != null;
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
     * @param last {@code true} if this is the expected last invocation of this function, {@code false} if not.
     *                         Allows for better optimisation and avoids unwanted {@link #pull()} calls
     */
    public void serialise(boolean last) {
        if (setter != null) {
            setter.accept(value, last);
        }
    }
}
