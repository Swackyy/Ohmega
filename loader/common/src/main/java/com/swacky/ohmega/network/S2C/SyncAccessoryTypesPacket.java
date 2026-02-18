package com.swacky.ohmega.network.S2C;

import com.google.common.collect.ImmutableSet;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

import java.util.function.IntSupplier;

public record SyncAccessoryTypesPacket(ImmutableSet<AccessoryType> types) implements CustomQueryPayload, IntSupplier {
    public static final ResourceLocation ID = OhmegaCommon.rl("sync_accessory_types");

    public SyncAccessoryTypesPacket() {
        this(AccessoryTypeManager.getInstance().getTypes());
    }

    public SyncAccessoryTypesPacket(FriendlyByteBuf buf) {
        this(readTypes(buf));
    }

    private static ImmutableSet<AccessoryType> readTypes(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        ImmutableSet.Builder<AccessoryType> builder = ImmutableSet.builderWithExpectedSize(size);

        for (int i = 0; i < size; i++) {
            builder.add(AccessoryType.read(buf));
        }

        return builder.build();
    }

    @Override
    public void write(@NonNull FriendlyByteBuf buf) {
        buf.writeVarInt(types.size());

        for (AccessoryType type : types) {
            type.write(buf);
        }
    }

    @Override
    public @NonNull ResourceLocation id() {
        return ID;
    }

    @Override
    public int getAsInt() {
        return 0;
    }
}
