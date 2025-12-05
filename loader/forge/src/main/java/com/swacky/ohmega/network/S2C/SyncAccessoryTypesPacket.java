package com.swacky.ohmega.network.S2C;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.network.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.NotNull;

public class SyncAccessoryTypesPacket extends BasePacket {
    public static final StreamCodec<FriendlyByteBuf, SyncAccessoryTypesPacket> CODEC = StreamCodec.composite(
            new StreamCodec<>() {
                @Override
                public @NotNull ImmutableList<AccessoryType> decode(@NotNull FriendlyByteBuf buf) {
                    int size = VarInt.read(buf);
                    ImmutableList.Builder<AccessoryType> builder = ImmutableList.builderWithExpectedSize(size);
                    for (int i = 0; i < size; i++) {
                        builder.add(AccessoryType.CODEC.decode(buf));
                    }
                    return builder.build();
                }

                @Override
                public void encode(@NotNull FriendlyByteBuf buf, @NotNull ImmutableList<AccessoryType> values) {
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

    public static void handle(SyncAccessoryTypesPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> AccessoryTypeManager.getInstance().apply(packet.types));
    }

    @Override
    protected String getId() {
        return "sync_accessory_types";
    }
}
