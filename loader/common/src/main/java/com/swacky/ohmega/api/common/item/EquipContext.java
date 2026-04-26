package com.swacky.ohmega.api.common.item;

/**
 * Context for when an accessory is equipped or un-equipped, provided for certain methods and events.
 * <p>
 * A boolean {@link #isMutateSafe()} is provided to state whether it is generally considered safe
 * to either cancel or heavily modify changes occurring with the provided context
 */
public enum EquipContext {
    ATTACH(false),
    COMMAND(true),
    DEATH(false),
    DISPENSE(true),
    USE_HELD(true),
    SLOT(false),
    SYNC(false);

    private final boolean mutateSafe;

    EquipContext(boolean mutateSafe) {
        this.mutateSafe = mutateSafe;
    }

    public boolean isMutateSafe() {
        return mutateSafe;
    }
}
