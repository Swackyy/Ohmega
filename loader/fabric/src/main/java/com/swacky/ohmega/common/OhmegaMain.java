package com.swacky.ohmega.common;

import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypePredicateArgument;
import com.swacky.ohmega.common.init.OhmegaDataAttachmentsImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.event.CommonEvents;
import com.swacky.ohmega.network.C2S.KeybindUsePacket;
import com.swacky.ohmega.network.C2S.SetExtensionVisiblePacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncDataPacket;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncKeybindUsePacket;
import com.swacky.ohmega.network.S2C.SyncSlotsPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
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
        PayloadTypeRegistry.serverboundPlay().register(KeybindUsePacket.TYPE, KeybindUsePacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetExtensionVisiblePacket.TYPE, SetExtensionVisiblePacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetHiddenPacket.TYPE, SetHiddenPacket.CODEC);
        // S2C
        PayloadTypeRegistry.clientboundPlay().register(SyncDataPacket.TYPE, SyncDataPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncHiddenPacket.TYPE, SyncHiddenPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncKeybindUsePacket.TYPE, SyncKeybindUsePacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncSlotsPacket.TYPE, SyncSlotsPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncStacksPacket.TYPE, SyncStacksPacket.CODEC);
        PayloadTypeRegistry.clientboundConfiguration().register(SyncTypesPacket.TYPE, SyncTypesPacket.CODEC);
        // Receive
        ServerPlayNetworking.registerGlobalReceiver(KeybindUsePacket.TYPE, (packet, context) ->
                OhmegaNetworking.C2S.handleKeybindUse(packet, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SetExtensionVisiblePacket.TYPE, (packet, context) ->
                OhmegaNetworking.C2S.handleSetExtensionVisible(packet, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SetHiddenPacket.TYPE, (packet, context) ->
                OhmegaNetworking.C2S.handleSetHidden(packet, context.player()));

        // Registration
        ArgumentTypeRegistry.registerArgumentType(
                Ohmega.id(AccessoryTypeArgument.KEY), AccessoryTypeArgument.class, AccessoryTypeArgument.SERIALISER);
        ArgumentTypeRegistry.registerArgumentType(
                Ohmega.id(AccessoryTypePredicateArgument.KEY), AccessoryTypePredicateArgument.class, AccessoryTypePredicateArgument.SERIALISER);
        OhmegaDataAttachmentsImpl.init();

        // Resource loader
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Ohmega.id(Ohmega.MODID), AccessoryTypeManager.getInstance());
    }
}