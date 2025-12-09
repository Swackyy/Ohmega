package com.swacky.ohmega.common;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaCommonEvents;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeCapPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryKbPacket;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.resource.v1.ResourceLoaderImpl;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.neoforged.fml.config.ModConfig;

public class Ohmega implements ModInitializer {
    private static ImmutableMap<Item, AccessoryType> accessoryTypeOverrides = ImmutableMap.of();

    @Override
    public void onInitialize() {
        OhmegaCommonEvents.bootstrap();

        ConfigRegistry.INSTANCE.register(OhmegaCommon.MODID, ModConfig.Type.CLIENT, OhmegaConfig.SPEC_CLIENT);
        ConfigRegistry.INSTANCE.register(OhmegaCommon.MODID, ModConfig.Type.SERVER, OhmegaConfig.SPEC_SERVER);

        OhmegaItems.init();
        OhmegaMenus.init();
        OhmegaDataComponents.init();
        OhmegaDataAttachments.init();

        PayloadTypeRegistry.playC2S().register(OpenAccessoryInventoryPacket.TYPE, OpenAccessoryInventoryPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(OpenInventoryPacket.TYPE, OpenInventoryPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ResizeCapPacket.TYPE, ResizeCapPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(UseAccessoryKbPacket.TYPE, UseAccessoryKbPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(OpenAccessoryInventoryPacket.TYPE, OpenAccessoryInventoryPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(OpenInventoryPacket.TYPE, OpenInventoryPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ResizeCapPacket.TYPE, ResizeCapPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(UseAccessoryKbPacket.TYPE, UseAccessoryKbPacket::handle);

        PayloadTypeRegistry.playS2C().register(SyncAccessorySlotsPacket.TYPE, SyncAccessorySlotsPacket.CODEC);
        PayloadTypeRegistry.configurationS2C().register(SyncAccessoryTypesPacket.TYPE, SyncAccessoryTypesPacket.CODEC);

        ResourceLoaderImpl.get(PackType.SERVER_DATA).registerReloader(OhmegaCommon.rl(OhmegaCommon.MODID), AccessoryTypeManager.getInstance());

        accessoryTypeOverrides = OhmegaHooks.accessoryOverrideTypesEvent();
    }

    public static ImmutableMap<Item, AccessoryType> getAccessoryTypeOverrides() {
        return accessoryTypeOverrides;
    }
}