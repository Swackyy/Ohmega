package com.swacky.ohmega.event;

import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.client.renderer.HaloRenderer;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Ohmega.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> AccessoryRenderers.register(OhmegaItems.getAngelRing(), HaloRenderer::new));
    }

    @SubscribeEvent
    private static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            AccessoryTypeManager.runDeferredAwaitingConfigLoad();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        IConfigSpec<ForgeConfigSpec> spec = event.getConfig().getSpec();

        if (spec == OhmegaConfigImpl.Client.getSpec()) {
            ClientCallbacks.onClientConfigReload();
        } else if (spec == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigReload(() -> Minecraft.getInstance().options.load(true));
        }
    }

    @SubscribeEvent
    public static void onConfigUnload(ModConfigEvent.Unloading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigUnload(() -> Minecraft.getInstance().options.load(true));
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ClientCallbacks.onItemTooltip(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onKeybindRegistration(RegisterKeyMappingsEvent event) {
        event.register(OhmegaBinds.OPEN_ACC_INV);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        ClientCallbacks.onKeyInput();
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientCallbacks.onJoinWorld(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onPostScreenInit(ScreenEvent.Init.Post event) {
        ClientCallbacks.onPostScreenInit(event.getScreen(), event::addListener);
    }
}
