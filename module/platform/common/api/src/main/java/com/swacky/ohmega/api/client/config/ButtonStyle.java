package com.swacky.ohmega.api.client.config;

import com.swacky.ohmega.api.common.Ohmega;
import net.minecraft.resources.Identifier;

public enum ButtonStyle {
    DEFAULT("default", 20, 18, true),
    LEGACY("legacy", 9, 9, true),
    TAG_LEFT("tag_left", 14, 8, false),
    TAG_RIGHT("tag_right", 14, 8, false),
    HIDDEN("hidden", 0, 0, false);

    public final String name;
    public final int width;
    public final int height;
    public final Identifier textureLocation;
    public final boolean highlightWhenHovered;

    ButtonStyle(String name, int width, int height, boolean highlightWhenHovered) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.textureLocation = Ohmega.id("textures/gui/container/accessory_inventory/inventory_buttons/" + name + ".png");
        this.highlightWhenHovered = highlightWhenHovered;
    }
}