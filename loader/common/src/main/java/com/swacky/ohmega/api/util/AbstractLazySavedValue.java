package com.swacky.ohmega.api.util;

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
    public abstract T getObject();

    /**
     * Function purely to set the stored data, possibly boxing
     * @param value the value to set as
     */
    public abstract void setObject(T value);
}
