package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.api.AccessoryHelper;

import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.network.BasePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ResizeCapPacket extends BasePacket {
    public static final ResizeCapPacket INSTANCE = new ResizeCapPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, ResizeCapPacket> CODEC = StreamCodec.unit(INSTANCE);

    private ResizeCapPacket() {}

    public static void handle(ResizeCapPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                AccessoryHelper.getContainer(player).ifPresent(AccessoryContainer::reloadCfg);
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    protected String getId() {
        return "resize_cap";
    }
}