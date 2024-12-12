package com.swacky.ohmega.network.S2C;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.network.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SyncAccessoryTypesPacket extends BasePacket<FriendlyByteBuf> {
    public ImmutableList<AccessoryType> types;

    public SyncAccessoryTypesPacket(ImmutableList<AccessoryType> types) {
        this.types = types;
    }

    public SyncAccessoryTypesPacket(FriendlyByteBuf buf) {
        int size = VarInt.read(buf);
        ImmutableList.Builder<AccessoryType> builder = ImmutableList.builderWithExpectedSize(size);
        for (int i = 0; i < size; i++) {
            builder.add(AccessoryType.CODEC.decode(buf));
        }
        this.types = builder.build();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        VarInt.write(buf, this.types.size());
        for (AccessoryType value : this.types) {
            AccessoryType.CODEC.encode(buf, value);
        }
    }

    @Override
    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> AccessoryTypeManager.getInstance().apply(this.types));
    }
}
