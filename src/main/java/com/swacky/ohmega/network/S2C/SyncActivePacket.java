package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncActivePacket {
    private final int playerId;
    private boolean[] actives = new boolean[3];

    public SyncActivePacket(FriendlyByteBuf buf) {
        this.playerId = buf.readInt();
        this.actives[0] = buf.readBoolean();
        this.actives[1] = buf.readBoolean();
        this.actives[2] = buf.readBoolean();
    }

    public SyncActivePacket(int playerId, boolean[] actives) {
        this.playerId = playerId;
        this.actives = actives;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.playerId);
        buf.writeBoolean(this.actives[0]);
        buf.writeBoolean(this.actives[1]);
        buf.writeBoolean(this.actives[2]);
    }

    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if(level == null) {
                return;
            }

            if(level.getEntity(playerId) instanceof Player player) {
                player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                    for(int i = 0; i < 3; i++) {
                        a.setActive(i  + 3, this.actives[i], true);
                    }
                });
            }
        });
        sup.get().setPacketHandled(true);
    }
}
