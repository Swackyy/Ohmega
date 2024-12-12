package com.swacky.ohmega.api.event;

/**
 * Used for Ohmega API events
 */
public enum EventResult {
    PASS, // Allow the event to continue, move onto next listener
    CANCEL; // Stop the event from continuing

    public boolean isCanceled() {
        return this == CANCEL;
    }
}
