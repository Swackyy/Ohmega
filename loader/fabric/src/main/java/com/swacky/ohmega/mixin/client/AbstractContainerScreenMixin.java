package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.common.init.OhmegaBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;matches(Lnet/minecraft/client/input/KeyEvent;)Z", ordinal = 0))
    public boolean keyPressed(KeyMapping instance, KeyEvent event) {
        Minecraft mc = ((AbstractContainerScreen<?>) (Object) this).minecraft;
        if (mc != null) {
            return mc.options.keyInventory.matches(event) || OhmegaBinds.OPEN_ACC_INV.matches(event);
        }
        return false;
    }
}
