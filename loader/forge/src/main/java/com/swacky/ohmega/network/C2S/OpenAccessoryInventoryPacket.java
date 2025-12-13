package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.network.BasePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class OpenAccessoryInventoryPacket extends BasePacket {
    public static final OpenAccessoryInventoryPacket INSTANCE = new OpenAccessoryInventoryPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenAccessoryInventoryPacket> CODEC = StreamCodec.unit(INSTANCE);

    private OpenAccessoryInventoryPacket() {}

    public static void handle(OpenAccessoryInventoryPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.containerMenu.getCarried();
                if (!stack.isEmpty()) {
                    AbstractContainerMenu.dropOrPlaceInInventory(player, stack);
                    player.containerMenu.setCarried(ItemStack.EMPTY);
                }

                player.openMenu(new SimpleMenuProvider((id, inv, player0) -> new AccessoryInventoryMenu(id, inv), Component.empty()));
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    protected String getId() {
        return "open_accessory_inventory";
    }
}