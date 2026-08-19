package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.api.network.OhmegaNetworking;
import com.swacky.ohmega.api.network.S2C.SyncDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin {
    @Inject(method = "handleCustomPayload(Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;)V", at = @At(value = "HEAD"), cancellable = true)
    public void handleCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (packet.payload() instanceof SyncDataPacket packet0) {
            System.out.println("caught ya");
            Minecraft.getInstance().execute(() -> OhmegaNetworking.S2C.handleSyncData(packet0));
            ci.cancel();
        }
    }
}
