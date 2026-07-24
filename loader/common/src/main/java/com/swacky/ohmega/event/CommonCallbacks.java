package com.swacky.ohmega.event;

import com.mojang.brigadier.CommandDispatcher;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import com.swacky.ohmega.common.command.OhmegaRootCommand;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncDataPacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CommonCallbacks {
    public static double getVisibilityPercentModifier(LivingEntity entity, Entity targetingEntity) {
        double multiplier = 1;

        for (AccessoryDataEntry entry : OhmegaDataAttachments.getData(entity).getEntries()) {
            ItemStack stack = entry.getStack();
            Accessory accessory = Accessories.get(stack.getItem());

            if (accessory != null) {
                multiplier *= accessory.getMobVisibilityMultiplier(stack, targetingEntity);
            }
        }

        return multiplier;
    }

    public static void onClonePlayer(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        AccessoryData data = OhmegaDataAttachments.getData(newPlayer);

        if (alive || shouldKeepInventory(oldPlayer)) {
            data.copyFrom(OhmegaDataAttachments.getData(oldPlayer), false);
        }

        AccessoryMenus.rebuildSlots(newPlayer.inventoryMenu, newPlayer);
    }

    public static void onLivingDeath(LivingEntity entity, Collection<ItemEntity> itemDrops) {
        if (!shouldKeepInventory(entity) && entity.level() instanceof ServerLevel level && entity.shouldDropLoot(level)) {
            AccessoryData data = OhmegaDataAttachments.getData(entity);
            int size = data.size();

            for (AccessoryDataEntry entry : data.getEntries()) {
                ItemStack stack = entry.getStack();

                if (!stack.isEmpty()) {
                    ItemEntity itemEntity = entity.createItemStackToDrop(stack, true, false);

                    if (itemEntity != null) {
                        itemEntity.setDefaultPickUpDelay();
                        itemDrops.add(itemEntity);
                    }
                }
            }

            List<ItemStack> stacks0 = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                stacks0.add(ItemStack.EMPTY);
            }

            data.setStacksRange(entity, 0, size, stacks0, EquipContext.DEATH, false);
        }

        AccessoryData.DEFAULT_TRACKERS.remove(entity);
    }

    public static void onLivingPostTick(LivingEntity living) {
        OhmegaDataAttachments.getData(living).tick(living);
    }

    public static void onLivingTrack(ServerPlayer tracker, LivingEntity tracked) {
        OhmegaNetworking.sendS2C(tracker, new SyncDataPacket(tracked.getId(), OhmegaDataAttachments.getData(tracked)));
    }

    public static void onPlayerChangeDimension(ServerPlayer player) {
        OhmegaNetworking.sendS2C(player, new SyncDataPacket(player.getId(), OhmegaDataAttachments.getData(player)));
    }

    public static void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        OhmegaRootCommand.register(dispatcher, context);
    }

    public static void onServerConfigLoad() {
        Accessories.surveyRegistry();
        OhmegaConfig.Server.revalidateCached();
    }

    public static void onServerConfigReload() {
        OhmegaConfig.Server.getData().pull();
    }

    public static void onSetupAccessoryTypeManager() {
        AccessoryTypeManager.unlockEvents();
        OhmegaHooks.accessoryBind();
        AccessoryTypeManager.postOverrideTypes();
    }

    public static boolean shouldKeepInventory(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            OhmegaConfig.Server.Service.KeepAccessoriesBehaviour behaviour = OhmegaConfig.Server.getData().keepAccessoriesBehaviour().getObject();

            if (behaviour != null) {
                return switch (behaviour) {
                    case ALWAYS_ON -> true;
                    case ALWAYS_OFF -> false;
                    case DEFAULT -> player.level().getGameRules().get(GameRules.KEEP_INVENTORY);
                };
            }
        }

        return false;
    }
}
