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
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class OhmegaNetworkingImpl {
    public static final class C2S implements OhmegaNetworking.C2S.Service {
        @Override
        public void send(CustomPacketPayload packet) {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();

            if (connection != null) {
                connection.send(packet);
            } else {
                throw new NullPointerException("Minecraft#getConnection returned a null pointer");
            }
        }

        @SuppressWarnings("unused")
        public static void handleOpenAccessoryInventory(OpenAccessoryInventoryPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    ItemStack stack = player.containerMenu.getCarried();

                    if (!stack.isEmpty()) {
                        AbstractContainerMenu.dropOrPlaceInInventory(player, stack);
                        player.containerMenu.setCarried(ItemStack.EMPTY);
                    }

                    player.containerMenu.removed(player);
                    player.openMenu(new SimpleMenuProvider((id, inv, player0) -> new AccessoryInventoryMenu(id, inv), Component.empty()));
                }
            });
        }

        @SuppressWarnings("unused")
        public static void handleOpenInventory(OpenInventoryPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    player.doCloseContainer();
                }
            });
        }

        @SuppressWarnings("unused")
        public static void handleResizeContainer(ResizeContainerPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    AccessoryHelper.getContainer(player).reloadCfg(player);
                }
            });
        }

        public static void handleSetHidden(SetHiddenPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (OhmegaConfig.Server.allowHideAccessories() && context.player() instanceof ServerPlayer player) {
                    int index = packet.index();
                    boolean value = packet.value();

                    AccessoryHelper.getContainer(player).setHidden(index, value);

                    for (ServerPlayer receiver : player.level().getPlayers(player0 -> player0 != player)) {
                        OhmegaNetworking.S2C.send(receiver, new SyncHiddenPacket(player.getId(), new int[]{index}, new boolean[]{value}));
                    }
                }
            });
        }

        public static void handleUseAccessory(UseAccessoryPacket packet, IPayloadContext context) {
            int index = packet.index();

            context.enqueueWork(() -> {
                if (index < AccessoryHelper.getSlotTypes().size()) {
                    Player player = context.player();
                    AccessoryContainer container = AccessoryHelper.getContainer(player);
                    ItemStack stack = container.getStackInSlot(index);
                    IAccessory accessory = AccessoryHelper.getBoundAccessory(stack.getItem());

                    if (accessory != null && !OhmegaHooks.accessoryUseEvent(player, stack)) {
                        accessory.onUse(player, stack);
                    }
                }
            });
        }
    }

    public static final class S2C implements OhmegaNetworking.S2C.Service {
        @Override
        public void send(ServerPlayer receiver, CustomPacketPayload packet) {
            PacketDistributor.sendToPlayer(receiver, packet);
        }

        public static void handleSyncHidden(SyncHiddenPacket packet, IPayloadContext context) {
            int[] indexes = packet.indexes();

            if (indexes.length == 0) {
                return;
            }

            context.enqueueWork(() -> {
                ClientLevel level = Minecraft.getInstance().level;

                if (level != null && level.getEntity(packet.playerId()) instanceof Player player) {
                    AccessoryContainer container = AccessoryHelper.getContainer(player);

                    for (int i = 0; i < indexes.length; i++) {
                        container.setHidden(indexes[i], packet.values()[i]);
                    }
                }
            });
        }

        public static void handleSyncStacks(SyncStacksPacket packet, IPayloadContext context) {
            int[] indexes = packet.indexes();

            if (indexes.length == 0) {
                return;
            }

            context.enqueueWork(() -> {
                ClientLevel level = Minecraft.getInstance().level;

                if (level != null && level.getEntity(packet.playerId()) instanceof Player player) {
                    AccessoryContainer container = AccessoryHelper.getContainer(player);

                    for (int i = 0; i < indexes.length; i++) {
                        container.setStackInSlot(player, indexes[i], packet.stacks().get(i), EquipContext.GENERIC, packet.forceOnEquip());
                    }
                }
            });
        }

        public static void handleSyncTypes(SyncTypesPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                AccessoryTypeManager.apply(packet.types);
                AccessoryTypeManager.applyClient(() -> ClientCallbacks.reloadRegisteredKeybinds(Minecraft.getInstance().options::load), !OhmegaConfig.Server.isLoaded());
            });
        }
    }
}
