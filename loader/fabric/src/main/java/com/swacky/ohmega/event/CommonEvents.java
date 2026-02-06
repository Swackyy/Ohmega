package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;

public final class CommonEvents {
    private static boolean bootstrapped;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            ServerPlayerEvents.COPY_FROM.register(CommonEvents::onClonePlayer);
            ServerLivingEntityEvents.AFTER_DEATH.register(CommonEvents::onLivingEntityDeath);
            ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(CommonEvents::onPlayerChangeDimension);
            EntityTrackingEvents.START_TRACKING.register(CommonEvents::onPlayerTrack);
        } else {
            throw new RuntimeException("Cannot bootstrap " + CommonEvents.class.getName() + " multiple times");
        }
    }

    private static void onClonePlayer(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (alive || CommonCallbacks.shouldKeepInventory(oldPlayer)) {
            CommonCallbacks.onClonePlayer(oldPlayer, newPlayer);
        }
    }

    private static void onLivingEntityDeath(LivingEntity entity, DamageSource source) {
        if (entity instanceof ServerPlayer player) {
            AccessoryHelper.getContainer(player).onDeath(player);
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
}
