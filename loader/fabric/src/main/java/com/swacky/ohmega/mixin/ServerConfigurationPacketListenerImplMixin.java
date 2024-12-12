package com.swacky.ohmega.mixin;

import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public class ServerConfigurationPacketListenerImplMixin {
    @Inject(method = "startConfiguration", at = @At(value = "HEAD"))
    public void startConfiguration(CallbackInfo ci) {
        ((ServerConfigurationPacketListenerImpl) (Object) this).send(new ClientboundCustomPayloadPacket(new SyncAccessoryTypesPacket(AccessoryTypeManager.getInstance().getTypes())));
    }
}
