package com.swacky.ohmega.network;

import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public abstract class BasePacket implements CustomPacketPayload {
    protected abstract String getId();

    @Override
    public final @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(OhmegaCommon.id(getId()));
    }
}