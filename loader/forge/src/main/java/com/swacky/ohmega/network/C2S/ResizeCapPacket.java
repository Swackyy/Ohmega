package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.network.BasePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ResizeCapPacket extends BasePacket<ByteBuf> {
    public ResizeCapPacket() {
    }

    @SuppressWarnings("unused")
    public ResizeCapPacket(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                AccessoryHelper.getContainer(player).ifPresent(AccessoryContainer::reloadCfg);
            }
        });
        context.setPacketHandled(true);
    }
}