package com.swacky.ohmega.compat.rrv.mixin;

import cc.cassian.rrv.common.overlay.itemlist.AbstractRrvItemListOverlay;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SidePanelOverlay.class)
abstract class SidePanelOverlayMixin extends AbstractRrvItemListOverlay {
    protected SidePanelOverlayMixin(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @WrapOperation(
            method = "lambda$updateSidePanelIndex$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lcc/cassian/rrv/common/overlay/itemlist/panel/SidePanelOverlay;updateSlots()V", ordinal = 0))
    private void updateSidePanelIndex(SidePanelOverlay instance, Operation<Void> handle) {
        Minecraft.getInstance().execute(() -> handle.call(instance));
    }
}
