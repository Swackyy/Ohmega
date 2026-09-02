package com.swacky.ohmega.compat.rrv.mixin;

import cc.cassian.rrv.client.util.RRVExtendedContainerScreen;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.compat.rrv.client.OhmegaRrvClient;
import com.swacky.ohmega.compat.util.exclusionzone.ExclusionZoneProvider;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(RRVExtendedContainerScreen.class)
interface RRVExtendedContainerScreenMixin {
    @Inject(
            method = "extractOverlay",
            at = @At(
                    value = "HEAD"))
    private static void updateSlots(CallbackInfo ci) {
        if (AccessoryScreens.getEffectiveScreen() instanceof AbstractContainerScreen<?> screen) {
            OverlayManager manager = OverlayManager.INSTANCE;
            List<Rect2i> rects = ExclusionZoneProvider.getExclusionZones(screen);
            int newExclusionZonesSize = rects.size();
            List<BlockingGuiComponent> newComponents = new ArrayList<>(newExclusionZonesSize);

            for (int i = 0; i < newExclusionZonesSize; i++) {
                Rect2i rect = rects.get(i);

                newComponents.add(new BlockingGuiComponent(
                        Ohmega.id(OhmegaRrvClient.EXCLUSION_ZONE_ID_PREFIX + '_' + i),
                        rect.getX(),
                        rect.getY(),
                        rect.getWidth(),
                        rect.getHeight()));
            }

            List<BlockingGuiComponent> currentComponents = manager.allGuiBlockings();
            List<BlockingGuiComponent> currentComponentsFiltered = new ArrayList<>(currentComponents.size());

            for (BlockingGuiComponent component : currentComponents) {
                Identifier id = component.id();

                if (id.getNamespace().equals(Ohmega.MODID) && id.getPath().startsWith(OhmegaRrvClient.EXCLUSION_ZONE_ID_PREFIX)) {
                    currentComponentsFiltered.add(component);
                }
            }

            if (currentComponentsFiltered.size() != newExclusionZonesSize || !ohmega$containsAllIgnoreId(currentComponentsFiltered, newComponents)) {
                manager.removeGuiBlocking(
                        id -> id.getNamespace().equals(Ohmega.MODID) && id.getPath().startsWith(OhmegaRrvClient.EXCLUSION_ZONE_ID_PREFIX), false);
                newComponents.forEach(manager::setGuiBlocking);
                manager.updateOverlaysAndWidgets(true);
            }
        }
    }

    @Unique
    private static boolean ohmega$containsAllIgnoreId(List<BlockingGuiComponent> list, List<BlockingGuiComponent> sublist) {
        for (BlockingGuiComponent secondElement : sublist) {
            boolean found = false;

            for (BlockingGuiComponent firstElement : list) {
                if (
                        firstElement == secondElement || (
                            firstElement.x() == secondElement.x() &&
                            firstElement.y() == secondElement.y() &&
                            firstElement.width() == secondElement.width() &&
                            firstElement.height() == secondElement.height())) {
                    found = true;

                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        return true;
    }
}
