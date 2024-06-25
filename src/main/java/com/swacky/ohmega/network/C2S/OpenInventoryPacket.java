package com.swacky.ohmega.network.C2S;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenInventoryPacket {
    public OpenInventoryPacket() {
    }

    @SuppressWarnings("unused")
    public OpenInventoryPacket(FriendlyByteBuf buf) {
    }

    @SuppressWarnings("unused")
    public void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context context = sup.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if(player != null) {
                player.containerMenu.removed(player);
                player.containerMenu = player.inventoryMenu;
            }
        });
        context.setPacketHandled(true);
    }
}
