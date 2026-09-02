package com.swacky.ohmega.compat.util.exclusionzone;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.config.OhmegaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ExclusionZoneProvider {
    public static List<Rect2i> getExclusionZones(AbstractContainerScreen<?> screen) {
        if (screen instanceof IAccessoryScreen accessoryScreen) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null && extension.isVisible()) {
                List<Rect2i> rects = null;

                if (screen.showsActiveEffects()) {
                    Minecraft minecraft = Minecraft.getInstance();
                    LocalPlayer player = minecraft.player;

                    if (player != null) {
                        Collection<MobEffectInstance> activeEffects = player.getActiveEffects();

                        if (!activeEffects.isEmpty()) {
                            int height;
                            rects = new ArrayList<>(3);

                            if (activeEffects.size() > 5) {
                                height = 132 / (activeEffects.size() - 1);
                            } else {
                                height = 33;
                            }

                            rects.add(new Rect2i(
                                    screen.leftPos + screen.imageWidth + extension.getExtraWidthRight() + 2,
                                    screen.topPos,
                                    32,
                                    height * activeEffects.size()));
                        }
                    }
                }

                if (rects == null) {
                    rects = new ArrayList<>(2);
                }

                OhmegaConfig.Client.Service.ButtonStyle buttonStyle = OhmegaConfig.Client.getData().toggleExtensionButtonStyle().getObject();

                if (buttonStyle != null && buttonStyle != OhmegaConfig.Client.Service.ButtonStyle.HIDDEN) {
                    LazyPosition buttonPosition = accessoryScreen.getAccessoryExtensionToggleButtonPosition(buttonStyle);

                    rects.add(new Rect2i(
                            screen.leftPos + buttonPosition.x().get(),
                            screen.topPos + buttonPosition.y().get(),
                            buttonStyle.width,
                            buttonStyle.height));
                }

                LazyPosition position = accessoryScreen.getAccessoryExtensionPosition();

                rects.add(new Rect2i(
                        screen.leftPos + position.x().get(),
                        screen.topPos + position.y().get(),
                        extension.getWidth(),
                        extension.getHeight()));

                return rects;
            }
        }

        return List.of();
    }
}
