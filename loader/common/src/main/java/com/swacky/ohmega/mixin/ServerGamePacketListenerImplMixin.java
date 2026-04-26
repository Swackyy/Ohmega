package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.common.menu.AccessorySlot;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl implements TickablePacketListener, GameProtocols.Context, ServerGamePacketListener, ServerPlayerConnection {
    @Shadow
    public ServerPlayer player;

    public ServerGamePacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
        super(server, connection, cookie);
    }

    @Definition(id = "packet", local = @Local(type = ServerboundSetCreativeModeSlotPacket.class, argsOnly = true))
    @Definition(id = "slotNum", method = "Lnet/minecraft/network/protocol/game/ServerboundSetCreativeModeSlotPacket;slotNum()S")
    @Expression("packet.slotNum() <= 45")
    @WrapOperation(
            method = "handleSetCreativeModeSlot",
            at = @At(
                    value = "MIXINEXTRAS:EXPRESSION",
                    target = "Lnet/minecraft/network/protocol/game/ServerboundSetCreativeModeSlotPacket;slotNum()S"))
    public boolean handleSetCreativeModeSlot(int slotNum, int originalBounds, Operation<Boolean> handle) {
        if (handle.call(slotNum, originalBounds)) {
            return true;
        }

        return player.inventoryMenu.slots.get(slotNum) instanceof AccessorySlot;
    }
}
