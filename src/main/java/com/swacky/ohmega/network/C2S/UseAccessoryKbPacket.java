package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.events.AccessoryUseEvent;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class UseAccessoryKbPacket {
    private final int slot;

    public UseAccessoryKbPacket(FriendlyByteBuf buf) {
        this.slot = buf.readInt();
    }

    public UseAccessoryKbPacket(int slot) {
        this.slot = slot + 3;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.slot);
    }

    public void handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context context = sup.get();
        context.enqueueWork(() -> {
            if(this.slot < 6) {
                Objects.requireNonNull(context.getSender()).getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                    if(a.getStackInSlot(this.slot).getItem() instanceof IAccessory acc) {
                        Player player = context.getSender();
                        ItemStack stack = a.getStackInSlot(slot);

                        AccessoryUseEvent event = OhmegaHooks.accessoryUseEvent(player, stack);
                        if(!event.isCanceled()) {
                            acc.onUse(player, stack);
                        }
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }
}
