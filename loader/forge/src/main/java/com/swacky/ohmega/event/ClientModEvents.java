package com.swacky.ohmega.event;

import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = OhmegaCommon.MODID, value = Dist.CLIENT)
public final class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(OhmegaMenus.getAccessoryMenu(), AccessoryInventoryScreen::new));
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigLoad();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (OhmegaConfigImpl.Client.getSpec().isLoaded()) {
            ModConfig config = event.getConfig();

            if (config.getSpec() == OhmegaConfigImpl.Client.getSpec()) {
                ClientCallbacks.onClientConfigReload();
            } else if (config.getSpec() == OhmegaConfigImpl.Server.getSpec() && OhmegaConfigImpl.Server.getSpec().isLoaded()) {
                ClientCallbacks.onServerConfigReload();
            }
        }
    }
}
