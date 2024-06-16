package com.swacky.ohmega.api.events;

public enum Phase {
    PRE,
    POST;

    public boolean pre() {
        return this == PRE;
    }

    public boolean post() {
        return this == POST;
    }
}
