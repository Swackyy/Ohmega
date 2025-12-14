package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.OhmegaCommon;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SyncAccessorySlotsPacket implements CustomPacketPayload {
    public static final Type<@NotNull SyncAccessorySlotsPacket> TYPE = new Type<>(OhmegaCommon.id("sync_accessory_slots_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAccessorySlotsPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            inst -> inst.playerId,
            new StreamCodec<ByteBuf, int[]>() {
                public int @NotNull [] decode(@NotNull ByteBuf buf) {
                    int size = VarInt.read(buf);
                    int[] values = new int[size];

                    for (int i = 0; i < size; i++) {
                        values[i] = VarInt.read(buf);
                    }
                    return values;
                }

                public void encode(@NotNull ByteBuf buf, int @NotNull [] values) {
                    VarInt.write(buf, values.length);

                    for (int value : values) {
                        VarInt.write(buf, value);
                    }
                }
            },
            inst -> inst.slots,
            ItemStack.OPTIONAL_LIST_STREAM_CODEC,
            inst -> inst.stacks,
            SyncAccessorySlotsPacket::new
    );

    private final int playerId;
    private final int[] slots;
    private final List<ItemStack> stacks;

    public SyncAccessorySlotsPacket(int playerId, int[] slots, List<ItemStack> stacks) {
        if (slots.length != stacks.size()) {
            throw new IllegalArgumentException("Mismatched int slot array size of " + slots.length + " and ItemStack size of " + stacks.size());
        }
        this.playerId = playerId;
        this.slots = slots;
        this.stacks = stacks;
    }

    public static void handle(SyncAccessorySlotsPacket packet, ClientPlayNetworking.Context context) {
        if (packet.slots.length == 0) {
            return;
        }

        ClientLevel level = context.client().level;
        if (level != null) {
            if (level.getEntity(packet.playerId) instanceof Player player) {
                for (int i = 0; i < packet.slots.length; i++) {
                    AccessoryHelper.getContainer(player).setStackInSlot(packet.slots[i], packet.stacks.get(i));
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
