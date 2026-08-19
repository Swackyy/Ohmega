
package com.swacky.ohmega.api.network.S2C;

import com.swacky.ohmega.api.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SyncKeybindUsePacket(int entityId, int index) implements CustomPacketPayload {
    public static final Type<@NonNull SyncKeybindUsePacket> TYPE = new Type<>(Ohmega.id("sync_use"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncKeybindUsePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncKeybindUsePacket::entityId,
            ByteBufCodecs.VAR_INT, SyncKeybindUsePacket::index,
            SyncKeybindUsePacket::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
