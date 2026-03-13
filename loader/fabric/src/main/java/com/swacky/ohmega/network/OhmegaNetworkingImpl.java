package com.swacky.ohmega.network;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.EquipContext;
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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class OhmegaNetworkingImpl {
    public static final class C2S implements OhmegaNetworking.C2S.Service {
        @Override
        public void send(OhmegaPacket<?> packet) {
            FriendlyByteBuf buf = PacketByteBufs.create();

            packet.write(buf);
            buf.readerIndex(0);
            buf.retain();
            ClientPlayNetworking.send(packet.id(), buf);
        }

        @SuppressWarnings("unused")
        public static void handleOpenAccessoryInventory(OpenAccessoryInventoryPacket packet, ServerPlayer player) {
            ItemStack stack = player.containerMenu.getCarried();

            if (!stack.isEmpty()) {
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }

                player.containerMenu.setCarried(ItemStack.EMPTY);
            }

            player.openMenu(new SimpleMenuProvider((id, inv, player0) -> new AccessoryInventoryMenu(id, inv), Component.empty()));
        }

        @SuppressWarnings("unused")
        public static void handleOpenInventory(OpenInventoryPacket packet, ServerPlayer player) {
            player.doCloseContainer();
        }

        @SuppressWarnings("unused")
        public static void handleResizeContainer(ResizeContainerPacket packet, ServerPlayer player) {
            AccessoryHelper.getContainer(player).reloadCfg(player);
        }

        public static void handleUseAccessory(UseAccessoryPacket packet, ServerPlayer player) {
            if (packet.slot() < AccessoryHelper.getSlotTypes().size()) {
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
        public void send(ServerPlayer receiver, OhmegaPacket<?> packet) {
            FriendlyByteBuf buf = PacketByteBufs.create();

            packet.write(buf);
            ServerPlayNetworking.send(receiver, packet.id(), buf);
        }

        public static void handleSyncAccessorySlots(SyncAccessorySlotsPacket packet, ClientLevel level) {
            if (level != null) {
                if (level.getEntity(packet.playerId()) instanceof Player player) {
                    AccessoryContainer container = AccessoryHelper.getContainer(player);

                    if (packet.forceOnEquip()) {
                        for (int i = 0; i < packet.indexes().length; i++) {
                            ItemStack stack = packet.stacks().get(i);
                            int index = packet.indexes()[i];

                            container.setStackInSlot(player, index, stack, EquipContext.GENERIC);
                        }
                    } else {
                        container.syncSlots(player, packet.indexes(), packet.stacks());
                    }
                }
            }
        }

        @SuppressWarnings("unused")
        public static void handleSyncAccessoryTypes(SyncAccessoryTypesPacket packet) {
            AccessoryTypeManager.getInstance().apply(packet.types());
        }
    }
}
