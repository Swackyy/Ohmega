package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.BasePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Objects;

public class UseAccessoryKbPacket extends BasePacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, UseAccessoryKbPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            inst -> inst.slot,
            UseAccessoryKbPacket::new
    );

    private final int slot;

    public UseAccessoryKbPacket(int slot) {
        this.slot = slot;
    }

    public static void handle(UseAccessoryKbPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (packet.slot < AccessoryHelper.getSlotTypes().size()) {
                AccessoryHelper.getContainer(Objects.requireNonNull(context.getSender())).ifPresent(a -> {
                    IAccessory acc = AccessoryHelper.getBoundAccessory(a.getStackInSlot(packet.slot).getItem());
                    if (acc != null) {
                        Player player = context.getSender();
                        ItemStack stack = a.getStackInSlot(packet.slot);

                        if (!OhmegaHooks.accessoryUseEvent(player, stack)) {
                            acc.onUse(player, stack);
                        }
                    }
                });
            }
        });

        context.setPacketHandled(true);
    }

    @Override
    protected String getId() {
        return "use_accessory";
    }
}