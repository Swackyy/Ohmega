package com.swacky.ohmega.common;

import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.IAccessory;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.event.CommonEvents;
import com.swacky.ohmega.network.C2S.ReloadDataPacket;
import com.swacky.ohmega.network.C2S.SetExtensionVisiblePacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.network.S2C.SyncUsePacket;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.config.ModConfig;
import org.jspecify.annotations.NonNull;

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
        PayloadTypeRegistry.serverboundPlay().register(SetExtensionVisiblePacket.TYPE, SetExtensionVisiblePacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ReloadDataPacket.TYPE, ReloadDataPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetHiddenPacket.TYPE, SetHiddenPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(UseAccessoryPacket.TYPE, UseAccessoryPacket.CODEC);
        // S2C
        PayloadTypeRegistry.clientboundPlay().register(SyncHiddenPacket.TYPE, SyncHiddenPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncStacksPacket.TYPE, SyncStacksPacket.CODEC);
        PayloadTypeRegistry.clientboundConfiguration().register(SyncTypesPacket.TYPE, SyncTypesPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncUsePacket.TYPE, SyncUsePacket.CODEC);
        // Receive
        ServerPlayNetworking.registerGlobalReceiver(ReloadDataPacket.TYPE, (_, context) ->
                OhmegaNetworking.C2S.handleReloadContainer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SetExtensionVisiblePacket.TYPE, (packet, context) ->
                OhmegaNetworking.C2S.handleSetExtensionVisible(packet, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SetHiddenPacket.TYPE, (packet, context) ->
                OhmegaNetworking.C2S.handleSetHidden(packet, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(UseAccessoryPacket.TYPE, (packet, context) ->
                OhmegaNetworking.C2S.handleUseAccessory(packet, context.player()));

        // Registration
        ArgumentTypeRegistry.registerArgumentType(Ohmega.id(AccessoryTypeArgument.KEY), AccessoryTypeArgument.class, SingletonArgumentInfo.contextFree(AccessoryTypeArgument::new));
        OhmegaDataAttachments.init();

        // Resource loader
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Ohmega.id(Ohmega.MODID), AccessoryTypeManager.getInstance());

        Accessories.bind(Items.SUGAR, new IAccessory() {
            @Override
            public void onEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
                System.out.println("wsg");
            }
        });
    }
}