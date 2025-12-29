package com.swacky.ohmega.network;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeContainerPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class OhmegaNetworkingImpl {
    public static final class C2S implements OhmegaNetworking.C2S.Service {
        @Override
        public void send(CustomPacketPayload packet) {
            ClientPlayNetworking.send(packet);
        }

        @SuppressWarnings("unused")
        public static void handleOpenAccessoryInventory(OpenAccessoryInventoryPacket packet, ServerPlayNetworking.Context context) {
            ServerPlayer player = context.player();
            ItemStack stack = player.containerMenu.getCarried();
            if (!stack.isEmpty()) {
                AbstractContainerMenu.dropOrPlaceInInventory(player, stack);
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }

            context.player().openMenu(new SimpleMenuProvider((id, inv, player0) -> new AccessoryInventoryMenu(id, inv), Component.empty()));
        }

        @SuppressWarnings("unused")
        public static void handleOpenInventory(OpenInventoryPacket packet, ServerPlayNetworking.Context context) {
            context.player().doCloseContainer();
        }

        @SuppressWarnings("unused")
        public static void handleResizeContainer(ResizeContainerPacket packet, ServerPlayNetworking.Context context) {
            ServerPlayer player = context.player();
            AccessoryHelper.getContainer(player).reloadCfg(player);
        }

        public static void handleUseAccessory(UseAccessoryPacket packet, ServerPlayNetworking.Context context) {
            if (packet.slot() < AccessoryHelper.getSlotTypes().size()) {
                ServerPlayer player = context.player();
                AccessoryContainer container = AccessoryHelper.getContainer(player);
                IAccessory accessory = AccessoryHelper.getBoundAccessory(container.getStackInSlot(packet.slot()).getItem());

                if (accessory != null) {
                    ItemStack stack = container.getStackInSlot(packet.slot());

                    if (!OhmegaHooks.accessoryUseEvent(player, stack)) {
                        accessory.onUse(player, stack);
                    }
                }
            }
        }
    }

    public static final class S2C implements OhmegaNetworking.S2C.Service {
        @Override
        public void send(ServerPlayer receiver, CustomPacketPayload packet) {
            ServerPlayNetworking.send(receiver, packet);
        }

        public static void handleSyncAccessorySlots(SyncAccessorySlotsPacket packet, ClientPlayNetworking.Context context) {
            ClientLevel level = context.client().level;

            if (level != null) {
                if (level.getEntity(packet.playerId()) instanceof Player player) {
                    AccessoryHelper.getContainer(player).syncSlots(player, packet.indexes(), packet.stacks());
                }
            }
        }

        @SuppressWarnings("unused")
        public static void handleSyncAccessoryTypes(SyncAccessoryTypesPacket packet, ClientConfigurationNetworking.Context context) {
            AccessoryTypeManager.getInstance().apply(packet.types);
        }
    }
}
