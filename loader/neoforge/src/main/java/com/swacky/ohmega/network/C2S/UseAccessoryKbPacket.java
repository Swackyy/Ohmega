package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.event.OhmegaHooks;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UseAccessoryKbPacket(int slot) implements CustomPacketPayload {
    public static final Type<UseAccessoryKbPacket> TYPE = new Type<>(OhmegaCommon.rl("use_accessory_keybind_packet"));

    public static final StreamCodec<ByteBuf, UseAccessoryKbPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            UseAccessoryKbPacket::slot,
            UseAccessoryKbPacket::new
    );

    public static void handle(UseAccessoryKbPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.slot < AccessoryHelper.getSlotTypes().size()) {
                Player player = context.player();
                AccessoryContainer a = player.getData(OhmegaDataAttachments.ACCESSORY_HANDLER.get());
                IAccessory acc = AccessoryHelper.getBoundAccessory(a.getStackInSlot(packet.slot).getItem());
                if (acc != null) {
                    ItemStack stack = a.getStackInSlot(packet.slot);

                    if (!OhmegaHooks.accessoryUseEvent(player, stack).isCanceled()) {
                        acc.onUse(player, stack);
                    }
                }
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
