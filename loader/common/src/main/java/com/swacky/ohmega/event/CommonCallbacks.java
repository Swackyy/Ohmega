package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

import java.util.Collections;

public final class CommonCallbacks {
    public static void onPlayerTrack(ServerPlayer tracker, ServerPlayer tracked) {
        AccessoryHelper.syncAllSlots(tracked, Collections.singleton(tracker));
    }

    public static void onPlayerPostTick(Player player) {
        AccessoryHelper.getContainer(player).tick(player);
    }

    // Not an event callback in itself
    public static boolean shouldKeepInventory(Player player) {
        return switch (OhmegaConfig.Server.keepAccessoriesBehaviour()) { // Inverse
            case ALWAYS_ON -> true;
            case ALWAYS_OFF -> false;
            case DEFAULT -> player.level() instanceof ServerLevel level && level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        };
    }

    // Ensure alive or 'shouldKeepInventory' returns true before this
    public static void onClonePlayer(Player oldPlayer, Player newPlayer) {
        AccessoryContainer oldA = AccessoryHelper.getContainer(oldPlayer);
        AccessoryContainer newA = AccessoryHelper.getContainer(newPlayer);
        NonNullList<ItemStack> stacks = oldA.getStacks();
        int size = stacks.size();
        int[] slots = new int[size];

        for (int i = 0; i < size; i++) {
            slots[i] = i;
        }

        newA.syncSlots(newPlayer, slots, stacks);

        if (newPlayer instanceof ServerPlayer svr) {
            AccessoryHelper.syncAllSlots(svr, Collections.singleton(svr));
        }
    }
}
