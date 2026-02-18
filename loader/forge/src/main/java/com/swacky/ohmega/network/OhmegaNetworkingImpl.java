package com.swacky.ohmega.network;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.OhmegaCommon;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class OhmegaNetworkingImpl {
    private static SimpleChannel channel;

    public static void bootstrap() {
        SimpleChannel net = ChannelBuilder
                .named(OhmegaCommon.rl("network"))
                .networkProtocolVersion(1)
                .clientAcceptedVersions((status, version) -> true)
                .serverAcceptedVersions((status, version) -> true)
                .simpleChannel();

        int packetId = 0;

        net.messageBuilder(OpenAccessoryInventoryPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenAccessoryInventoryPacket::write)
                .decoder(OpenAccessoryInventoryPacket::new)
                .consumerMainThread(C2S::handleOpenAccessoryInventory)
                .add();
        net.messageBuilder(OpenInventoryPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenInventoryPacket::write)
                .decoder(OpenInventoryPacket::new)
                .consumerMainThread(C2S::handleOpenInventory)
                .add();
        net.messageBuilder(ResizeContainerPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ResizeContainerPacket::write)
                .decoder(ResizeContainerPacket::new)
                .consumerMainThread(C2S::handleResizeContainer)
                .add();
        net.messageBuilder(UseAccessoryPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(UseAccessoryPacket::write)
                .decoder(UseAccessoryPacket::new)
                .consumerMainThread(C2S::handleUseAccessory)
                .add();
        net.messageBuilder(SyncAccessorySlotsPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncAccessorySlotsPacket::write)
                .decoder(SyncAccessorySlotsPacket::new)
                .consumerMainThread(S2C::handleSyncAccessorySlots)
                .add();
        net.messageBuilder(SyncAccessoryTypesPacket.class, packetId, NetworkDirection.LOGIN_TO_CLIENT)
                .encoder(SyncAccessoryTypesPacket::write)
                .decoder(SyncAccessoryTypesPacket::new)
                .consumerMainThread(S2C::handleSyncAccessoryTypes)
                .add();
        OhmegaNetworkingImpl.channel = net;
    }

    public static final class C2S implements OhmegaNetworking.C2S.Service {
        @Override
        public void send(CustomPacketPayload packet) {
            OhmegaNetworkingImpl.channel.send(packet, PacketDistributor.SERVER.noArg());
        }

        @SuppressWarnings("unused")
        public static void handleOpenAccessoryInventory(OpenAccessoryInventoryPacket packet, CustomPayloadEvent.Context context) {
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();

                if (player != null) {
                    ItemStack stack = player.containerMenu.getCarried();

                    if (!stack.isEmpty()) {
                        if (!player.getInventory().add(stack)) {
                            player.drop(stack, false);
                        }

                        player.containerMenu.setCarried(ItemStack.EMPTY);
                    }

                    player.openMenu(new SimpleMenuProvider((id, inv, player0) -> new AccessoryInventoryMenu(id, inv), Component.empty()));
                }
            });

            context.setPacketHandled(true);
        }

        @SuppressWarnings("unused")
        public static void handleOpenInventory(OpenInventoryPacket packet, CustomPayloadEvent.Context context) {
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player != null) {
                    player.doCloseContainer();
                }
            });
            context.setPacketHandled(true);
        }

        @SuppressWarnings("unused")
        public static void handleResizeContainer(ResizeContainerPacket packet, CustomPayloadEvent.Context context) {
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();

                if (player != null) {
                    AccessoryHelper.getContainer(player).reloadCfg(player);
                }
            });

            context.setPacketHandled(true);
        }

        public static void handleUseAccessory(UseAccessoryPacket packet, CustomPayloadEvent.Context context) {
            context.enqueueWork(() -> {
                if (packet.slot() < AccessoryHelper.getSlotTypes().size()) {
                    AccessoryContainer container = AccessoryHelper.getContainer(context.getSender());
                    IAccessory accessory = AccessoryHelper.getBoundAccessory(container.getStackInSlot(packet.slot()).getItem());

                    if (accessory != null) {
                        Player player = context.getSender();

                        if (player != null) {
                            ItemStack stack = container.getStackInSlot(packet.slot());

                            if (!OhmegaHooks.accessoryUseEvent(player, stack)) {
                                accessory.onUse(player, stack);
                            }
                        }
                    }
                }
            });

            context.setPacketHandled(true);
        }
    }

    public static final class S2C implements OhmegaNetworking.S2C.Service {
        @Override
        public void send(ServerPlayer receiver, CustomPacketPayload packet) {
            OhmegaNetworkingImpl.channel.send(packet, PacketDistributor.PLAYER.with(receiver));
        }

        public static void send(Connection connection, Object packet) {
            OhmegaNetworkingImpl.channel.send(packet, connection);
        }

        public static void handleSyncAccessorySlots(SyncAccessorySlotsPacket packet, CustomPayloadEvent.Context context) {
            if (packet.indexes().length == 0) {
                return;
            }

            context.enqueueWork(() -> {
                ClientLevel level = Minecraft.getInstance().level;
                if (level != null) {
                    if (level.getEntity(packet.playerId()) instanceof Player player) {
                        AccessoryContainer container = AccessoryHelper.getContainer(player);

                        for (int i = 0; i < packet.indexes().length; i++) {
                            ItemStack stack = packet.stacks().get(i);
                            int index = packet.indexes()[i];

                            container.setStackInSlot(player, index, stack, EquipContext.GENERIC);
                        }
                    }
                }
            });

            context.setPacketHandled(true);
        }

        public static void handleSyncAccessoryTypes(SyncAccessoryTypesPacket packet, CustomPayloadEvent.Context context) {
            context.enqueueWork(() -> AccessoryTypeManager.getInstance().apply(packet.types()));
            context.setPacketHandled(true);
        }
    }
}
