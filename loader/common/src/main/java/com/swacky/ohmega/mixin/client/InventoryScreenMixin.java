package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.client.screen.IEntityRenderingExtension;
import com.swacky.ohmega.api.client.screen.IMixinAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IMixinEntityRenderingScreen;
import com.swacky.ohmega.api.client.screen.widget.LazyPosition;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
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
    public @NonNull LazyPosition getAccessoryExtensionPosition() {
        OhmegaConfig.Client.Service.Data data = OhmegaConfig.Client.getData();

        return new LazyPosition(
                data.survivalExtensionX(),
                data.survivalExtensionY());
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull LazyPosition getAccessoryExtensionToggleButtonPosition(OhmegaConfig.Client.Service.ButtonStyle style) {
        return switch (style) {
            case DEFAULT -> new LazyPosition(132, 61);
            case LEGACY -> new LazyPosition(27, 9);
            case TAG_LEFT -> new LazyPosition(-11, 8);
            case TAG_RIGHT -> new LazyPosition(173, 8);
            default -> throw new IllegalStateException("Unexpected value: " + style);
        };
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull LazyPosition getFlipEntityButtonPosition() {
        return new LazyPosition(65, 9);
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
}