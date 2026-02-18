package com.swacky.ohmega.mixin;

import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public class ServerLoginPacketListenerImplMixin {
    @Shadow(remap = false)
    @Final
    Connection connection;

    @Inject(
            method = "handleHello",
            at = @At(
                    value = "HEAD"),
            remap = false)
    public void startClientVerification(ServerboundHelloPacket packet, CallbackInfo ci) {
        OhmegaNetworkingImpl.S2C.send(connection, new SyncAccessoryTypesPacket());
    }
}
