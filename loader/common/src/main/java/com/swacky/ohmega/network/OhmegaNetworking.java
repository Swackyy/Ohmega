package com.swacky.ohmega.network;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.common.menu.AccessoryInventoryMenu;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.ClientCallbacks;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.network.S2C.SyncUsePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

// todo: will probably need to add entityIds to a lot of packets for allowing entities to have accessory inventories
// todo: reorder packet registration on forge and neoforge to be alphabetical
public final class OhmegaNetworking {
    public static void bootstrap() {
        C2S.bootstrap();
        S2C.bootstrap();
    }

    public static final class C2S {
        private static final Service IMPL = Ohmega.loadService(Service.class);

        public static void bootstrap() {}

        public static void send(CustomPacketPayload packet) {
            IMPL.send(packet);
        }

        public static void send(LocalPlayer player, Packet<?> packet) {
            player.connection.send(packet);
        }

        public static void handleOpenAccessoryInventory(ServerPlayer player) {
            ItemStack stack = player.containerMenu.getCarried();

            if (!stack.isEmpty()) {
                AbstractContainerMenu.dropOrPlaceInInventory(player, stack);
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }

            player.openMenu(new SimpleMenuProvider((id, inv, _) -> new AccessoryInventoryMenu(id, inv), Component.empty()));
        }

        public static void handleReloadContainer(ServerPlayer player) {
            AccessoryHelper.getContainer(player).reload(player);
        }

        public static void handleSetHidden(SetHiddenPacket packet, ServerPlayer player) {
            if (OhmegaConfig.Server.allowHideAccessories()) {
                int index = packet.index();
                boolean value = packet.value();

                if (index < AccessoryHelper.getSlotTypes().size()) {
                    AccessoryHelper.getContainer(player).setHidden(index, value);

                    for (ServerPlayer receiver : player.level().getPlayers(player0 -> player0 != player)) {
                        OhmegaNetworking.S2C.send(receiver, new SyncHiddenPacket(player.getId(), new int[]{index}, new boolean[]{value}));
                    }
                }
            }
        }

        public static void handleUseAccessory(UseAccessoryPacket packet, ServerPlayer player) {
            int index = packet.index();

            if (index < AccessoryHelper.getSlotTypes().size()) {
                AccessoryContainer container = AccessoryHelper.getContainer(player);
                ItemStack stack = container.getStackInSlot(index);
                IAccessory accessory = AccessoryHelper.getAccessory(stack.getItem());

                if (accessory != null && !OhmegaHooks.accessoryUseEvent(player, stack)) {
                    accessory.onUse(player, stack);
                }

                for (ServerPlayer receiver : player.level().getPlayers(player0 -> player0 != player)) {
                    OhmegaNetworking.S2C.send(receiver, new SyncUsePacket(player.getId(), index));
                }
            }
        }

        public interface Service {
            void send(CustomPacketPayload packet);
        }
    }

    public static final class S2C {
        private static final Service IMPL = Ohmega.loadService(Service.class);

        public static void bootstrap() {}

        public static void send(ServerPlayer receiver, CustomPacketPayload packet) {
            IMPL.send(receiver, packet);
        }

        public static void handleSyncHidden(SyncHiddenPacket packet) {
            ClientLevel level = Minecraft.getInstance().level;

            if (level != null) {
                int[] indexes = packet.indexes();

                if (indexes.length == 0) {
                    return;
                }

                if (level.getEntity(packet.playerId()) instanceof Player player) {
                    AccessoryContainer container = AccessoryHelper.getContainer(player);

                    for (int i = 0; i < indexes.length; i++) {
                        container.setHidden(indexes[i], packet.values()[i]);
                    }
                }
            }
        }

        public static void handleSyncStacks(SyncStacksPacket packet) {
            ClientLevel level = Minecraft.getInstance().level;

            if (level != null) {
                int[] indexes = packet.indexes();

                if (indexes.length == 0) {
                    return;
                }

                if (level.getEntity(packet.playerId()) instanceof Player player) {
                    AccessoryContainer container = AccessoryHelper.getContainer(player);

                    for (int i = 0; i < indexes.length; i++) {
                        container.setStack(player, indexes[i], packet.stacks().get(i), EquipContext.GENERIC, true, packet.forceOnEquip());
                    }
                }
            }
        }

        public static void handleSyncTypes(SyncTypesPacket packet) {
            AccessoryTypeManager.apply(packet.types);
            AccessoryTypeManager.applyClient(() -> ClientCallbacks.reloadRegisteredKeybinds(Minecraft.getInstance().options::load), !OhmegaConfig.Server.isLoaded());
        }

        public static void handleSyncUse(SyncUsePacket packet) {
            ClientLevel level = Minecraft.getInstance().level;

            if (level != null && level.getEntity(packet.playerId()) instanceof Player player) {
                ItemStack stack = AccessoryHelper.getContainer(player).getStackInSlot(packet.index());

                AccessoryHelper.getAccessory(stack.getItem()).onUse(player, stack);
            }
        }

        public interface Service {
            void send(ServerPlayer receiver, CustomPacketPayload packet);
        }
    }
}
