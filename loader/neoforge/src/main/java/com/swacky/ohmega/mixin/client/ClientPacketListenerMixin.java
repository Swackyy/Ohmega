package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.event.ClientCallbacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl implements ClientGamePacketListener, TickablePacketListener {
    protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie cookie) {
        super(minecraft, connection, cookie);
    }

    @Inject(
            method = "clearLevel",
            at = @At(value = "HEAD"))
    private void clearLevel(CallbackInfo ci) {
        ClientCallbacks.onDisconnect(Minecraft.getInstance().options::load);
    }
}
