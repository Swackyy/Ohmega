package com.swacky.ohmega.client;

import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaMenusImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.event.ClientEvents;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.config.ModConfig;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public final class OhmegaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OhmegaCommon.bootstrapClient();
        ClientEvents.bootstrap();

        ForgeConfigRegistry.INSTANCE.register(OhmegaCommon.MODID, ModConfig.Type.CLIENT, OhmegaConfigImpl.Client.getSpec());

        KeyBindingHelper.registerKeyBinding(OhmegaBinds.OPEN_ACC_INV);
        MenuScreens.register(OhmegaMenusImpl.ACCESSORY_INVENTORY, AccessoryInventoryScreen::new);

        ClientLoginNetworking.registerGlobalReceiver(SyncAccessoryTypesPacket.ID, (client, handler, buf, sender) -> {
            SyncAccessoryTypesPacket packet = new SyncAccessoryTypesPacket(buf);

            OhmegaNetworkingImpl.S2C.handleSyncAccessoryTypes(packet);
            return CompletableFuture.completedFuture(PacketByteBufs.empty());
        });
        ClientPlayNetworking.registerGlobalReceiver(SyncAccessorySlotsPacket.ID, (client, handler, buf, sender) -> {
            SyncAccessorySlotsPacket packet = new SyncAccessorySlotsPacket(buf);

            client.execute(() -> OhmegaNetworkingImpl.S2C.handleSyncAccessorySlots(packet, client.level));
        });
    }
}