package com.swacky.ohmega.common;

import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenusImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.event.CommonEvents;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeContainerPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.config.ModConfig;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public final class Ohmega implements ModInitializer {
    @Override
    public void onInitialize() {
        OhmegaCommon.bootstrap();
        CommonEvents.bootstrap();

        ForgeConfigRegistry.INSTANCE.register(OhmegaCommon.MODID, ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        OhmegaItems.init();
        OhmegaMenusImpl.init();

        ServerLoginNetworking.registerGlobalReceiver(SyncAccessoryTypesPacket.ID, (server, handler, understood, buf, synchroniser, sender) -> {});
        ServerPlayNetworking.registerGlobalReceiver(OpenAccessoryInventoryPacket.ID, (server, player, listener ,buf, sender) -> server.execute(() -> OhmegaNetworkingImpl.C2S.handleOpenAccessoryInventory(OpenAccessoryInventoryPacket.INSTANCE, player)));
        ServerPlayNetworking.registerGlobalReceiver(OpenInventoryPacket.ID, (server, player, listener ,buf, sender) -> server.execute(() -> OhmegaNetworkingImpl.C2S.handleOpenInventory(OpenInventoryPacket.INSTANCE, player)));
        ServerPlayNetworking.registerGlobalReceiver(ResizeContainerPacket.ID, (server, player, listener ,buf, sender) -> server.execute(() -> OhmegaNetworkingImpl.C2S.handleResizeContainer(ResizeContainerPacket.INSTANCE, player)));
        ServerPlayNetworking.registerGlobalReceiver(UseAccessoryPacket.ID, (server, player, listener ,buf, sender) -> {
            UseAccessoryPacket packet = new UseAccessoryPacket(buf);

            server.execute(() -> OhmegaNetworkingImpl.C2S.handleUseAccessory(packet, player));
        });

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