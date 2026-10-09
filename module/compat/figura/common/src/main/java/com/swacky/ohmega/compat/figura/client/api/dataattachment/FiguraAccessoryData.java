package com.swacky.ohmega.compat.figura.client.api.dataattachment;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaMethodOverload;
import org.figuramc.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(
        name = "AccessoryData",
        value = "accessory_data")
public class FiguraAccessoryData {
    private final AccessoryData data;

    public FiguraAccessoryData(AccessoryData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AccessoryData";
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaMethodOverload(
                    argumentTypes = Integer.class,
                    argumentNames = "index"),
            value = "get_entry")
    public FiguraAccessoryDataEntry getEntry(int index) {
        return new FiguraAccessoryDataEntry(data.getEntry(index));
    }
}
