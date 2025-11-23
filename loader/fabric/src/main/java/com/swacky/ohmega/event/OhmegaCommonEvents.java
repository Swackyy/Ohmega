package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.AccessoryEquipCallback;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.config.OhmegaConfig;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public class OhmegaCommonEvents {
    private static boolean bootstrapped = false;
    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;
            ServerPlayConnectionEvents.JOIN.register(OhmegaCommonEvents::onPlayerJoin);
            EntityTrackingEvents.START_TRACKING.register(OhmegaCommonEvents::onPlayerTrack);
            ServerPlayerEvents.COPY_FROM.register(OhmegaCommonEvents::onClonePlayer);
            UseItemCallback.EVENT.register(OhmegaCommonEvents::onItemRightClick);
        }
    }

    @SuppressWarnings("resource")
    private static void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        ServerPlayer player = handler.getPlayer();
        List<ServerPlayer> receivers = player.serverLevel().players();

        receivers.add(player);
        AccessoryHelper.syncAllSlots(player, receivers);
    }

    private static void onPlayerTrack(Entity tracked, ServerPlayer tracker) {
        if (tracked instanceof ServerPlayer player) {
            AccessoryHelper.syncAllSlots(player, Collections.singletonList(tracker));
        }
    }

    private static void onClonePlayer(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        boolean flag = switch (OhmegaConfig.CONFIG_SERVER.keepAccessories.get()) { // Inverse
            case ON -> false;
            case OFF -> true;
            case DEFAULT -> oldPlayer.getServer() == null || !oldPlayer.getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        };

        if (!alive || flag) {
            AccessoryContainer oldA = AccessoryHelper.getContainer(oldPlayer);
            AccessoryContainer newA = AccessoryHelper.getContainer(newPlayer);

            for (int i = 0; i < newA.getSlots(); i++) {
                ItemStack stack = oldA.getStackInSlot(i);
                newA.setStackInSlot(i, stack);
                IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                if (acc != null) {
                    AccessoryHelper.changeModifiers(newPlayer, AccessoryHelper.getModifiers(stack).getPassive(), true);

                    if (!OhmegaHooks.accessoryEquipEvent(newPlayer, stack, AccessoryEquipCallback.Context.GENERIC).isCanceled()) {
                        acc.onEquip(newPlayer, stack);
                    }
                    AccessoryHelper.setSlot(stack, i);
                }
            }
        }
    }

    private static InteractionResult onItemRightClick(Player player, Level level, InteractionHand hand) {
        InteractionResult result = AccessoryHelper.tryEquip(player, hand);
        if (result == InteractionResult.SUCCESS) {
            return result;
        }
        return InteractionResult.PASS;
    }
}
