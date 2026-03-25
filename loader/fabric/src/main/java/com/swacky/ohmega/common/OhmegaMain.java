package com.swacky.ohmega.common;

import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.init.OhmegaDataComponentsImpl;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenusImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.event.CommonEvents;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeContainerPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.config.ModConfig;

@SuppressWarnings("unused")
public final class OhmegaMain implements ModInitializer {
    @Override
    public void onInitialize() {
        Ohmega.bootstrap();
        CommonEvents.bootstrap();

        ConfigRegistry.INSTANCE.register(Ohmega.MODID, ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        OhmegaDataAttachments.init();
        OhmegaDataComponentsImpl.init();
        OhmegaItems.init();
        OhmegaMenusImpl.init();

        PayloadTypeRegistry.serverboundPlay().register(OpenAccessoryInventoryPacket.TYPE, OpenAccessoryInventoryPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(OpenInventoryPacket.TYPE, OpenInventoryPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ResizeContainerPacket.TYPE, ResizeContainerPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(UseAccessoryPacket.TYPE, UseAccessoryPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(OpenAccessoryInventoryPacket.TYPE, OhmegaNetworkingImpl.C2S::handleOpenAccessoryInventory);
        ServerPlayNetworking.registerGlobalReceiver(OpenInventoryPacket.TYPE, OhmegaNetworkingImpl.C2S::handleOpenInventory);
        ServerPlayNetworking.registerGlobalReceiver(ResizeContainerPacket.TYPE, OhmegaNetworkingImpl.C2S::handleResizeContainer);
        ServerPlayNetworking.registerGlobalReceiver(UseAccessoryPacket.TYPE, OhmegaNetworkingImpl.C2S::handleUseAccessory);

        PayloadTypeRegistry.clientboundPlay().register(SyncAccessorySlotsPacket.TYPE, SyncAccessorySlotsPacket.CODEC);
        PayloadTypeRegistry.clientboundConfiguration().register(SyncAccessoryTypesPacket.TYPE, SyncAccessoryTypesPacket.CODEC);

        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Ohmega.id(Ohmega.MODID), AccessoryTypeManager.getInstance());
    }
}