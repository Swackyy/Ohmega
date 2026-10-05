package com.swacky.ohmega.api.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An informational annotation to signify that an abstraction service interface is to be implemented in a {@code <loader>-impl} module.
 * This differs from {@link CommonService} only in the fact that it is implemented by a mod-loader
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface LoaderService {}
