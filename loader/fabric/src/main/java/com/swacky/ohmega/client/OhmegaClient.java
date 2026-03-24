package com.swacky.ohmega.client;

import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaMenusImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.event.ClientEvents;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

@SuppressWarnings("unused")
public final class OhmegaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OhmegaCommon.bootstrapClient();
        ClientEvents.bootstrap();

        ConfigRegistry.INSTANCE.register(OhmegaCommon.MODID, ModConfig.Type.CLIENT, OhmegaConfigImpl.Client.getSpec());

        MenuScreens.register(OhmegaMenusImpl.ACCESSORY_INVENTORY, AccessoryInventoryScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(SyncAccessorySlotsPacket.TYPE, OhmegaNetworkingImpl.S2C::handleSyncAccessorySlots);
        ClientConfigurationNetworking.registerGlobalReceiver(SyncAccessoryTypesPacket.TYPE, OhmegaNetworkingImpl.S2C::handleSyncAccessoryTypes);

        ConfigScreenFactoryRegistry.INSTANCE.register(OhmegaCommon.MODID, ConfigurationScreen::new);
    }
}
