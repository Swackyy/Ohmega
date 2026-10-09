package com.swacky.ohmega.compat.figura.client.event;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.compat.figura.client.api.event.OhmegaFiguraClientEventsAPI;
import org.figuramc.figura.entries.FiguraEvent;
import org.figuramc.figura.entries.annotations.FiguraEventPlugin;
import org.figuramc.figura.lua.api.event.LuaEvent;

import java.util.Collection;

@FiguraEventPlugin
public class OhmegaFiguraClientEventsMain implements FiguraEvent {
    @Override
    public String getID() {
        return OhmegaFiguraClientEventsAPI.getID();
    }

    @Override
    public Collection<Pair<String, LuaEvent>> getEvents() {
        return OhmegaFiguraClientEventsAPI.getEvents();
    }
}
