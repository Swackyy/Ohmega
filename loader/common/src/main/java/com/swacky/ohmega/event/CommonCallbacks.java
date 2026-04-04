package com.swacky.ohmega.event;

import com.mojang.brigadier.CommandDispatcher;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.command.OhmegaRootCommand;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Collection;

public final class CommonCallbacks {
    // todo
    public static double getVisibilityPercentModifier(Player player, Entity targetingEntity) {
        double multiplier = 1;

        for (ItemStack stack : AccessoryHelper.getAccessoryStacks(player)) {
            multiplier = Math.min(multiplier, AccessoryHelper.getAccessory(stack.getItem()).getMobVisibilityMultiplier(stack, targetingEntity));
        }

        return multiplier;
    }
    // Ensure alive or 'shouldKeepInventory' returns true before this
    public static void onClonePlayer(Player oldPlayer, Player newPlayer) {
        AccessoryContainer oldA = AccessoryHelper.getContainer(oldPlayer);
        AccessoryContainer newA = AccessoryHelper.getContainer(newPlayer);

        for (int i = 0; i < Math.min(oldA.size(), newA.size()); i++) {
            newA.setStack(newPlayer, i, oldA.getStackInSlot(i), EquipContext.GENERIC, true);
        }
    }

    // Not an event callback in itself
    public static boolean shouldKeepInventory(Player player) {
        return switch (OhmegaConfig.Server.keepAccessoriesBehaviour()) { // Inverse
            case ALWAYS_ON -> true;
            case ALWAYS_OFF -> false;
            case DEFAULT -> player.level() instanceof ServerLevel level && level.getGameRules().get(GameRules.KEEP_INVENTORY);
        };
    }

    public static void onPlayerChangeDimension(ServerPlayer player) {
        AccessoryHelper.getContainer(player).syncAllData(player, player.getId());
    }

    public static void onPlayerDeath(Player player, Collection<ItemEntity> itemDrops) {
        if (!shouldKeepInventory(player)) {
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

                    container.setChanged(i);
                }
            }
        }
    }

    public static void onPlayerPostTick(Player player) {
        AccessoryHelper.getContainer(player).tick(player);
    }

    public static void onPlayerTrack(ServerPlayer tracker, ServerPlayer tracked) {
        AccessoryHelper.getContainer(tracked).syncAllData(tracker, tracked.getId());
    }

    public static void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        OhmegaRootCommand.register(dispatcher, context);
    }
}
