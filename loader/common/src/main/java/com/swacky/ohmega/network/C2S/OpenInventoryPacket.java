package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

public final class OpenInventoryPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = OhmegaCommon.rl("open_inventory");
    public static final OpenInventoryPacket INSTANCE = new OpenInventoryPacket();

    private OpenInventoryPacket() {}

    public OpenInventoryPacket(FriendlyByteBuf buf) {}

    @Override
    public void write(@NonNull FriendlyByteBuf buf) {}

    @Override
    public @NonNull ResourceLocation id() {
        return ID;
    }
}
