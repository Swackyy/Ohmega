package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SyncAccessoriesPacket {
    private final int playerId;
    private final byte slot;
    private final ItemStack accessory;

    public SyncAccessoriesPacket(RegistryFriendlyByteBuf buf) {
        this.playerId = buf.readInt();
        this.slot = buf.readByte();
        this.accessory = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
    }

    public SyncAccessoriesPacket(int playerId, byte slot, ItemStack accessory) {
        this.playerId = playerId;
        this.slot = slot;
        this.accessory = accessory;
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.playerId);
        buf.writeByte(this.slot);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, this.accessory);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if(level == null) return;
            if(level.getEntity(playerId) instanceof Player player) {
                player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> a.setStackInSlot(this.slot, accessory));
            }
        });
        context.setPacketHandled(true);
    }
}
