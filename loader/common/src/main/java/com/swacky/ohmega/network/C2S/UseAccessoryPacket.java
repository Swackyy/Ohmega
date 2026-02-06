package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

public record UseAccessoryPacket(int slot) implements CustomPacketPayload {
    public static final ResourceLocation ID = OhmegaCommon.rl("use_accessory");

    public UseAccessoryPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt());
    }

    @Override
    public void write(@NonNull FriendlyByteBuf buf) {
        buf.writeVarInt(slot);
    }

    @Override
    public @NonNull ResourceLocation id() {
        return ID;
    }
}
