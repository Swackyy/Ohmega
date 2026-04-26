package com.swacky.ohmega.event;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

import java.util.List;

public final class ClientEvents {
    private static boolean bootstrapped = false;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            ModConfigEvents.loading(Ohmega.MODID).register(ClientEvents::onConfigLoad);
            ModConfigEvents.reloading(Ohmega.MODID).register(ClientEvents::onConfigReload);
            ModConfigEvents.unloading(Ohmega.MODID).register(ClientEvents::onConfigUnload);
            ItemTooltipCallback.EVENT.register(ClientEvents::onItemTooltip);
            ClientPlayConnectionEvents.JOIN.register(ClientEvents::onJoinWorld);
            ScreenEvents.AFTER_INIT.register(ClientEvents::onPostScreenInit);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + ClientEvents.class + " multiple times");
        }
    }

    private static void onConfigLoad(ModConfig config) {
        if (config.getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            AccessoryTypeManager.runDeferredAwaitingConfigLoad();
        }
    }

    private static void onConfigReload(ModConfig config) {
        IConfigSpec spec = config.getSpec();

        if (spec == OhmegaConfigImpl.Client.getSpec()) {
            ClientCallbacks.onClientConfigReload();
        } else if (spec == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigReload(Minecraft.getInstance().options::load);
        }
    }

    private static void onConfigUnload(ModConfig config) {
        if (config.getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigUnload(Minecraft.getInstance().options::load);
        }
    }

    private static void onItemTooltip(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> tooltip) {
        ClientCallbacks.onItemTooltip(stack, tooltip);
    }

    private static void onJoinWorld(ClientPacketListener listener, PacketSender sender, Minecraft mc) {
        ClientCallbacks.onJoinWorld(mc);
    }

    private static void onPostScreenInit(Minecraft mc, Screen screen, int width, int height) {
        ClientCallbacks.onPostScreenInit(screen, screen::addRenderableWidget);
    }
}
