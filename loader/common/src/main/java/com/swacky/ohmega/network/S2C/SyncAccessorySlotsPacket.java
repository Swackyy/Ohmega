package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public record SyncAccessorySlotsPacket(int playerId, int[] indexes, List<ItemStack> stacks, boolean forceOnEquip) implements CustomPacketPayload {
    public static final ResourceLocation ID = OhmegaCommon.rl("sync_accessory_slots");

    public SyncAccessorySlotsPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readVarIntArray(), readStacks(buf), buf.readBoolean());
    }

    private static List<ItemStack> readStacks(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<ItemStack> list = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(buf.readItem());
        }

        return list;
    }

    @Override
    public void write(@NonNull FriendlyByteBuf buf) {
        buf.writeInt(playerId);
        buf.writeVarIntArray(indexes);
        buf.writeVarInt(stacks.size());

        for (ItemStack stack : stacks) {
            buf.writeItem(stack);
        }

        buf.writeBoolean(forceOnEquip);
    }

    @Override
    public @NonNull ResourceLocation id() {
        return ID;
    }
}
