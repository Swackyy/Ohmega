package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
abstract class Fabric_AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    private Fabric_AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;matches(Lnet/minecraft/client/input/KeyEvent;)Z",
                    ordinal = 0))
    private boolean keyPressed(KeyMapping instance, KeyEvent event, Operation<Boolean> handle) {
        if (ClientCallbacks.onKeyPressedInMenu((AbstractContainerScreen<?>) (Object) this, event)) {
            return true;
        }

        return handle.call(instance, event);
    }
}
