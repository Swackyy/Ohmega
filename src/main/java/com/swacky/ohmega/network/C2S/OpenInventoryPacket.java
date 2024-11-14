package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.network.BasePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class OpenInventoryPacket extends BasePacket {
    public OpenInventoryPacket() {
    }

    @SuppressWarnings("unused")
    public OpenInventoryPacket(RegistryFriendlyByteBuf buf) {
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
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
