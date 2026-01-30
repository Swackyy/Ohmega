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
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.config.ModConfig;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public final class Ohmega implements ModInitializer {
    @Override
    public void onInitialize() {
        OhmegaCommon.bootstrap();
        CommonEvents.bootstrap();

        NeoForgeConfigRegistry.INSTANCE.register(OhmegaCommon.MODID, ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        OhmegaDataAttachments.init();
        OhmegaDataComponentsImpl.init();
        OhmegaItems.init();
        OhmegaMenusImpl.init();

        PayloadTypeRegistry.playC2S().register(OpenAccessoryInventoryPacket.TYPE, OpenAccessoryInventoryPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(OpenInventoryPacket.TYPE, OpenInventoryPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ResizeContainerPacket.TYPE, ResizeContainerPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(UseAccessoryPacket.TYPE, UseAccessoryPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(OpenAccessoryInventoryPacket.TYPE, OhmegaNetworkingImpl.C2S::handleOpenAccessoryInventory);
        ServerPlayNetworking.registerGlobalReceiver(OpenInventoryPacket.TYPE, OhmegaNetworkingImpl.C2S::handleOpenInventory);
        ServerPlayNetworking.registerGlobalReceiver(ResizeContainerPacket.TYPE, OhmegaNetworkingImpl.C2S::handleResizeContainer);
        ServerPlayNetworking.registerGlobalReceiver(UseAccessoryPacket.TYPE, OhmegaNetworkingImpl.C2S::handleUseAccessory);

        PayloadTypeRegistry.playS2C().register(SyncAccessorySlotsPacket.TYPE, SyncAccessorySlotsPacket.CODEC);
        PayloadTypeRegistry.configurationS2C().register(SyncAccessoryTypesPacket.TYPE, SyncAccessoryTypesPacket.CODEC);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return OhmegaCommon.RELOAD_LISTENER_ID;
            }

            @Override
            public @NonNull CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                AccessoryTypeManager.getInstance().reload(barrier, manager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
                return CompletableFuture.allOf();
            }
        });
    }
}