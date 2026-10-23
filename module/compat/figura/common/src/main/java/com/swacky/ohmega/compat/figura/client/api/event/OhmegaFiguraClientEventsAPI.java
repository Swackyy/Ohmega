package com.swacky.ohmega.compat.figura.client.api.event;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.api.common.Ohmega;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.event.LuaEvent;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.figuramc.figura.lua.docs.LuaTypeDoc;
import org.luaj.vm2.Varargs;

import java.util.Collection;
import java.util.List;

@LuaWhitelist
@LuaTypeDoc(
        name = "OhmegaEventsAPI",
        value = "ohmega.events")
public final class OhmegaFiguraClientEventsAPI {
    @LuaWhitelist
    @LuaFieldDoc("events.ohmega.accessory_layer_render")
    public static final LuaEvent ACCESSORY_LAYER_RENDER = new LuaEvent();
    @LuaWhitelist
    @LuaFieldDoc("events.ohmega.accessory_render_post")
    public static final LuaEvent ACCESSORY_RENDER_POST = new LuaEvent();
    @LuaWhitelist
    @LuaFieldDoc("events.ohmega.accessory_render_pre")
    public static final LuaEvent ACCESSORY_RENDER_PRE = new LuaEvent();
    @LuaWhitelist
    @LuaFieldDoc("events.ohmega.accessory_equip")
    public static final LuaEvent ACCESSORY_EQUIP = new LuaEvent();
    @LuaWhitelist
    @LuaFieldDoc("events.ohmega.accessory_tick_post")
    public static final LuaEvent ACCESSORY_TICK_POST = new LuaEvent();
    @LuaWhitelist
    @LuaFieldDoc("events.ohmega.accessory_tick_pre")
    public static final LuaEvent ACCESSORY_TICK_PRE = new LuaEvent();
    @LuaWhitelist
    @LuaFieldDoc("events.ohmega.accessory_unequip")
    public static final LuaEvent ACCESSORY_UNEQUIP = new LuaEvent();
    @LuaWhitelist
    @LuaFieldDoc("events.ohmega.accessory_use")
    public static final LuaEvent ACCESSORY_USE = new LuaEvent();

    public static void clear() {
        ACCESSORY_LAYER_RENDER.clear();
        ACCESSORY_RENDER_POST.clear();
        ACCESSORY_RENDER_PRE.clear();
        ACCESSORY_EQUIP.clear();
        ACCESSORY_TICK_POST.clear();
        ACCESSORY_TICK_PRE.clear();
        ACCESSORY_UNEQUIP.clear();
        ACCESSORY_USE.clear();
    }

    public static String getID() {
        return Ohmega.MODID;
    }

    public static Collection<Pair<String, LuaEvent>> getEvents() {
        return List.of(
                Pair.of("ACCESSORY_LAYER_RENDER", ACCESSORY_LAYER_RENDER),
                Pair.of("ACCESSORY_RENDER_POST", ACCESSORY_RENDER_POST),
                Pair.of("ACCESSORY_RENDER_PRE", ACCESSORY_RENDER_PRE),
                Pair.of("ACCESSORY_EQUIP", ACCESSORY_EQUIP),
                Pair.of("ACCESSORY_TICK_POST", ACCESSORY_TICK_POST),
                Pair.of("ACCESSORY_TICK_PRE", ACCESSORY_TICK_PRE),
                Pair.of("ACCESSORY_UNEQUIP", ACCESSORY_UNEQUIP),
                Pair.of("ACCESSORY_USE", ACCESSORY_USE));
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
