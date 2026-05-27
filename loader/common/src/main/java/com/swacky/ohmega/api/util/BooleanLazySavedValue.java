package com.swacky.ohmega.api.util;

import org.apache.commons.lang3.function.BooleanConsumer;
import org.jspecify.annotations.Nullable;

import java.util.function.BooleanSupplier;

/**
 * Non-boxing boolean implementation of {@link AbstractLazySavedValue}
 */
public class BooleanLazySavedValue extends AbstractLazySavedValue<Boolean> {
    private final BooleanSupplier getter;
    private final BooleanConsumer setter;

    private boolean value;

    /**
     * Construct a new {@link BooleanLazySavedValue}
     * @param getter the initial value supplier
     * @param setter the serialisation value acceptor
     */
    public BooleanLazySavedValue(@Nullable BooleanSupplier getter, @Nullable BooleanConsumer setter) {
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Construct a constant-value {@link BooleanLazySavedValue}
     * @param value the constant value to set as
     * @return newly constructed constant instance
     */
    public static BooleanLazySavedValue constant(boolean value) {
        BooleanLazySavedValue instance = new BooleanLazySavedValue(null, null);
        instance.value = value;

        return instance;
    }

    @Override
    public Boolean getObject() {
        return get();
    }

    @Override
    public void setObject(Boolean value) {
        set(value);
    }

    /**
     * Lazily get the stored value, non-boxed
     * @return the current value if it has already been fetched, else calls the {@link #getter} to initialise to the stored or default value
     */
    public boolean get() {
        if (!initialised) {
            initialised = true;

            if (getter != null) {
                value = getter.getAsBoolean();
            }
        }

        return value;
    }

    /**
     * Sets a value but strictly does not call the serialiser, non-boxed
     * @param value the new value to set to
     */
    public void set(boolean value) {
        initialised = true;
        this.value = value;
    }

    /**
     * Refreshes the cached value with the {@link #getter} if non-null
     */
    public void pull() {
        if (getter != null) {
            value = getter.getAsBoolean();
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
