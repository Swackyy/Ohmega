package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Collection;
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
            case DEFAULT -> player.level() instanceof ServerLevel level && level.getGameRules().get(GameRules.KEEP_INVENTORY);
        };
    }

    // Ensure alive or 'shouldKeepInventory' returns true before this
    public static void onClonePlayer(Player oldPlayer, Player newPlayer) {
        AccessoryContainer oldA = AccessoryHelper.getContainer(oldPlayer);
        AccessoryContainer newA = AccessoryHelper.getContainer(newPlayer);

        for (int i = 0; i < Math.min(oldA.getSlots(), newA.getSlots()); i++) {
            newA.setStackInSlot(newPlayer, i, oldA.getStackInSlot(i), EquipContext.GENERIC);
        }
    }

    public static void onPlayerDeath(Player player, Collection<ItemEntity> itemDrops) {
        if (!shouldKeepInventory(player) && player.level() instanceof ServerLevel level && player.shouldDropLoot(level)) {
            AccessoryContainer container = AccessoryHelper.getContainer(player);
            NonNullList<ItemStack> stacks = container.getStacks();

            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);

                if (!stack.isEmpty()) {
                    container.doUnequip(player, stack);

                    ItemEntity entity = player.createItemStackToDrop(stack, true, true);

                    if (entity != null) {
                        itemDrops.add(entity);
                    }

                    container.onContentsChanged(i);
                }
            }
        }
    }
}
