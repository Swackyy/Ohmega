package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.network.BasePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class OpenInventoryPacket extends BasePacket {
    public static final OpenInventoryPacket INSTANCE = new OpenInventoryPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenInventoryPacket> CODEC = StreamCodec.unit(INSTANCE);

    private OpenInventoryPacket() {}

    public static void handle(OpenInventoryPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.doCloseContainer();
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    protected String getId() {
        return "open_inventory";
    }
}