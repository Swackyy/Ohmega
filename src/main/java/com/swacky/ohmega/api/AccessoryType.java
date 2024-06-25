package com.swacky.ohmega.api;

import net.minecraft.network.chat.TranslatableComponent;

public enum AccessoryType {
    NORMAL("normal"),
    UTILITY("utility"),
    SPECIAL("special");

    private final String identifier;

    AccessoryType(String identifier) {
        this.identifier = identifier;
    }

    public TranslatableComponent getTranslation() {
        return new TranslatableComponent("accessory.type." + identifier);
    }

    public String getIdentifier() {
        return this.identifier;
    }
}
