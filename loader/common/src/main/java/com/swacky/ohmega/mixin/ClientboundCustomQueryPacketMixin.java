package com.swacky.ohmega.mixin;

import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Unused on Fabric and Forge
@SuppressWarnings("UnusedMixin")
@Mixin(ClientboundCustomQueryPacket.class)
abstract class ClientboundCustomQueryPacketMixin {
    @Inject(
            method = "readPayload",
            at = @At(
                    value = "HEAD"),
            cancellable = true)
    private static void readPayload(ResourceLocation id, FriendlyByteBuf buf, CallbackInfoReturnable<CustomQueryPayload> cir) {
        if (id.equals(SyncAccessoryTypesPacket.ID)) {
            cir.setReturnValue(new SyncAccessoryTypesPacket(buf));
        }
    }
}
