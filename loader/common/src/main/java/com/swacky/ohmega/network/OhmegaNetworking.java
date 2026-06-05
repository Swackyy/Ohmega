package com.swacky.ohmega.network;

import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.ClientCallbacks;
import com.swacky.ohmega.network.C2S.SetExtensionVisiblePacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.network.S2C.SyncUsePacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class OhmegaNetworking {
    public static final class C2S {
        private static final Service IMPL = Ohmega.loadService(Service.class);

        public static void bootstrap() {}

        public static void send(CustomPacketPayload packet) {
            IMPL.send(packet);
        }

        public static void send(LocalPlayer player, Packet<?> packet) {
            player.connection.send(packet);
        }

        public static void handleReloadContainer(ServerPlayer player) {
            AccessoryHelper.getData(player).reload(player);
        }

        public static void handleSetExtensionVisible(SetExtensionVisiblePacket packet, ServerPlayer player) {
            if (player.containerMenu instanceof IAccessoryMenu menu) {
                menu.setAccessoryExtensionVisible(packet.value());
            }
        }

        public static void handleSetHidden(SetHiddenPacket packet, ServerPlayer player) {
            if (OhmegaConfig.Server.getData().allowHideAccessories().get()) {
                int index = packet.index();
                boolean value = packet.value();

                if (index < AccessoryHelper.getSlotTypes().size()) {
                    AccessoryHelper.getData(player).setHidden(index, value);

                    for (ServerPlayer receiver : player.level().getPlayers(player0 -> player0 != player)) {
                        OhmegaNetworking.S2C.send(receiver, new SyncHiddenPacket(player.getId(), new int[]{index}, new boolean[]{value}));
                    }
                }
            }
        }

        public static void handleUseAccessory(UseAccessoryPacket packet, ServerPlayer player) {
            int index = packet.index();

            if (index < AccessoryHelper.getSlotTypes().size()) {
                AccessoryData data = AccessoryHelper.getData(player);
                ItemStack stack = data.getStackInSlot(index);
                Accessory accessory = Accessories.get(stack.getItem());

                if (accessory != null) {
                    accessory.onKeybindUse(player, stack);
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

                if (level.getEntity(packet.entityId()) instanceof LivingEntity entity) {
                    AccessoryData data = AccessoryHelper.getData(entity);

                    for (int i = 0; i < indexes.length; i++) {
                        int index = indexes[i];

                        if (index < data.size()) {
                            data.setHidden(index, packet.values()[i]);
                        }
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

                if (level.getEntity(packet.entityId()) instanceof LivingEntity entity) {
                    AccessoryHelper.getData(entity).setStacks(entity, packet.indexes(), packet.stacks(), EquipContext.SYNC, packet.forceOnEquip());
                }
            }
        }

        public static void handleSyncTypes(SyncTypesPacket packet, RegistryAccess lookup) {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), lookup);

            buf.writeBytes(packet.data());
            AccessoryTypeManager.apply(OhmegaByteBufCodecs.ACCESSORY_TYPE_COLLECTION.decode(buf));
            AccessoryTypeManager.applyClient(() -> ClientCallbacks.reloadRegisteredKeybinds(Minecraft.getInstance().options::load), !OhmegaConfig.Server.isLoaded());
        }

        public static void handleSyncUse(SyncUsePacket packet) {
            ClientLevel level = Minecraft.getInstance().level;

            if (level != null && level.getEntity(packet.entityId()) instanceof Player player) {
                ItemStack stack = AccessoryHelper.getData(player).getStackInSlot(packet.index());
                Accessory accessory = Accessories.get(stack.getItem());

                if (accessory != null) {
                    accessory.onKeybindUse(player, stack);
                }
            }
        }

        public interface Service {
            void send(ServerPlayer receiver, CustomPacketPayload packet);
        }
    }
}
