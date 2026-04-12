package com.swacky.ohmega.event;

import com.mojang.brigadier.CommandDispatcher;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.EquipContext;
import com.swacky.ohmega.common.command.OhmegaRootCommand;
import com.swacky.ohmega.common.dataattachment.AccessoryData;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Collection;

public final class CommonCallbacks {
    public static double getVisibilityPercentModifier(LivingEntity entity, Entity targetingEntity) {
        NonNullList<ItemStack> stacks = AccessoryHelper.getAccessoryStacks(entity);
        double multiplier = 0;

        for (ItemStack stack : stacks) {
            multiplier += AccessoryHelper.getAccessory(stack.getItem()).getMobVisibilityMultiplier(stack, targetingEntity);
        }

        return multiplier / stacks.size();
    }
    // Ensure alive or 'shouldKeepInventory' returns true before this
    public static void onClonePlayer(Player oldPlayer, Player newPlayer) {
        AccessoryData oldA = AccessoryHelper.getData(oldPlayer);
        AccessoryData newA = AccessoryHelper.getData(newPlayer);

        for (int i = 0; i < Math.min(oldA.size(), newA.size()); i++) {
            newA.setStack(newPlayer, i, oldA.getStackInSlot(i), EquipContext.GENERIC, true);
        }
    }

    public static void onLivingDeath(LivingEntity entity, Collection<ItemEntity> itemDrops) {
        if (!shouldKeepInventory(entity)) {
            AccessoryData data = AccessoryHelper.getData(entity);
            NonNullList<ItemStack> stacks = data.getStacks();

            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = stacks.get(i);

                if (!stack.isEmpty()) {
                    data.doUnequip(entity, stack);

                    ItemEntity itemEntity = entity.createItemStackToDrop(stack, true, false);

                    if (itemEntity != null) {
                        itemEntity.setDefaultPickUpDelay();
                        itemDrops.add(itemEntity);
                    }

                    data.setChanged(i);
                }
            }
        }
    }

    public static void onLivingPostTick(LivingEntity living) {
        AccessoryHelper.getData(living).tick(living);
    }

    public static void onLivingTrack(ServerPlayer tracker, LivingEntity tracked) {
        AccessoryHelper.getData(tracked).syncAllData(tracker, tracked.getId());
    }

    public static void onPlayerChangeDimension(ServerPlayer player) {
        AccessoryHelper.getData(player).syncAllData(player, player.getId());
    }

    public static void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        OhmegaRootCommand.register(dispatcher, context);
    }

    // Not an event callback in itself
    public static boolean shouldKeepInventory(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            return switch (OhmegaConfig.Server.keepAccessoriesBehaviour()) {
                case ALWAYS_ON -> true;
                case ALWAYS_OFF -> false;
                case DEFAULT -> player.level().getGameRules().get(GameRules.KEEP_INVENTORY);
            };
        }

        return false;
    }
}
