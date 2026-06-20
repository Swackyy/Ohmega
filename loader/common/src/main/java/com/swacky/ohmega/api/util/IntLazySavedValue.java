package com.swacky.ohmega.api.util;

import it.unimi.dsi.fastutil.ints.IntBooleanBiConsumer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.IntSupplier;

/**
 * Non-boxing integer implementation of {@link AbstractLazySavedValue}
 */
public class IntLazySavedValue extends AbstractLazySavedValue<Integer> {
    private final @Nullable IntSupplier getter;
    private final @Nullable IntBooleanBiConsumer setter;

    private int value;

    /**
     * Construct a new {@link IntLazySavedValue}
     * @param getter the initial value supplier
     * @param setter the serialisation value acceptor
     */
    public IntLazySavedValue(@Nullable IntSupplier getter, @Nullable IntBooleanBiConsumer setter) {
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Construct a new variable-value {@link IntLazySavedValue}
     * @param value the initial value to set as
     * @return newly constructed instance
     */
    public static @NonNull IntLazySavedValue of(int value) {
        IntLazySavedValue instance = new IntLazySavedValue(null, null);
        instance.value = value;

        return instance;
    }

    @Override
    public @NonNull Integer getObject() {
        return get();
    }

    @Override
    public void setObject(@Nullable Integer value) {
        if (value != null) {
            set(value);
        }
    }

    @Override
    public boolean isSerialisable() {
        return setter != null;
    }

    /**
     * Lazily get the stored value, non-boxed
     * @return the current value if it has already been fetched, else calls the {@link #getter} to initialise to the stored or default value
     */
    public int get() {
        if (!initialised) {
            initialised = true;

            if (getter != null) {
                value = getter.getAsInt();
            }
        }

        return value;
    }

    /**
     * Sets a value but strictly does not call the serialiser, non-boxed
     * @param value the new value to set to
     */
    public void set(int value) {
        initialised = true;
        this.value = value;
    }

    /**
     * Refreshes the cached value with the {@link #getter} if non-null
     */
    public void pull() {
        if (getter != null) {
            value = getter.getAsInt();
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
