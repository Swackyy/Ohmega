package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Collections;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = OhmegaCommon.MODID)
public final class CommonGameEvents {
    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();

        if (!event.isWasDeath() || CommonCallbacks.shouldKeepInventory(oldPlayer)) {
            Player newPlayer = event.getEntity();

            CommonCallbacks.onClonePlayer(oldPlayer, newPlayer);
        }
    }

    @SubscribeEvent
    public static void onLivingEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.getContainer(player).onDeath(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.syncAllSlots(player, Collections.singleton(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerPostTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CommonCallbacks.onPlayerPostTick(event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer tracked && event.getEntity() instanceof ServerPlayer tracker) {
            CommonCallbacks.onPlayerTrack(tracker, tracked);
        }
    }

    @SubscribeEvent
    public static void onRegisterServerReloadListeners(AddReloadListenerEvent event) {
        event.addListener(AccessoryTypeManager.getInstance());
    }
}
