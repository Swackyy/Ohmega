package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.network.OhmegaPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

public final class ResizeContainerPacket implements OhmegaPacket<ServerPacketListener> {
    public static final ResourceLocation ID = OhmegaCommon.rl("resize_container");
    public static final ResizeContainerPacket INSTANCE = new ResizeContainerPacket();

    private ResizeContainerPacket() {}

    public ResizeContainerPacket(FriendlyByteBuf buf) {}

    @Override
    public void write(@NonNull FriendlyByteBuf buf) {}

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
