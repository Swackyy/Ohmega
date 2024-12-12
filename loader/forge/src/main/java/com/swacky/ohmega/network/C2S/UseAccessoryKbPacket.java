package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.BasePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Objects;

public class UseAccessoryKbPacket extends BasePacket<ByteBuf> {
    private final int slot;

    public UseAccessoryKbPacket(int slot) {
        this.slot = slot;
    }

    public UseAccessoryKbPacket(ByteBuf buf) {
        this.slot = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.slot);
    }

    @Override
    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (this.slot < AccessoryHelper.getSlotTypes().size()) {
                AccessoryHelper.getContainer(Objects.requireNonNull(context.getSender())).ifPresent(a -> {
                    IAccessory acc = AccessoryHelper.getBoundAccessory(a.getStackInSlot(this.slot).getItem());
                    if (acc != null) {
                        Player player = context.getSender();
                        ItemStack stack = a.getStackInSlot(slot);

                        if (!OhmegaHooks.accessoryUseEvent(player, stack).isCanceled()) {
                            acc.onUse(player, stack);
                        }
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }
}