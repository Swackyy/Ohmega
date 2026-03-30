package com.swacky.ohmega.common;

import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.init.OhmegaDataComponentsImpl;
import com.swacky.ohmega.common.init.OhmegaItemsImpl;
import com.swacky.ohmega.common.init.OhmegaMenusImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.event.CommonEvents;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeContainerPacket;
import com.swacky.ohmega.network.common.SetVisibilityPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.config.ModConfig;

@SuppressWarnings("unused")
public final class OhmegaMain implements ModInitializer {
    @Override
    public void onInitialize() {
        // Bootstrap
        Ohmega.bootstrap();
        CommonEvents.bootstrap();

        // Config
        ConfigRegistry.INSTANCE.register(Ohmega.MODID, ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        // Networking
        // Send
        // C2S
        PayloadTypeRegistry.serverboundPlay().register(OpenAccessoryInventoryPacket.TYPE, OpenAccessoryInventoryPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(OpenInventoryPacket.TYPE, OpenInventoryPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ResizeContainerPacket.TYPE, ResizeContainerPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetVisibilityPacket.TYPE, SetVisibilityPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(UseAccessoryPacket.TYPE, UseAccessoryPacket.CODEC);
        // S2C
        PayloadTypeRegistry.clientboundPlay().register(SetVisibilityPacket.TYPE, SetVisibilityPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncAccessorySlotsPacket.TYPE, SyncAccessorySlotsPacket.CODEC);
        PayloadTypeRegistry.clientboundConfiguration().register(SyncAccessoryTypesPacket.TYPE, SyncAccessoryTypesPacket.CODEC);
        // Receive
        ServerPlayNetworking.registerGlobalReceiver(OpenAccessoryInventoryPacket.TYPE, OhmegaNetworkingImpl.C2S::handleOpenAccessoryInventory);
        ServerPlayNetworking.registerGlobalReceiver(OpenInventoryPacket.TYPE, OhmegaNetworkingImpl.C2S::handleOpenInventory);
        ServerPlayNetworking.registerGlobalReceiver(ResizeContainerPacket.TYPE, OhmegaNetworkingImpl.C2S::handleResizeContainer);
        ServerPlayNetworking.registerGlobalReceiver(SetVisibilityPacket.TYPE, OhmegaNetworkingImpl.C2S::handleSetVisibility);
        ServerPlayNetworking.registerGlobalReceiver(UseAccessoryPacket.TYPE, OhmegaNetworkingImpl.C2S::handleUseAccessory);

        // Registration
        ArgumentTypeRegistry.registerArgumentType(AccessoryTypeArgument.ID, AccessoryTypeArgument.class, SingletonArgumentInfo.contextFree(AccessoryTypeArgument::new));
        OhmegaDataAttachments.init();
        OhmegaDataComponentsImpl.init();
        OhmegaItemsImpl.init();
        OhmegaMenusImpl.init();

        // Resource loader
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Ohmega.id(Ohmega.MODID), AccessoryTypeManager.getInstance());
    }
}