package com.swacky.ohmega.network.S2C;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class SyncAccessoryTypesPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull SyncAccessoryTypesPacket> TYPE = new CustomPacketPayload.Type<>(OhmegaCommon.id("sync_accessory_types_packet"));

    public static final StreamCodec<ByteBuf, SyncAccessoryTypesPacket> CODEC = StreamCodec.composite(
            new StreamCodec<>() {
                @Override
                public @NotNull ImmutableList<AccessoryType> decode(@NotNull ByteBuf buf) {
                    int size = VarInt.read(buf);
                    ImmutableList.Builder<AccessoryType> builder = ImmutableList.builderWithExpectedSize(size);
                    for (int i = 0; i < size; i++) {
                        builder.add(AccessoryType.CODEC.decode(buf));
                    }
                    return builder.build();
                }

                @Override
                public void encode(@NotNull ByteBuf buf, @NotNull ImmutableList<AccessoryType> values) {
                    VarInt.write(buf, values.size());
                    for (AccessoryType value : values) {
                        AccessoryType.CODEC.encode(buf, value);
                    }
                }
            },
            inst -> inst.types,
            SyncAccessoryTypesPacket::new
    );

    public ImmutableList<AccessoryType> types;

    public SyncAccessoryTypesPacket(ImmutableList<AccessoryType> types) {
        this.types = types;
    }

    public static void handle(SyncAccessoryTypesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> AccessoryTypeManager.getInstance().apply(packet.types));
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
