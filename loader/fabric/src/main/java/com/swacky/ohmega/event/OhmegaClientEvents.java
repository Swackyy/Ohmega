package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.client.screen.AccessoryInventoryButton;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeCapPacket;
import com.swacky.ohmega.common.OhmegaCommon;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.neoforged.fml.config.ModConfig;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class OhmegaClientEvents {
    private static boolean bootstrapped = false;
    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            ScreenEvents.AFTER_INIT.register(OhmegaClientEvents::addToScreens);
            ScreenEvents.BEFORE_INIT.register(OhmegaClientEvents::hide);
            NeoForgeModConfigEvents.loading(OhmegaCommon.MODID).register(OhmegaClientEvents::onConfigLoad);
            NeoForgeModConfigEvents.unloading(OhmegaCommon.MODID).register(OhmegaClientEvents::onConfigUnload);
            NeoForgeModConfigEvents.reloading(OhmegaCommon.MODID).register(OhmegaClientEvents::onConfigReload);
        }
    }

    private static void addToScreens(Minecraft mc, Screen screen, int width, int height) {
        if (screen instanceof InventoryScreen && OhmegaConfig.CONFIG_CLIENT.buttonStyle.get() != OhmegaConfig.ButtonStyle.HIDDEN) {
            if (mc != null && mc.player != null && !mc.player.isCreative() && !mc.player.isSpectator()) {
                screen.addRenderableWidget(new AccessoryInventoryButton(OhmegaConfig.CONFIG_CLIENT.buttonStyle.get(), (AbstractContainerScreen<?>) screen));
            }
        }
    }

    private static void hide(Minecraft mc, Screen screen, int width, int height) {
        if (screen instanceof InventoryScreen scr) {
            for (GuiEventListener list : scr.children()) {
                if (list instanceof AccessoryInventoryButton btn) {
                    btn.visible = !scr.recipeBookComponent.isVisible();
                }
            }
        }
    }

    private static void onConfigLoad(ModConfig config) {
        if (config.getSpec() == OhmegaConfig.SPEC_SERVER) {
            ArrayList<KeyMapping> list = new ArrayList<>();
            for (ImmutableList<KeyMapping> immutableList : OhmegaBinds.Generated.getSlotKeys().values()) {
                list.addAll(immutableList);
            }

            Minecraft mc = Minecraft.getInstance();
            mc.options.keyMappings = ArrayUtils.addAll(Arrays.stream(mc.options.keyMappings).filter(v -> !(v instanceof OhmegaBinds.OhmegaKeyMapping)).toList().toArray(new KeyMapping[0]), list.toArray(new KeyMapping[0]));
            mc.options.load();
        }
    }

    private static void onConfigUnload(ModConfig config) {
        if (OhmegaConfig.SPEC_CLIENT.isLoaded() && config.getSpec() == OhmegaConfig.SPEC_SERVER) {
            Minecraft mc = Minecraft.getInstance();
            mc.options.keyMappings = Arrays.stream(mc.options.keyMappings).filter(v -> !(v instanceof OhmegaBinds.OhmegaKeyMapping)).toList().toArray(new KeyMapping[0]);
            mc.options.load();
        }
    }

    private static void onConfigReload(ModConfig config) {
        if (OhmegaConfig.SPEC_CLIENT.isLoaded() && OhmegaConfig.SPEC_SERVER.isLoaded()) {
            Minecraft mc = Minecraft.getInstance();
            if (config.getSpec() == OhmegaConfig.SPEC_CLIENT && !OhmegaConfig.CONFIG_CLIENT.compatibilityMode.get()) {
                if (mc.player != null && mc.player.containerMenu instanceof AccessoryInventoryMenu) {
                    ClientPlayNetworking.send(new OpenAccessoryInventoryPacket());
                }
            } else if (config.getSpec() == OhmegaConfig.SPEC_SERVER) {
                ArrayList<KeyMapping> list = new ArrayList<>();
                for (ImmutableList<KeyMapping> immutableList : OhmegaBinds.Generated.getSlotKeys().values()) {
                    list.addAll(immutableList);
                }

                mc.options.keyMappings = ArrayUtils.addAll(Arrays.stream(mc.options.keyMappings).filter(v -> !(v instanceof OhmegaBinds.OhmegaKeyMapping)).toList().toArray(new KeyMapping[0]), list.toArray(new KeyMapping[0]));
                mc.options.load();

                if (mc.player != null) {
                    AccessoryHelper.getContainer(mc.player).reloadCfg();
                    ClientPlayNetworking.send(new ResizeCapPacket());

                    if (!OhmegaConfig.CONFIG_CLIENT.compatibilityMode.get() && mc.player.containerMenu instanceof AccessoryInventoryMenu) {
                        mc.screen = null;
                        mc.player.connection.send(new ServerboundContainerClosePacket(mc.player.containerMenu.containerId));
                        ClientPlayNetworking.send(new OpenAccessoryInventoryPacket());
                    }
                }
            }
        }
    }
}
