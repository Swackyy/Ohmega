package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

public final class OpenAccessoryInventoryPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = OhmegaCommon.rl("open_accessory_inventory");
    public static final OpenAccessoryInventoryPacket INSTANCE = new OpenAccessoryInventoryPacket();

    private OpenAccessoryInventoryPacket() {}

    public OpenAccessoryInventoryPacket(FriendlyByteBuf buf) {}

    @Override
    public void write(@NonNull FriendlyByteBuf buf) {}

    @Override
    public @NonNull ResourceLocation id() {
        return ID;
    }
}
