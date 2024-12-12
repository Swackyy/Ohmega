package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.network.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.List;

public class SyncAccessorySlotsPacket extends BasePacket<RegistryFriendlyByteBuf> {
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

    public SyncAccessorySlotsPacket(RegistryFriendlyByteBuf buf) {
        this.playerId = buf.readInt();

        int size = VarInt.read(buf);
        int[] values = new int[size];

        for (int i = 0; i < size; i++) {
            values[i] = VarInt.read(buf);
        }
        this.slots = values;
        this.stacks = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buf);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.playerId);

        VarInt.write(buf, this.slots.length);

        for (int value : this.slots) {
            VarInt.write(buf, value);
        }

        ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buf, this.stacks);
    }

    @Override
    public void handle(CustomPayloadEvent.Context context) {
        if (this.slots.length == 0) {
            return;
        }

        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                if (level.getEntity(this.playerId) instanceof Player player) {
                    for (int[] i = {0}; i[0] < this.slots.length; i[0]++) {
                        AccessoryHelper.getContainer(player).ifPresent(a -> a.setStackInSlot(this.slots[i[0]], this.stacks.get(i[0])));
                    }
                }
            }
        });
    }
}
