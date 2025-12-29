package com.swacky.ohmega.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.common.init.OhmegaBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerScreen.class)
abstract class AbstractContainerScreenMixin extends Screen {
    private AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isActiveAndMatches(Lcom/mojang/blaze3d/platform/InputConstants$Key;)Z", ordinal = 0))
    public boolean keyPressed(KeyMapping instance, InputConstants.Key key) {
        return this.minecraft.options.keyInventory.isActiveAndMatches(key) || OhmegaBinds.OPEN_ACC_INV.isActiveAndMatches(key);
    }
}
