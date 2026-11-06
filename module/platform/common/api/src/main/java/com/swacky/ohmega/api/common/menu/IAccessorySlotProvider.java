package com.swacky.ohmega.api.common.menu;

import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

/**
 * A provider interface for {@link AccessorySlot}s
 */
public interface IAccessorySlotProvider extends Supplier<@NonNull AccessorySlot> {}
