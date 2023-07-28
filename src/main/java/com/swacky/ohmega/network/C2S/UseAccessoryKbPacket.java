package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.network.FriendlyByteBuf;
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
        sup.get().enqueueWork(() -> {
            if(this.slot < 6) {
                Objects.requireNonNull(sup.get().getSender()).getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                    if(a.getStackInSlot(this.slot).getItem() instanceof IAccessory acc) {
                        acc.onUse(sup.get().getSender(), a.getStackInSlot(slot));
                    }
                });
            }
        });
        sup.get().setPacketHandled(true);
    }
}
