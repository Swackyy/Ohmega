package com.swacky.ohmega.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public abstract class BasePacket<T extends ByteBuf> {
    protected BasePacket() {
    }

    @SuppressWarnings("unused")
    public BasePacket(T buf) {
    }

    public abstract void toBytes(T buf);

    public abstract void handle(CustomPayloadEvent.Context context);
}