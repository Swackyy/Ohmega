package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.network.BasePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class OpenInventoryPacket extends BasePacket<ByteBuf> {
    public OpenInventoryPacket() {
    }

    @SuppressWarnings("unused")
    public OpenInventoryPacket(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.containerMenu.removed(player);
                player.containerMenu = player.inventoryMenu;
            }
        });
        context.setPacketHandled(true);
    }
}