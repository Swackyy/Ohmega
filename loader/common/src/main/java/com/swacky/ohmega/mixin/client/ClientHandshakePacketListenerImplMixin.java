package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Unused on Fabric and Forge
@SuppressWarnings("UnusedMixin")
@Mixin(ClientHandshakePacketListenerImpl.class)
abstract class ClientHandshakePacketListenerImplMixin {
    @Inject(
            method = "handleCustomQuery",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;)V"),
            cancellable = true)
    public void handleCustomQuery(ClientboundCustomQueryPacket packet, CallbackInfo ci) {
        if (packet.transactionId() == SyncAccessoryTypesPacket.ID.hashCode()) {
            ci.cancel();
            Minecraft.getInstance().submit(() -> AccessoryTypeManager.getInstance().apply(((SyncAccessoryTypesPacket) packet.payload()).types()));
        }
    }
}
