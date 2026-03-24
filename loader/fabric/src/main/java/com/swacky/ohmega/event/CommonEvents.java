package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.world.entity.Entity;

import java.util.Collections;

public final class CommonEvents {
    private static boolean bootstrapped;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            ServerPlayerEvents.COPY_FROM.register(CommonEvents::onClonePlayer);
            ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(CommonEvents::onPlayerChangeDimension);
            EntityTrackingEvents.START_TRACKING.register(CommonEvents::onPlayerTrack);
            ServerConfigurationConnectionEvents.CONFIGURE.register(CommonEvents::onServerConfigure);
        } else {
            throw new RuntimeException("Cannot bootstrap " + CommonEvents.class.getName() + " multiple times");
        }
    }

    private static void onClonePlayer(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (alive || CommonCallbacks.shouldKeepInventory(oldPlayer)) {
            CommonCallbacks.onClonePlayer(oldPlayer, newPlayer);
        }
    }

    public static void onPlayerChangeDimension(ServerPlayer player, ServerLevel from, ServerLevel to) {
        AccessoryHelper.syncAllSlots(player, Collections.singleton(player));
    }

    private static void onPlayerTrack(Entity entity, ServerPlayer tracker) {
        if (entity instanceof ServerPlayer tracked) {
            CommonCallbacks.onPlayerTrack(tracker, tracked);
        }
    }

    private static void onServerConfigure(ServerConfigurationPacketListenerImpl handler, MinecraftServer server) {
        handler.send(new ClientboundCustomPayloadPacket(new SyncAccessoryTypesPacket()));
    }
}
