package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

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

    public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context context = sup.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if(player != null) {
                player.containerMenu.removed(player);
                if(!player.isCreative())
                    NetworkHooks.openGui(player, new MenuProvider() {
                        @Override
                        public @NotNull Component getDisplayName() {
                            return new TextComponent("");
                        }

                        @Override
                        public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
                            return new AccessoryInventoryMenu(id, inv);
                        }
                    });
            }
        });
        return true;
    }
}
