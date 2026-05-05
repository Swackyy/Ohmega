package com.swacky.ohmega.event;

import com.mojang.brigadier.CommandDispatcher;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtensions;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.common.command.OhmegaRootCommand;
import com.swacky.ohmega.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.item.Accessory;
import com.swacky.ohmega.config.OhmegaConfig;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.Collection;
import java.util.List;

public final class CommonCallbacks {
    public static List<Rect2i> getJeiAvoidRects(AbstractContainerScreen<?> screen) {
        if (screen instanceof IAccessoryScreen accessoryScreen) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null && extension.isVisible()) {
                OhmegaConfig.Client.Service.ButtonStyle buttonStyle = OhmegaConfig.Client.buttonStyle();
                IntIntPair buttonPosition = accessoryScreen.getAccessoryExtensionToggleButtonPosition(buttonStyle);

                return List.of(
                        new Rect2i(
                                screen.leftPos + buttonPosition.firstInt(),
                                screen.topPos + buttonPosition.secondInt(),
                                buttonStyle.width,
                                buttonStyle.height),
                        new Rect2i(
                                screen.leftPos + AccessoryScreenExtensions.getAccessoryExtensionX(accessoryScreen),
                                screen.topPos + AccessoryScreenExtensions.getAccessoryExtensionY(accessoryScreen),
                                extension.getWidth(),
                                extension.getHeight()));
            }
        }

        return List.of();
    }

    public static double getVisibilityPercentModifier(LivingEntity entity, Entity targetingEntity) {
        NonNullList<ItemStack> stacks = AccessoryHelper.getData(entity).getStacks();
        double multiplier = 1;

        for (ItemStack stack : stacks) {
            Accessory accessory = Accessories.get(stack.getItem());

            if (accessory != null) {
                multiplier *= accessory.getMobVisibilityMultiplier(stack, targetingEntity);
            }
        }

        return multiplier;
    }

    // Ensure alive or 'shouldKeepInventory' returns true before this
    public static void onClonePlayer(Player oldPlayer, Player newPlayer) {
        AccessoryData oldA = AccessoryHelper.getData(oldPlayer);
        AccessoryData newA = AccessoryHelper.getData(newPlayer);

        for (int i = 0; i < Math.min(oldA.size(), newA.size()); i++) {
            newA.setStacksRange(newPlayer, 0, Math.min(oldA.size(), newA.size()), oldA.getStacks(), EquipContext.SYNC, true);
        }
    }

    public static void onLivingDeath(LivingEntity entity, Collection<ItemEntity> itemDrops) {
        if (!shouldKeepInventory(entity) && entity.level() instanceof ServerLevel level && entity.shouldDropLoot(level)) {
            AccessoryData data = AccessoryHelper.getData(entity);
            NonNullList<ItemStack> stacks = data.getStacks();
            int size = stacks.size();

            for (int i = 0; i < size; i++) {
                ItemStack stack = data.getStackInSlot(i);

                if (!stack.isEmpty()) {
                    ItemEntity itemEntity = entity.createItemStackToDrop(stack, true, false);

                    if (itemEntity != null) {
                        itemEntity.setDefaultPickUpDelay();
                        itemDrops.add(itemEntity);
                    }
                }
            }

            data.setStacksRange(entity, 0, size, NonNullList.withSize(size, ItemStack.EMPTY), EquipContext.DEATH, false);
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
