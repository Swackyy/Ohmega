package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAccessoriesPacket {
    public int playerId;
    public byte slot;
    ItemStack accessory;

    public SyncAccessoriesPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readInt();
        this.slot = buf.readByte();
        this.accessory = buf.readItem();
    }

    public SyncAccessoriesPacket(int playerId, byte slot, ItemStack accessory) {
        this.playerId = playerId;
        this.slot = slot;
        this.accessory = accessory;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.playerId);
        buf.writeByte(this.slot);
        buf.writeItem(this.accessory);
    }

    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if(level == null) return;
            if(level.getEntity(playerId) instanceof Player player) {
                player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> a.setStackInSlot(this.slot, accessory));
            }
        });
        sup.get().setPacketHandled(true);
    }
}
