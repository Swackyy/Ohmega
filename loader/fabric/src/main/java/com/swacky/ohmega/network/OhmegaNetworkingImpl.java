package com.swacky.ohmega.network;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.common.menu.AccessoryInventoryMenu;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.ClientCallbacks;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeContainerPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
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

            player.openMenu(new SimpleMenuProvider((id, inv, _) -> new AccessoryInventoryMenu(id, inv), Component.empty()));
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

        public static void handleSetHidden(SetHiddenPacket packet, ServerPlayNetworking.Context context) {
            if (OhmegaConfig.Server.allowHideAccessories()) {
                ServerPlayer player = context.player();
                int index = packet.index();
                boolean value = packet.value();

                AccessoryHelper.getContainer(player).setHidden(index, value);

                for (ServerPlayer receiver : player.level().getPlayers(player0 -> player0 != player)) {
                    OhmegaNetworking.S2C.send(receiver, new SyncHiddenPacket(player.getId(), new int[]{index}, new boolean[]{value}));
                }
            }
        }

        public static void handleUseAccessory(UseAccessoryPacket packet, ServerPlayNetworking.Context context) {
            int index = packet.index();

            if (index < AccessoryHelper.getSlotTypes().size()) {
                ServerPlayer player = context.player();
                AccessoryContainer container = AccessoryHelper.getContainer(player);
                ItemStack stack = container.getStackInSlot(index);
                IAccessory accessory = AccessoryHelper.getAccessory(stack.getItem());

                if (accessory != null && !OhmegaHooks.accessoryUseEvent(player, stack)) {
                    accessory.onUse(player, stack);
                }
            }
        }
    }

    public static final class S2C implements OhmegaNetworking.S2C.Service {
        @Override
        public void send(ServerPlayer receiver, CustomPacketPayload packet) {
            ServerPlayNetworking.send(receiver, packet);
        }

        public static void handleSyncHidden(SyncHiddenPacket packet, ClientPlayNetworking.Context context) {
            int[] indexes = packet.indexes();

            if (indexes.length == 0) {
                return;
            }

            ClientLevel level = context.client().level;

            if (level != null && level.getEntity(packet.playerId()) instanceof Player player) {
                AccessoryContainer container = AccessoryHelper.getContainer(player);

                for (int i = 0; i < indexes.length; i++) {
                    container.setHidden(indexes[i], packet.values()[i]);
                }
            }
        }

        public static void handleSyncStacks(SyncStacksPacket packet, ClientPlayNetworking.Context context) {
            int[] indexes = packet.indexes();

            if (indexes.length == 0) {
                return;
            }

            ClientLevel level = context.client().level;

            if (level != null && level.getEntity(packet.playerId()) instanceof Player player) {
                AccessoryContainer container = AccessoryHelper.getContainer(player);

                for (int i = 0; i < indexes.length; i++) {
                    container.setStackInSlot(player, indexes[i], packet.stacks().get(i), EquipContext.GENERIC, true, packet.forceOnEquip());
                }
            }
        }

        @SuppressWarnings("unused")
        public static void handleSyncTypes(SyncTypesPacket packet, ClientConfigurationNetworking.Context context) {
            AccessoryTypeManager.apply(packet.types);
            AccessoryTypeManager.applyClient(() -> ClientCallbacks.reloadRegisteredKeybinds(Minecraft.getInstance().options::load), !OhmegaConfig.Server.isLoaded());
        }
    }
}
