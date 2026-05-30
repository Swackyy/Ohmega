package com.swacky.ohmega.mixin;

import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationPacketListenerImpl.class)
abstract class ServerConfigurationPacketListenerImplMixin extends ServerCommonPacketListenerImpl implements ServerConfigurationPacketListener, TickablePacketListener {
    public ServerConfigurationPacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
        super(server, connection, cookie);
    }

    @Inject(
            method = "addOptionalTasks",
            at = @At(
                    value = "TAIL"))
    private void handleConfigurationFinished(CallbackInfo ci) {
        connection.send(new ClientboundCustomPayloadPacket(new SyncTypesPacket(server.registryAccess())));
    }
}
