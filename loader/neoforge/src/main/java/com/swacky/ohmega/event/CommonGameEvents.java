package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.AccessoryEquipEvent;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Collections;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = OhmegaCommon.MODID)
public class CommonGameEvents {
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.syncAllSlots(player, Collections.singletonList(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer player && event.getEntity() instanceof ServerPlayer player0) {
            AccessoryHelper.syncAllSlots(player, Collections.singletonList(player0));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        event.getEntity().getData(OhmegaDataAttachments.ACCESSORY_HANDLER.get()).tick();
    }

    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        boolean flag = switch (OhmegaConfig.CONFIG_SERVER.keepAccessories.get()) { // Inverse
            case ON -> false;
            case OFF -> true;
            case DEFAULT -> oldPlayer.getServer() == null || !oldPlayer.getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        };

        if (event.isWasDeath() || flag) {
            AccessoryContainer oldA = AccessoryHelper.getContainer(oldPlayer);
            AccessoryContainer newA = AccessoryHelper.getContainer(newPlayer);

            for (int i = 0; i < newA.getSlots(); i++) {
                ItemStack stack = oldA.getStackInSlot(i);
                newA.setStackInSlot(i, stack);
                IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                if (acc != null) {
                    AccessoryHelper.changeModifiers(newPlayer, AccessoryHelper.getModifiers(stack).getPassive(), true);

                    if (!OhmegaHooks.accessoryEquipEvent(newPlayer, stack, AccessoryEquipEvent.Context.GENERIC).isCanceled()) {
                        acc.onEquip(newPlayer, stack);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            boolean flag = switch (OhmegaConfig.CONFIG_SERVER.keepAccessories.get()) { // Inverse
                case ON -> false;
                case OFF -> true;
                case DEFAULT -> player.getServer() == null || !player.getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            };

            if (player.getServer() != null && flag) {
                player.getData(OhmegaDataAttachments.ACCESSORY_HANDLER.get()).invalidate();
            }
        }
    }

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        InteractionResult result = AccessoryHelper.tryEquip(event.getEntity(), event.getHand()).getResult();
        if (result == InteractionResult.SUCCESS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void addResourceReloadListeners(AddReloadListenerEvent event) {
        event.addListener(AccessoryTypeManager.getInstance());
    }
}
