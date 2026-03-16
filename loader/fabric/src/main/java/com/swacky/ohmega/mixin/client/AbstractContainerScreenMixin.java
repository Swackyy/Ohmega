package com.swacky.ohmega.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.inv.AccessorySlot;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
abstract class AbstractContainerScreenMixin extends Screen {
    private AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Redirect(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;matches(II)Z",
                    ordinal = 0))
    public boolean keyPressed(KeyMapping instance, int keyCode, int scanCode) {
        if (minecraft != null) {
            return minecraft.options.keyInventory.matches(keyCode, scanCode) || OhmegaBinds.OPEN_ACC_INV.matches(keyCode, scanCode);
        }

        return false;
    }

    @Inject(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/Slot;getNoItemIcon()Lcom/mojang/datafixers/util/Pair;"))
    public void renderSlot(PoseStack stack, Slot slot, CallbackInfo ci) {
        if (slot instanceof AccessorySlot accessorySlot) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, accessorySlot.getType().getEmptySlotLocation());
            blit(stack, slot.x, slot.y, 0, 0, 16, 16, 16, 16);
        }
    }
}
