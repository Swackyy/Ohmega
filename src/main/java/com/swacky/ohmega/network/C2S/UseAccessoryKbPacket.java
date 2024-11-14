package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.AccessoryUseEvent;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.BasePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Objects;

public class UseAccessoryKbPacket extends BasePacket {
    private final int slot;

    public UseAccessoryKbPacket(int slot) {
        this.slot = slot;
    }

    public UseAccessoryKbPacket(RegistryFriendlyByteBuf buf) {
        this.slot = buf.readInt();
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.slot);
    }

    @Override
    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (this.slot < AccessoryHelper.getSlotTypes().size()) {
                Objects.requireNonNull(context.getSender()).getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                    IAccessory acc = AccessoryHelper.getBoundAccessory(a.getStackInSlot(this.slot).getItem());
                    if (acc != null) {
                        Player player = context.getSender();
                        ItemStack stack = a.getStackInSlot(slot);

                        AccessoryUseEvent event = OhmegaHooks.accessoryUseEvent(player, stack);
                        if (!event.isCanceled()) {
                            acc.onUse(player, stack);
                        }
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }
}
