package com.swacky.ohmega.compat.rrv.event;

import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.compat.util.ExclusionZoneProvider;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Rect2i;

import java.util.List;

public final class ClientEvents {
    private static final String EXCLUSION_ZONE_ID_PREFIX = "rrv_exclusion_zone";

    private static boolean bootstrapped = false;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            //AccessoryExtensionRenderEvent.Pre.EVENT.register(ClientEvents::onAccessoryExtensionRenderPre);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + ClientEvents.class + " multiple times");
        }
    }

    private static boolean onAccessoryExtensionRenderPre(GuiGraphicsExtractor gui, AccessoryScreenExtension extension) {
        List<Rect2i> rects = ExclusionZoneProvider.getExclusionZones(extension.getScreen());
        int size = rects.size();

        for (int i = 0; i < size; i++) {
            Rect2i rect = rects.get(i);
            OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                    Ohmega.id(EXCLUSION_ZONE_ID_PREFIX + '_' + i),
                    rect.getX(),
                    rect.getY(),
                    rect.getWidth(),
                    rect.getHeight()));
        }

        if (rects.isEmpty()) {
            OverlayManager.INSTANCE.removeGuiBlocking(id -> id.getPath().equals(Ohmega.MODID) && id.getNamespace().startsWith(EXCLUSION_ZONE_ID_PREFIX), true);
        }

        return true;
    }
}
