package com.swacky.ohmega.event;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.fml.config.ModConfig;

import java.util.List;

public final class ClientEvents {
    private static boolean bootstrapped;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            NeoForgeModConfigEvents.loading(OhmegaCommon.MODID).register(ClientEvents::onConfigLoad);
            NeoForgeModConfigEvents.reloading(OhmegaCommon.MODID).register(ClientEvents::onConfigReload);
            NeoForgeModConfigEvents.unloading(OhmegaCommon.MODID).register(ClientEvents::onConfigUnload);
            ItemTooltipCallback.EVENT.register(ClientEvents::onItemTooltip);
            ScreenEvents.AFTER_INIT.register(ClientEvents::onPostScreenInit);
        } else {
            throw new RuntimeException("Cannot bootstrap " + ClientEvents.class.getName() + " multiple times");
        }
    }

    private static void onConfigLoad(ModConfig config) {
        if (config.getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigLoad(Minecraft.getInstance().options::load);
        }
    }

    private static void onConfigReload(ModConfig config) {
        if (OhmegaConfigImpl.Client.getSpec().isLoaded()) {
            if (config.getSpec() == OhmegaConfigImpl.Client.getSpec()) {
                ClientCallbacks.onClientConfigReload();
            } else if (config.getSpec() == OhmegaConfigImpl.Server.getSpec() && OhmegaConfigImpl.Server.getSpec().isLoaded()) {
                ClientCallbacks.onServerConfigReload(Minecraft.getInstance().options::load);
            }
        }
    }

    private static void onConfigUnload(ModConfig config) {
        if (OhmegaConfigImpl.Client.getSpec().isLoaded() && config.getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigUnload(Minecraft.getInstance().options::load);
        }
    }

    private static void onItemTooltip(ItemStack stack, TooltipFlag flag, List<Component> tooltip) {
        ClientCallbacks.onItemTooltip(stack, tooltip);
    }

    private static void onPostScreenInit(Minecraft mc, Screen screen, int width, int height) {
        ClientCallbacks.onPostScreenInit(screen, screen::addRenderableWidget);
    }
}
