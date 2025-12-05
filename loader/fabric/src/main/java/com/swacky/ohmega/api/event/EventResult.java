package com.swacky.ohmega.api.event;

/**
 * (Deprecated) Used for Ohmega API events
 * <p>
 * Will be migrated to returning boolean return values in cancellable events by next major version (1.5.0).
 * There are no alternatives to using this deprecated class prior to the migration
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated(forRemoval = true, since = "1.4.3+1.21.6")
public enum EventResult {
    PASS, // Allow the event to continue, move onto next listener
    CANCEL; // Stop the event from continuing

    public boolean isCanceled() {
        return this == CANCEL;
    }
}
