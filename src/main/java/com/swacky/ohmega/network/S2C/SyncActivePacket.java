package com.swacky.ohmega.network.S2C;

import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncActivePacket {
    private final int playerId;
    private final boolean[] active;

    public SyncActivePacket(FriendlyByteBuf buf) {
        this.playerId = buf.readInt();
        this.active = new boolean[]{buf.readBoolean(), buf.readBoolean(), buf.readBoolean()};
    }

    public SyncActivePacket(int playerId, boolean[] active) {
        this.playerId = playerId;
        this.active = active;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.playerId);
        buf.writeBoolean(active[0]);
        buf.writeBoolean(active[1]);
        buf.writeBoolean(active[2]);
    }

    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                if (level.getEntity(playerId) instanceof Player player) {
                    player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                        for (int i = 0; i < 3; i++) {
                            a.setActive(i + 3, active[i], true);
                        }
                    });
                }
            }
        });
        sup.get().setPacketHandled(true);
    }
}
