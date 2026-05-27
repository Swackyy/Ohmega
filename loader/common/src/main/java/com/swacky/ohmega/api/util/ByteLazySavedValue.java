package com.swacky.ohmega.api.util;

import org.apache.commons.lang3.function.ByteConsumer;
import org.apache.commons.lang3.function.ByteSupplier;
import org.jspecify.annotations.Nullable;

/**
 * Non-boxing byte implementation of {@link AbstractLazySavedValue}
 */
public class ByteLazySavedValue extends AbstractLazySavedValue<Byte> {
    private final ByteSupplier getter;
    private final ByteConsumer setter;

    private byte value;

    /**
     * Construct a new {@link ByteLazySavedValue}
     * @param getter the initial value supplier
     * @param setter the serialisation value acceptor
     */
    public ByteLazySavedValue(@Nullable ByteSupplier getter, @Nullable ByteConsumer setter) {
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * Construct a constant-value {@link ByteLazySavedValue}
     * @param value the constant value to set as
     * @return newly constructed constant instance
     */
    public static ByteLazySavedValue constant(byte value) {
        ByteLazySavedValue instance = new ByteLazySavedValue(null, null);
        instance.value = value;

        return instance;
    }

    @Override
    public Byte getObject() {
        return get();
    }

    @Override
    public void setObject(Byte value) {
        set(value);
    }

    /**
     * Lazily get the stored value, non-boxed
     * @return the current value if it has already been fetched, else calls the {@link #getter} to initialise to the stored or default value
     */
    public byte get() {
        if (!initialised) {
            initialised = true;

            if (getter != null) {
                value = getter.getAsByte();
            }
        }

        return value;
    }

    /**
     * Sets a value but strictly does not call the serialiser, non-boxed
     * @param value the new value to set to
     */
    public void set(byte value) {
        initialised = true;
        this.value = value;
    }

    /**
     * Refreshes the cached value with the {@link #getter} if non-null
     */
    public void pull() {
        if (getter != null) {
            value = getter.getAsByte();
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
