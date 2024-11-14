package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.network.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SyncAccessorySlotPacket extends BasePacket {
    private final int playerId;
    private final int slot;
    private final ItemStack stack;

    public SyncAccessorySlotPacket(int playerId, int slot, ItemStack stack) {
        this.playerId = playerId;
        this.slot = slot;
        this.stack = stack;
    }

    public SyncAccessorySlotPacket(RegistryFriendlyByteBuf buf) {
        this.playerId = buf.readInt();
        this.slot = buf.readInt();
        this.stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.playerId);
        buf.writeInt(this.slot);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, this.stack);
    }

    @Override
    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                if (level.getEntity(this.playerId) instanceof Player player) {
                    player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                        a.setStackInSlot(this.slot, this.stack);
                    });
                }
            }
        });
        context.setPacketHandled(true);
    }
}
