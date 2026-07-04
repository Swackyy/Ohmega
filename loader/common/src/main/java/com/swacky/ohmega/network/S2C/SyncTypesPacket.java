package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.Ohmega;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SyncTypesPacket(byte[] data) implements CustomPacketPayload {
    public static final Type<@NonNull SyncTypesPacket> TYPE = new Type<>(Ohmega.id("sync_types"));
    public static final StreamCodec<FriendlyByteBuf, SyncTypesPacket> CODEC = CustomPacketPayload.codec(
            (packet, buf) -> buf.writeByteArray(packet.data),
            buf -> new SyncTypesPacket(buf.readByteArray())
    );

    public SyncTypesPacket(RegistryAccess lookup) {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), lookup);

        AccessoryType.LIST_INITIALISER_STREAM_CODEC.encode(buf, AccessoryTypeManager.getTypes());

        byte[] data = new byte[buf.readableBytes()];

        buf.readBytes(data);
        this(data);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
