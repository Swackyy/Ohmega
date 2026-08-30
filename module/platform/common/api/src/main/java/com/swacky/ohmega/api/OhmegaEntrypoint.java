package com.swacky.ohmega.api;

import com.swacky.ohmega.api.util.LogicalSide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom common entrypoints should be annotated with this class where custom metadata entrypoint specification is not supported by your target mod-loader.
 * Any classes annotated with this should implement {@link IOhmegaEntrypoint}
 * @apiNote If you are targeting Ohmega as a required dependency you should not be using this
 */
// todo: impl this for forge and neo
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OhmegaEntrypoint {
    LogicalSide value();
}
