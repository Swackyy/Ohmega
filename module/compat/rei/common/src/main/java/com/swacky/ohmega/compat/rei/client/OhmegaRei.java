package com.swacky.ohmega.compat.rei.client;

import com.swacky.ohmega.compat.util.exclusionzone.ExclusionZoneProvider;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;

public final class OhmegaRei {
    @SuppressWarnings("ProtectedMemberInFinalClass")
    protected static void registerExclusionZones(ExclusionZones zones) {
        zones.register(AbstractContainerScreen.class, screen -> {
            List<Rect2i> rects = ExclusionZoneProvider.getExclusionZones(screen);
            ArrayList<Rectangle> ret = new ArrayList<>(rects.size());

            for (Rect2i rect : rects) {
                ret.add(new Rectangle(
                        rect.getX(),
                        rect.getY(),
                        rect.getWidth(),
                        rect.getHeight()));
            }

            return ret;
        });
    }
}