package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.common.OhmegaCommon;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OpenAccessoryInventoryPacket implements CustomPacketPayload {
    public static final Type<@NotNull OpenAccessoryInventoryPacket> TYPE = new Type<>(OhmegaCommon.id("open_accessory_inventory_packet"));

    public static final StreamCodec<ByteBuf, OpenAccessoryInventoryPacket> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull OpenAccessoryInventoryPacket decode(@NotNull ByteBuf buf) {
            return new OpenAccessoryInventoryPacket();
        }

        @Override
        public void encode(@NotNull ByteBuf buf, @NotNull OpenAccessoryInventoryPacket packet) {
        }
    };

    public static void handle(OpenAccessoryInventoryPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        ItemStack stack = player.containerMenu.getCarried();
        if (!stack.isEmpty()) {
            AbstractContainerMenu.dropOrPlaceInInventory(player, stack);
            player.containerMenu.setCarried(ItemStack.EMPTY);
        }

        context.player().openMenu(new SimpleMenuProvider((id, inv, player0) -> new AccessoryInventoryMenu(id, inv), Component.empty()));
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
