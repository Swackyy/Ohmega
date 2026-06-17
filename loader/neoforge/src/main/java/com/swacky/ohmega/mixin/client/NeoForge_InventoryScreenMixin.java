package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.swacky.ohmega.event.ClientCallbacks;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryScreen.class)
abstract class NeoForge_InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {
    public NeoForge_InventoryScreenMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title) {
        super(menu, recipeBook, inventory, title);
    }

    @WrapOperation(
            method = "renderEntityInInventoryFollowsAngle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;entity(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FLorg/joml/Vector3fc;Lorg/joml/Quaternionfc;Lorg/joml/Quaternionfc;IIII)V"))
    private static void extractEntityInInventoryFollowsMouse(GuiGraphicsExtractor gui, EntityRenderState state, float scale, Vector3fc translation, Quaternionfc rotation, Quaternionfc xRotation, int x0, int y0, int x1, int y1, Operation<Void> handle) {
        handle.call(gui, state, ClientCallbacks.applyEntityInInventoryTranslation(state, scale, (Quaternionf) rotation), translation, rotation, xRotation, x0, y0, x1, y1);
    }
}
