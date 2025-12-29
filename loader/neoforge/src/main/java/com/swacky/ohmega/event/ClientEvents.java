package com.swacky.ohmega.event;

import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = OhmegaCommon.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigLoad(Minecraft.getInstance().options::load);
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (OhmegaConfigImpl.Client.getSpec().isLoaded()) {
            ModConfig config = event.getConfig();

            if (config.getSpec() == OhmegaConfigImpl.Client.getSpec()) {
                ClientCallbacks.onClientConfigReload();
            } else if (config.getSpec() == OhmegaConfigImpl.Server.getSpec() && OhmegaConfigImpl.Server.getSpec().isLoaded()) {
                ClientCallbacks.onServerConfigReload(Minecraft.getInstance().options::load);
            }
        }
    }

    @SubscribeEvent
    public static void onConfigUnload(ModConfigEvent.Unloading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigUnload(Minecraft.getInstance().options::load);
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ClientCallbacks.onItemTooltip(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onKeybindRegistration(RegisterKeyMappingsEvent event) {
        event.registerCategory(OhmegaBinds.CATEGORY);
        event.register(OhmegaBinds.OPEN_ACC_INV);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        ClientCallbacks.onKeyInput();
    }

    @SubscribeEvent
    public static void onMenuRegistration(RegisterMenuScreensEvent event) {
        event.register(OhmegaMenus.getAccessoryMenu(), AccessoryInventoryScreen::new);
    }

    @SubscribeEvent
    public static void onPostScreenInit(ScreenEvent.Init.Post event) {
        ClientCallbacks.onPostScreenInit(event.getScreen(), event::addListener);
    }
}
