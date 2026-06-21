package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.client.screen.IMixinAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IMixinEntityRenderingScreen;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.client.screen.SnapLine;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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
        OhmegaConfig.Client.Service.Data data = OhmegaConfig.Client.getData();

        return switch (style) {
            case DEFAULT -> new LazyPosition(data.survivalToggleExtensionButtonDefaultX(), data.survivalToggleExtensionButtonDefaultY());
            case LEGACY -> new LazyPosition(data.survivalToggleExtensionButtonLegacyX(), data.survivalToggleExtensionButtonLegacyY());
            case TAG_LEFT -> new LazyPosition(data.survivalToggleExtensionButtonTagLeftX(), data.survivalToggleExtensionButtonTagLeftY());
            case TAG_RIGHT -> new LazyPosition(data.survivalToggleExtensionButtonTagRightX(), data.survivalToggleExtensionButtonTagRightY());
            default -> throw new IllegalStateException("Unexpected value: " + style);
        };
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @NonNull LazyPosition getFlipEntityButtonPosition() {
        OhmegaConfig.Client.Service.Data data = OhmegaConfig.Client.getData();

        return new LazyPosition(data.survivalFlipEntityButtonX(), data.survivalFlipEntityButtonY());
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public @Nullable List<SnapLine> getSnapLines(@NonNull AbstractContainerScreen<?> screen, @NonNull AccessoryScreenExtension extension) {
        int x = screen.leftPos;
        int y = screen.topPos;

        return List.of(
                new SnapLine(true, x + 25),
                new SnapLine(false, y + 7),
                new SnapLine(true, x + 75),
                new SnapLine(false, y + 78));
    }

    @Inject(
            method = "<init>",
            at = @At(
                    value = "RETURN"))
    private void init(Player player, CallbackInfo ci) {
        AccessoryScreens.onConstruct(this);
    }
}