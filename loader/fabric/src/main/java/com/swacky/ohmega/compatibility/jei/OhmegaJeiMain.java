package com.swacky.ohmega.compatibility.jei;

/*
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.event.ClientCallbacks;
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
*/
// todo: this needs to be done in a much more disable-able way
public class OhmegaJeiMain /*implements IModPlugin*/ {
    /*
    @Override
    public @NonNull Identifier getPluginUid() {
        return Ohmega.id("jei");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration register) {
        register.addGenericGuiContainerHandler(AbstractContainerScreen.class, new IGuiContainerHandler<>() {
            @Override
            public @NonNull List<Rect2i> getGuiExtraAreas(@NonNull AbstractContainerScreen<?> screen) {
                return ClientCallbacks.getJeiAvoidRects(screen);
            }
        });
    }
    */
}
