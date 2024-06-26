package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.NotNull;

public class OpenAccessoryGuiPacket {
    private int playerId;
    public OpenAccessoryGuiPacket(int playerId) {
        this.playerId = playerId;
    }

    public OpenAccessoryGuiPacket(FriendlyByteBuf buf) {
        buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.playerId);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if(player != null) {
                player.containerMenu.removed(player);
                if(!player.isCreative()) {
                    player.openMenu(new MenuProvider() {
                        @Override
                        public @NotNull Component getDisplayName() {
                            return MutableComponent.create(PlainTextContents.EMPTY);
                        }

                        @Override
                        public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
                            return new AccessoryInventoryMenu(id, inv);
                        }
                    });
                }
            }
        });
        context.setPacketHandled(true);
    }
}
