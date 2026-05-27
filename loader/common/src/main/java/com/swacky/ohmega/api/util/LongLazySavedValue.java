package com.swacky.ohmega.api.util;

import org.jspecify.annotations.Nullable;

import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

/**
 * Non-boxing long implementation of {@link AbstractLazySavedValue}
 */
public class LongLazySavedValue extends AbstractLazySavedValue<Long> {
    private final LongSupplier getter;
    private final LongConsumer setter;

    private long value;

    /**
     * Construct a new {@link LongLazySavedValue}
     * @param getter the initial value supplier
     * @param setter the serialisation value acceptor
     */
    public LongLazySavedValue(@Nullable LongSupplier getter, @Nullable LongConsumer setter) {
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Construct a constant-value {@link LongLazySavedValue}
     * @param value the constant value to set as
     * @return newly constructed constant instance
     */
    public static LongLazySavedValue constant(long value) {
        LongLazySavedValue instance = new LongLazySavedValue(null, null);
        instance.value = value;

        return instance;
    }

    @Override
    public Long getObject() {
        return get();
    }

    @Override
    public void setObject(Long value) {
        set(value);
    }

    /**
     * Lazily get the stored value, non-boxed
     * @return the current value if it has already been fetched, else calls the {@link #getter} to initialise to the stored or default value
     */
    public long get() {
        if (!initialised) {
            initialised = true;

            if (getter != null) {
                value = getter.getAsLong();
            }
        }

        return value;
    }

    /**
     * Sets a value but strictly does not call the serialiser, non-boxed
     * @param value the new value to set to
     */
    public void set(long value) {
        initialised = true;
        this.value = value;
    }

    /**
     * Refreshes the cached value with the {@link #getter} if non-null
     */
    public void pull() {
        if (getter != null) {
            value = getter.getAsLong();
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
