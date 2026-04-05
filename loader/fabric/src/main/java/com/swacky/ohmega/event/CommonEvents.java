package com.swacky.ohmega.event;

import com.mojang.brigadier.CommandDispatcher;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class CommonEvents {
    private static boolean bootstrapped = false;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            ServerPlayerEvents.COPY_FROM.register(CommonEvents::onClonePlayer);
            ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(CommonEvents::onPlayerChangeDimension);
            ServerPlayerEvents.JOIN.register(CommonEvents::onPlayerJoin);
            EntityTrackingEvents.START_TRACKING.register(CommonEvents::onPlayerTrack);
            CommandRegistrationCallback.EVENT.register(CommonEvents::onRegisterCommands);
            ItemEvents.USE.register(CommonEvents::onUseItem);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + CommonEvents.class.getName() + " multiple times");
        }
    }

    private static void onClonePlayer(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (alive || CommonCallbacks.shouldKeepInventory(oldPlayer)) {
            CommonCallbacks.onClonePlayer(oldPlayer, newPlayer);
        }
    }

    private static void onPlayerChangeDimension(ServerPlayer player, ServerLevel from, ServerLevel to) {
        CommonCallbacks.onPlayerChangeDimension(player);
    }

    private static void onPlayerJoin(ServerPlayer player) {
        OhmegaNetworking.S2C.send(player, new SyncTypesPacket());
        AccessoryHelper.getData(player).onAttach(player);
    }

    private static void onPlayerTrack(Entity entity, ServerPlayer tracker) {
        if (entity instanceof ServerPlayer tracked) {
            CommonCallbacks.onPlayerTrack(tracker, tracked);
        }
    }

    private static void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        CommonCallbacks.onRegisterCommands(dispatcher, context);
    }

    private static InteractionResult onUseItem(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();

        if (AccessoryHelper.isAccessory(stack.getItem()) && !AccessoryHelper.getAccessory(item).preferVanillaUse(stack)) {
            InteractionResult candidate = AccessoryHelper.tryEquip(player, stack);

            if (candidate.consumesAction()) {
                return candidate;
            }
        }

        return null;
    }
}
