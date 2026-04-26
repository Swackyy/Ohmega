package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.event.ClientCallbacks;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
abstract class NeoForge_AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    private NeoForge_AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;isActiveAndMatches(Lcom/mojang/blaze3d/platform/InputConstants$Key;)Z",
                    ordinal = 0))
    private boolean keyPressed(KeyMapping instance, InputConstants.Key key, Operation<Boolean> handle, @Local(argsOnly = true) KeyEvent event) {
        if (ClientCallbacks.onKeyPressedInMenu((AbstractContainerScreen<?>) (Object) this, event)) {
            return true;
        }

        return handle.call(instance, key);
    }
}
