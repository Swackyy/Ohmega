package com.swacky.ohmega.api;

import net.minecraft.network.chat.contents.TranslatableContents;

public enum AccessoryType {
    NORMAL("normal"),
    UTILITY("utility"),
    SPECIAL("special");

    private final String identifier;

    AccessoryType(String identifier) {
        this.identifier = identifier;
    }

    public TranslatableContents getTranslation() {
        return new TranslatableContents("accessory.type." + identifier, null, TranslatableContents.NO_ARGS);
    }

    public String getIdentifier() {
        return this.identifier;
    }
}
