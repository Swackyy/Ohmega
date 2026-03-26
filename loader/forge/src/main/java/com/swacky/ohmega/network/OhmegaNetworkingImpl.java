package com.swacky.ohmega.network;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.ClientCallbacks;
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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class OhmegaNetworkingImpl {
    private static SimpleChannel channel;

    public static void bootstrap() {
        SimpleChannel net = ChannelBuilder
                .named(Ohmega.id("network"))
                .networkProtocolVersion(1)
                .clientAcceptedVersions((_, _) -> true)
                .serverAcceptedVersions((_, _) -> true)
                .simpleChannel();

        net.play().serverbound().addMain(OpenAccessoryInventoryPacket.class, OpenAccessoryInventoryPacket.CODEC, C2S::handleOpenAccessoryInventory);
        net.play().serverbound().addMain(OpenInventoryPacket.class, OpenInventoryPacket.CODEC, C2S::handleOpenInventory);
        net.play().serverbound().addMain(ResizeContainerPacket.class, ResizeContainerPacket.CODEC, C2S::handleResizeContainer);
        net.play().serverbound().addMain(UseAccessoryPacket.class, UseAccessoryPacket.CODEC, C2S::handleUseAccessory);

        net.play().clientbound().addMain(SyncAccessorySlotsPacket.class, SyncAccessorySlotsPacket.CODEC, S2C::handleSyncAccessorySlots);
        net.configuration().clientbound().addMain(SyncAccessoryTypesPacket.class, SyncAccessoryTypesPacket.CODEC, S2C::handleSyncAccessoryTypes);

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
                        AbstractContainerMenu.dropOrPlaceInInventory(player, stack);
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

        public static void send(Connection connection, CustomPacketPayload packet) {
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
                        AccessoryHelper.getContainer(player).syncSlots(player, packet.indexes(), packet.stacks());
                    }
                }
            });

            context.setPacketHandled(true);
        }

        public static void handleSyncAccessoryTypes(SyncAccessoryTypesPacket packet, CustomPayloadEvent.Context context) {
            context.enqueueWork(() -> {
                AccessoryTypeManager.apply(packet.types);
                AccessoryTypeManager.applyClient(() -> ClientCallbacks.reloadRegisteredKeybinds(() -> Minecraft.getInstance().options.load(true)), !OhmegaConfig.Server.isLoaded());
            });

            context.setPacketHandled(true);
        }
    }
}
