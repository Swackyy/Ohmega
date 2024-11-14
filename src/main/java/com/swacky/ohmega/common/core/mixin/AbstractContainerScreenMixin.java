package com.swacky.ohmega.common.core.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.common.core.init.OhmegaBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isActiveAndMatches(Lcom/mojang/blaze3d/platform/InputConstants$Key;)Z"))
    public boolean keyPressed(KeyMapping instance, InputConstants.Key key) {
        Minecraft mc = ((AbstractContainerScreen<?>) (Object) this).getMinecraft();
        if (mc != null) {
            return mc.options.keyInventory.isActiveAndMatches(key) || OhmegaBinds.OPEN_ACC_INV.isActiveAndMatches(key);
        }
        return false;
    }
}
