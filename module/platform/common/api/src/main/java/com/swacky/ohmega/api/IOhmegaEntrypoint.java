package com.swacky.ohmega.api;

import org.jspecify.annotations.NonNull;

import java.util.function.Function;

/**
 * Custom entrypoints should implement this class and override {@link #invoke(Function)} to execute optional dependency code
 * @apiNote If you are targeting Ohmega as a required dependency you should not be using this
 */
public interface IOhmegaEntrypoint {
    /**
     * Where code execution begins for this entrypoint, called internally when necessary.
     * The exact calling location differs by mod-loader
     */
    void invoke(@NonNull Function<String, Boolean> modLoaded);
}
