package com.swacky.ohmega.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public abstract class BasePacket {
    protected BasePacket() {
    }

    @SuppressWarnings("unused")
    public BasePacket(RegistryFriendlyByteBuf buf) {
    }

    public abstract void toBytes(RegistryFriendlyByteBuf buf);

    public abstract void handle(CustomPayloadEvent.Context context);
}
