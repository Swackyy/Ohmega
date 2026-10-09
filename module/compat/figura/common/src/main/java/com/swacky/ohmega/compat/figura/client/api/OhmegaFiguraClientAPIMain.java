package com.swacky.ohmega.compat.figura.client.api;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.compat.figura.client.api.accessorytype.FiguraAccessoryType;
import com.swacky.ohmega.compat.figura.client.api.dataattachment.FiguraAccessoryData;
import com.swacky.ohmega.compat.figura.client.api.dataattachment.FiguraAccessoryDataEntry;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.entries.FiguraAPI;
import org.figuramc.figura.entries.annotations.FiguraAPIPlugin;
import org.figuramc.figura.lua.LuaNotNil;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.api.entity.LivingEntityAPI;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaMethodOverload;
import org.figuramc.figura.lua.docs.LuaTypeDoc;

import java.util.Collection;
import java.util.List;

@FiguraAPIPlugin
@LuaWhitelist
@LuaTypeDoc(
        name = "OhmegaAPI",
        value = Ohmega.MODID)
public class OhmegaFiguraClientAPIMain implements FiguraAPI {
    @Override
    public FiguraAPI build(Avatar avatar) {
        return this;
    }

    @Override
    public String getName() {
        return Ohmega.MODID;
    }

    @Override
    public Collection<Class<?>> getWhitelistedClasses() {
        return List.of(
                OhmegaFiguraClientAPIMain.class,
                FiguraAccessoryType.class,
                FiguraAccessoryData.class,
                FiguraAccessoryDataEntry.class);
    }

    @Override
    public Collection<Class<?>> getDocsClasses() {
        return List.of(
                OhmegaFiguraClientAPIMain.class,
                FiguraAccessoryType.class,
                FiguraAccessoryData.class,
                FiguraAccessoryDataEntry.class);
    }

    @Override
    public String toString() {
        return "Ohmega";
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaMethodOverload(
                    argumentTypes = LivingEntityAPI.class,
                    argumentNames = "entity"),
            value = "get_data")
    public static FiguraAccessoryData getData(@LuaNotNil LivingEntityAPI<?> entity) {
        return new FiguraAccessoryData(OhmegaDataAttachments.getData(entity.getEntity()));
    }
}
