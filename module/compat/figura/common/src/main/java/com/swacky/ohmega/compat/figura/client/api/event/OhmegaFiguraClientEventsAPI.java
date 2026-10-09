package com.swacky.ohmega.compat.figura.client.api.event;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.api.common.Ohmega;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.event.LuaEvent;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.luaj.vm2.Varargs;

import java.util.Collection;
import java.util.List;

public final class OhmegaFiguraClientEventsAPI {
    @LuaWhitelist
    @LuaFieldDoc("events.accessory_layer_render")
    public static final LuaEvent ACCESSORY_LAYER_RENDER_EVENT = new LuaEvent();
    @LuaWhitelist
    @LuaFieldDoc("events.accessory_render.post")
    public static final LuaEvent ACCESSORY_RENDER_POST_EVENT = new LuaEvent();
    @LuaWhitelist
    @LuaFieldDoc("events.accessory_render.pre")
    public static final LuaEvent ACCESSORY_RENDER_PRE_EVENT = new LuaEvent();

    public static String getID() {
        return Ohmega.MODID;
    }

    public static Collection<Pair<String, LuaEvent>> getEvents() {
        return List.of(
                Pair.of("ACCESSORY_LAYER_RENDER", ACCESSORY_LAYER_RENDER_EVENT),
                Pair.of("ACCESSORY_RENDER_POST", ACCESSORY_RENDER_POST_EVENT),
                Pair.of("ACCESSORY_RENDER_PRE", ACCESSORY_RENDER_PRE_EVENT));
    }

    public static boolean isCancelled(Varargs args) {
        if (args != null) {
            for (int i = 1; i <= args.narg(); i++) {
                if (args.arg(i).isboolean() && args.arg(i).checkboolean()) {
                    return true;
                }
            }

        }

        return false;
    }
}
