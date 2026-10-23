package com.swacky.ohmega.compat.figura.client.api.item;

import com.swacky.ohmega.api.common.item.AccessoryContext;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(
        name = "AccessoryContext",
        value = "accessory_context")
public class AccessoryContextAPI {
    private final AccessoryContext context;

    public AccessoryContextAPI(AccessoryContext context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "AccessoryContext";
    }

    @LuaWhitelist
    @LuaMethodDoc("name")
    public String name() {
        return context.name();
    }

    @LuaWhitelist
    @LuaMethodDoc("is_mutate_safe")
    public boolean isMutateSafe() {
        return context.isMutateSafe();
    }
}
