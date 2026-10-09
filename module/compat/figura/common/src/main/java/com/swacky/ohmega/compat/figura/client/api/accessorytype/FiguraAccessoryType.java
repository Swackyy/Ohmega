package com.swacky.ohmega.compat.figura.client.api.accessorytype;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(
        name = "AccessoryType",
        value = "accessory_type")
public class FiguraAccessoryType {
    private final AccessoryType type;

    public FiguraAccessoryType(AccessoryType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AccessoryType";
    }

    @LuaWhitelist
    @LuaMethodDoc("get_id")
    public String getId() {
        return type.getId().toString();
    }

    @LuaWhitelist
    @LuaMethodDoc("allow_fallback")
    public boolean allowFallback() {
        return type.allowFallback();
    }

    @LuaWhitelist
    @LuaMethodDoc("allow_reference")
    public boolean allowReference() {
        return type.allowReference();
    }

    @LuaWhitelist
    @LuaMethodDoc("display_hover_text")
    public boolean displayHoverText() {
        return type.displayHoverText();
    }

    @LuaWhitelist
    @LuaMethodDoc("get_default_slots")
    public int getDefaultSlots() {
        return type.getDefaultSlots();
    }

    @LuaWhitelist
    @LuaMethodDoc("get_empty_slot_location")
    public String getEmptySlotLocation() {
        return type.getEmptySlotLocation().toString();
    }

    @LuaWhitelist
    @LuaMethodDoc("get_hover_text_colour")
    public int getHoverTextColour() {
        return type.getHoverTextColour();
    }

    @LuaWhitelist
    @LuaMethodDoc("get_slot_priority")
    public int getSlotPriority() {
        return type.getSlotPriority();
    }

    @LuaWhitelist
    @LuaMethodDoc("get_type_priority")
    public int getTypePriority() {
        return type.getTypePriority();
    }

    @LuaWhitelist
    @LuaMethodDoc("is_default")
    public boolean isDefault() {
        return type.isDefault();
    }

    @LuaWhitelist
    @LuaMethodDoc("get_translation")
    public String getTranslation() {
        return type.getTranslation().getString();
    }
}
