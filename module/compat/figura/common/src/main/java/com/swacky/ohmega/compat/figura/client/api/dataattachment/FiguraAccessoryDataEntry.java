package com.swacky.ohmega.compat.figura.client.api.dataattachment;

import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.compat.figura.client.api.accessorytype.FiguraAccessoryType;
import com.swacky.ohmega.compat.figura.client.api.item.AccessoryContextAPI;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.world.ItemStackAPI;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(
        name = "AccessoryDataEntry",
        value = "accessory_data_entry")
public class FiguraAccessoryDataEntry {
    private final AccessoryDataEntry entry;

    public FiguraAccessoryDataEntry(AccessoryDataEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "AccessoryDataEntry";
    }

    @LuaWhitelist
    @LuaMethodDoc("get_type")
    public FiguraAccessoryType getType() {
        return new FiguraAccessoryType(entry.getType());
    }

    @LuaWhitelist
    @LuaMethodDoc("get_context")
    public AccessoryContextAPI getContext() {
        return new AccessoryContextAPI(entry.getContext());
    }

    @LuaWhitelist
    @LuaMethodDoc("get_stack")
    public ItemStackAPI getStack() {
        return new ItemStackAPI(entry.getStack());
    }

    @LuaWhitelist
    @LuaMethodDoc("is_hidden")
    public boolean isHidden() {
        return entry.isHidden();
    }
}
