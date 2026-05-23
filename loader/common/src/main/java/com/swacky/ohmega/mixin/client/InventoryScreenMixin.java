package com.swacky.ohmega.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.client.screen.IEntityRenderingExtension;
import com.swacky.ohmega.api.client.screen.IMixinAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IMixinEntityRenderingScreen;
import com.swacky.ohmega.config.OhmegaConfig;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> implements IMixinAccessoryScreen, IMixinEntityRenderingScreen {
    @Unique
    private @Nullable AccessoryScreenExtension ohmega$extension = null;

    private InventoryScreenMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title) {
        super(menu, recipeBook, inventory, title);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @Nullable AccessoryScreenExtension getAccessoryExtension() {
        return ohmega$extension;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setAccessoryExtension(@NonNull AccessoryScreenExtension extension) {
        ohmega$extension = extension;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public int getAccessoryExtensionX() {
        return OhmegaConfig.Client.survivalExtensionX();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public int getAccessoryExtensionY() {
        return OhmegaConfig.Client.survivalExtensionY();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public IntIntPair getAccessoryExtensionToggleButtonPosition(OhmegaConfig.Client.Service.ButtonStyle style) {
        return switch (style) {
            case DEFAULT -> IntIntPair.of(132, 61);
            case LEGACY -> IntIntPair.of(27, 9);
            case TAG_LEFT -> IntIntPair.of(-11, 8);
            case TAG_RIGHT -> IntIntPair.of(173, 8);
            default -> throw new IllegalStateException("Unexpected value: " + style);
        };
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull IntIntPair getFlipEntityButtonPosition() {
        return IntIntPair.of(65, 9);
    }

    @Inject(
            method = "<init>",
            at = @At(
                    value = "RETURN"))
    private void init(Player player, CallbackInfo ci) {
        AccessoryScreens.onConstruct(this);
    }

    @ModifyArg(
            method = "extractBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;extractEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"),
            index = 5)
    private int extractBackground(int size) {
        // Hacky thing, shouldn't cause issues
        if (ohmega$extension instanceof IEntityRenderingExtension extension && extension.isEntityFlipped()) {
            return -size;
        }

        return size;
    }

    @WrapOperation(
            method = "extractEntityInInventoryFollowsMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;entity(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FLorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;IIII)V"))
    private static void extractEntityInInventoryFollowsMouse(GuiGraphicsExtractor gui, EntityRenderState state, float scale, Vector3f translation, Quaternionf rotation, Quaternionf xRotation, int x0, int y0, int x1, int y1, Operation<Void> handle) {
        // Hacky thing, shouldn't cause issues
        if (state instanceof LivingEntityRenderState livingState && scale < 0) {
            scale = -scale;

            livingState.bodyRot = -livingState.bodyRot;
            livingState.yRot = -livingState.yRot;

            rotation.rotationX((float) Math.PI);
        }

        handle.call(gui, state, scale, translation, rotation, xRotation, x0, y0, x1, y1);
    }
}