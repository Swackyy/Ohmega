package com.swacky.ohmega.network;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.ClientCallbacks;
import com.swacky.ohmega.network.C2S.KeybindUsePacket;
import com.swacky.ohmega.network.C2S.SetExtensionVisiblePacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncDataPacket;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncKeybindUsePacket;
import com.swacky.ohmega.network.S2C.SyncSlotsPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

public final class OhmegaNetworking {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static void sendC2S(CustomPacketPayload packet) {
        IMPL.sendC2S(packet);
    }

    public static void sendS2C(ServerPlayer receiver, CustomPacketPayload packet) {
        IMPL.sendS2C(receiver, packet);
    }

    public static final class C2S {
        public static void handleKeybindUse(KeybindUsePacket packet, ServerPlayer player) {
            int index = packet.index();
            AccessoryData data = OhmegaDataAttachments.getData(player);

            if (index < data.size()) {
                ItemStack stack = data.getEntry(index).getStack();
                Accessory accessory = Accessories.get(stack.getItem());

                if (accessory != null) {
                    accessory.onKeybindUse(player, stack);
                }

                for (ServerPlayer receiver : player.level().getPlayers(player0 -> player0 != player)) {
                    OhmegaNetworking.sendS2C(receiver, new SyncKeybindUsePacket(player.getId(), index));
                }
            }
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

                if (index < OhmegaDataAttachments.getData(player).size()) {
                    OhmegaDataAttachments.getData(player).getEntry(index).setHidden(value);

                    for (ServerPlayer receiver : player.level().getPlayers(player0 -> player0 != player)) {
                        OhmegaNetworking.sendS2C(receiver, new SyncHiddenPacket(player.getId(), new int[]{index}, new boolean[]{value}));
                    }
                }
            }
        }

        public interface Service {
            void send(CustomPacketPayload packet);
        }
    }

    public static final class S2C {
        public static void handleSyncData(SyncDataPacket packet) {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;

            if (level != null && level.getEntity(packet.entityId()) instanceof LivingEntity entity) {
                AccessoryData data = packet.data();

                OhmegaDataAttachments.setData(entity, data);

                if (entity instanceof LocalPlayer player) {
                    InventoryMenu menu = player.inventoryMenu;
                    AccessoryMenuExtension extension = AccessoryMenus.assertImplementation(menu).getAccessoryExtension();

                    if (extension != null) {
                        extension.setSlots(AccessoryMenus.createSlots(menu, player, menu::addSlot));
                    }

                    ClientCallbacks.reloadRegisteredKeybinds(mc.options::load);
                }
            }
        }

        public static void handleSyncHidden(SyncHiddenPacket packet) {
            ClientLevel level = Minecraft.getInstance().level;

            if (level != null) {
                int[] indexes = packet.indexes();

                if (indexes.length == 0) {
                    return;
                }

                if (level.getEntity(packet.entityId()) instanceof LivingEntity entity) {
                    int size = indexes.length;
                    AccessoryData data = OhmegaDataAttachments.getData(entity);

                    for (int i = 0; i < size; i++) {
                        int index = indexes[i];

                        if (index < data.size()) {
                            data.getEntry(index).setHidden(packet.values()[i]);
                        }
                    }
                }
            }
        }

        public static void handleSyncKeybindUse(SyncKeybindUsePacket packet) {
            ClientLevel level = Minecraft.getInstance().level;

            if (level != null && level.getEntity(packet.entityId()) instanceof AbstractClientPlayer player) {
                ItemStack stack = OhmegaDataAttachments.getData(player).getEntry(packet.index()).getStack();
                Accessory accessory = Accessories.get(stack.getItem());

                if (accessory != null) {
                    accessory.onKeybindUse(player, stack);
                }
            }
        }

        public static void handleSyncSlots(SyncSlotsPacket packet) {
            ClientLevel level = Minecraft.getInstance().level;

            if (level != null && level.getEntity(packet.entityId()) instanceof LivingEntity entity) {
                AccessoryData data = OhmegaDataAttachments.getData(entity);
                EquipContext context = packet.context();

                switch (packet.action()) {
                    case CLEAR -> data.clearSlots(entity, null, packet.data()[0], context);
                    case CLEAR_ALL -> data.clearSlots(entity, null, -1, context);
                    case DEFAULT -> data.defaultSlots(entity, context);
                    case INHERIT -> {
                        int[] packetData = packet.data();

                        if (level.getEntity(packetData[0]) instanceof LivingEntity other) {
                            data.inheritSlots(entity, other, packetData[1], packetData[2], context);
                        }
                    }
                    case INSERT -> {
                        int[] packetData = packet.data();

                        data.insertSlots(entity, packetData[0], packet.accessoryType().orElseThrow(), packetData[1], context);
                    }
                    case REMOVE -> data.removeSlots(entity, IntArrays.reverse(packet.data()), context);
                    case SET -> {
                        int[] packetData = packet.data();

                        data.setSlots(entity, packetData[0], packet.accessoryType().orElseThrow(), packetData[1], context);
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
                    OhmegaDataAttachments.getData(entity).setStacks(entity, packet.indexes(), packet.stacks(), EquipContext.SYNC, packet.forceOnEquip());
                }
            }
        }

        public static void handleSyncTypes(SyncTypesPacket packet, RegistryAccess lookup) {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), lookup);

            buf.writeBytes(packet.data());
            AccessoryTypeManager.apply(AccessoryType.LIST_INITIALISER_STREAM_CODEC.decode(buf));
        }
    }

    public interface Service {
        void sendC2S(CustomPacketPayload packet);

        void sendS2C(ServerPlayer receiver, CustomPacketPayload packet);
    }
}
