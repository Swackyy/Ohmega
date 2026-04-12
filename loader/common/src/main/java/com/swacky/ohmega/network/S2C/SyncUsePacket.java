
package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.common.Ohmega;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SyncUsePacket(int entityId, int index) implements CustomPacketPayload {
    public static final Type<@NonNull SyncUsePacket> TYPE = new Type<>(Ohmega.id("sync_use"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncUsePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, inst -> inst.entityId,
            ByteBufCodecs.VAR_INT, inst -> inst.index,
            SyncUsePacket::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
