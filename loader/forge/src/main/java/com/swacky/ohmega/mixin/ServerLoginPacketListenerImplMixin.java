package com.swacky.ohmega.mixin;

import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public class ServerLoginPacketListenerImplMixin {
    @Inject(
            method = "handleHello",
            at = @At(
                    value = "HEAD"))
    public void startClientVerification(ServerboundHelloPacket packet, CallbackInfo ci) {
        OhmegaNetworkingImpl.S2C.send(((ServerLoginPacketListenerImpl) (Object) this).connection, new SyncAccessoryTypesPacket());
    }
}
