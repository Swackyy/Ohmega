package com.swacky.ohmega.api.util;

import org.jspecify.annotations.Nullable;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/**
 * Non-boxing double implementation of {@link AbstractLazySavedValue}
 */
public class DoubleLazySavedValue extends AbstractLazySavedValue<Double> {
    private final DoubleSupplier getter;
    private final DoubleConsumer setter;

    private double value;

    /**
     * Construct a new {@link DoubleLazySavedValue}
     * @param getter the initial value supplier
     * @param setter the serialisation value acceptor
     */
    public DoubleLazySavedValue(@Nullable DoubleSupplier getter, @Nullable DoubleConsumer setter) {
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Construct a new variable-value {@link DoubleLazySavedValue}
     * @param value the initial value to set as
     * @return newly constructed instance
     */
    public static DoubleLazySavedValue of(double value) {
        DoubleLazySavedValue instance = new DoubleLazySavedValue(null, null);
        instance.value = value;

        return instance;
    }

    @Override
    public Double getObject() {
        return get();
    }

    @Override
    public void setObject(Double value) {
        set(value);
    }

    /**
     * Lazily get the stored value, non-boxed
     * @return the current value if it has already been fetched, else calls the {@link #getter} to initialise to the stored or default value
     */
    public double get() {
        if (!initialised) {
            initialised = true;

            if (getter != null) {
                value = getter.getAsDouble();
            }
        }

        return value;
    }

    /**
     * Sets a value but strictly does not call the serialiser, non-boxed
     * @param value the new value to set to
     */
    public void set(double value) {
        initialised = true;
        this.value = value;
    }

    /**
     * Refreshes the cached value with the {@link #getter} if non-null
     */
    public void pull() {
        if (getter != null) {
            value = getter.getAsDouble();
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
