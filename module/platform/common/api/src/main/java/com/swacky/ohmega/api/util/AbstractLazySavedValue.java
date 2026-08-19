package com.swacky.ohmega.api.util;

import org.jspecify.annotations.Nullable;

/**
 * Templates a 2-way lazy saved value scheme, allowing for lazily fetching an initial value and having deferred serialisation
 * @param <T> the value type to store, possibly primitive and in such a case the primitive-specific implementations should be used when applicable
 */
public abstract class AbstractLazySavedValue<T> {
    protected boolean initialised = false;

    /**
     * Function purely to retrieve the stored data, possibly boxing
     * @return the stored value
     */
    public abstract @Nullable T getObject();

    /**
     * Function purely to set the stored data, possibly boxing
     * @param value the value to set as
     */
    public abstract void setObject(@Nullable T value);

    /**
     * Check if this can be serialised
     * @return {@code true} if a serialiser has been specified, {@code false} otherwise ({@code null} value)
     */
    public abstract boolean isSerialisable();
}
