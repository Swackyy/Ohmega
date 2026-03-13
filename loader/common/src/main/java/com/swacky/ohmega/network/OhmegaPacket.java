package com.swacky.ohmega.network;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

public interface OhmegaPacket<T extends PacketListener> extends Packet<T> {
    ResourceLocation id();

    @Override
    default void handle(@NonNull T listener) {}
}
