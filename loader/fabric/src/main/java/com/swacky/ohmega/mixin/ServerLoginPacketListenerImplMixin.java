package com.swacky.ohmega.mixin;

import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
abstract class ServerLoginPacketListenerImplMixin {
    @Shadow
    @Final
    Connection connection;

    @Inject(
            method = "handleHello",
            at = @At(
                    value = "HEAD"))
    public void startClientVerification(ServerboundHelloPacket packet, CallbackInfo ci) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeVarInt(SyncAccessoryTypesPacket.ID.hashCode());
        buf.writeResourceLocation(SyncAccessoryTypesPacket.ID);
        new SyncAccessoryTypesPacket().write(buf);
        connection.send(new ClientboundCustomQueryPacket(buf));
    }
}
