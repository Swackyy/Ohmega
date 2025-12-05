package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.common.OhmegaCommon;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record UseAccessoryKbPacket(int slot) implements CustomPacketPayload {
    public static final Type<UseAccessoryKbPacket> TYPE = new Type<>(OhmegaCommon.rl("use_accessory_keybind_packet"));

    public static final StreamCodec<ByteBuf, UseAccessoryKbPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            UseAccessoryKbPacket::slot,
            UseAccessoryKbPacket::new
    );

    public static void handle(UseAccessoryKbPacket packet, ServerPlayNetworking.Context context) {
        if (packet.slot < AccessoryHelper.getSlotTypes().size()) {
            ServerPlayer player = context.player();
            AccessoryContainer a = AccessoryHelper.getContainer(player);
            IAccessory acc = AccessoryHelper.getBoundAccessory(a.getStackInSlot(packet.slot).getItem());

            if (acc != null) {
                ItemStack stack = a.getStackInSlot(packet.slot);

                if (!OhmegaHooks.accessoryUseEvent(player, stack)) {
                    acc.onUse(player, stack);
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
