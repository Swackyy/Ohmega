package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.swacky.ohmega.api.client.event.ClientCallbacks;
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
abstract class Fabric_InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {
    public Fabric_InventoryScreenMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title) {
        super(menu, recipeBook, inventory, title);
    }

    @WrapOperation(
            method = "extractEntityInInventoryFollowsMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;entity(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FLorg/joml/Vector3fc;Lorg/joml/Quaternionfc;Lorg/joml/Quaternionfc;IIII)V"))
    private static void extractEntityInInventoryFollowsMouse(GuiGraphicsExtractor gui, EntityRenderState state, float scale, Vector3fc translation, Quaternionfc rotation, Quaternionfc xRotation, int x0, int y0, int x1, int y1, Operation<Void> handle) {
        ClientCallbacks.applyEntityInInventoryTranslation(state, (Quaternionf) rotation);
        handle.call(gui, state, scale, translation, rotation, xRotation, x0, y0, x1, y1);
    }
}
