package com.swacky.ohmega.api.util;

import org.apache.commons.lang3.function.ByteConsumer;
import org.apache.commons.lang3.function.ByteSupplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Non-boxing byte implementation of {@link AbstractLazySavedValue}
 */
public class ByteLazySavedValue extends AbstractLazySavedValue<Byte> {
    private final @Nullable ByteSupplier getter;
    private final @Nullable ByteConsumer setter;

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
     * Construct a new variable-value {@link ByteLazySavedValue}
     * @param value the initial value to set as
     * @return newly constructed instance
     */
    public static @NonNull ByteLazySavedValue of(byte value) {
        ByteLazySavedValue instance = new ByteLazySavedValue(null, null);
        instance.value = value;

        return instance;
    }

    @Override
    public @NonNull Byte getObject() {
        return get();
    }

    @Override
    public void setObject(@Nullable Byte value) {
        if (value != null) {
            set(value);
        }
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
