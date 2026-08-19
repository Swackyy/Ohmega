package com.swacky.ohmega.compat.jei.common;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.compat.util.ExclusionZoneProvider;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.List;

@JeiPlugin
public class OhmegaJeiMain implements IModPlugin {
    @Override
    public @NonNull Identifier getPluginUid() {
        return Ohmega.id("jei");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration register) {
        register.addGenericGuiContainerHandler(AbstractContainerScreen.class, new IGuiContainerHandler<>() {
            @Override
            public @NonNull List<Rect2i> getGuiExtraAreas(@NonNull AbstractContainerScreen<?> screen) {
                return ExclusionZoneProvider.getExclusionZones(screen);
            }
        });
    }
}
