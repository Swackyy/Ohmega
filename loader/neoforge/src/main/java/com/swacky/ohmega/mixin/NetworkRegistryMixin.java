package com.swacky.ohmega.mixin;

import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkRegistry.class)
public class NetworkRegistryMixin {
    @Inject(
            method = "canSendPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ServerCommonPacketListener;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"),
            cancellable = true)
    public void canSendPacket(Packet<?> packet, ServerCommonPacketListener listener, CallbackInfoReturnable<Boolean> cir) {
        if (((ClientboundCustomPayloadPacket) packet).payload().id().equals(SyncAccessoryTypesPacket.ID)) {
            // Still return false so NeoForge does not try to handle it,
            // but suppress the log message, as we handle it ourselves
            cir.setReturnValue(false);
        }
    }
}
