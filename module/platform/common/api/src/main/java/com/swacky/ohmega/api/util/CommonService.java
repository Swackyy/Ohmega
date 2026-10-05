package com.swacky.ohmega.api.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An informational annotation to signify that an abstraction service interface is to be implemented in the {@code common-impl} module.
 * The only reason for the abstraction is to better separate the {@code api} and {@code impl} modules.
 * This differs from {@link LoaderService} only in the fact that it is not implemented by a mod-loader
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface CommonService {}
