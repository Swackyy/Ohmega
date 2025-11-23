package com.swacky.ohmega.client;

import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.*;
import com.swacky.ohmega.event.OhmegaClientEvents;
import com.swacky.ohmega.event.OhmegaCommonEvents;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class OhmegaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OhmegaCommonEvents.bootstrap();
        OhmegaClientEvents.bootstrap();

        OhmegaItems.init();
        OhmegaMenus.init();
        OhmegaDataComponents.init();
        OhmegaDataAttachments.init();

        KeyBindingHelper.registerKeyBinding(OhmegaBinds.OPEN_ACC_INV);

        MenuScreens.register(OhmegaMenus.ACCESSORY_INVENTORY, AccessoryInventoryScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(SyncAccessorySlotsPacket.TYPE, SyncAccessorySlotsPacket::handle);
        ClientConfigurationNetworking.registerGlobalReceiver(SyncAccessoryTypesPacket.TYPE, SyncAccessoryTypesPacket::handle);

        ConfigScreenFactoryRegistry.INSTANCE.register(OhmegaCommon.MODID, ConfigurationScreen::new);
    }
}
