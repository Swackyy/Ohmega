package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class OpenAccessoryInventoryPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenAccessoryInventoryPacket> TYPE = new CustomPacketPayload.Type<>(OhmegaCommon.rl("open_accessory_inventory_packet"));

    public static final StreamCodec<ByteBuf, OpenAccessoryInventoryPacket> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull OpenAccessoryInventoryPacket decode(@NotNull ByteBuf buf) {
            return new OpenAccessoryInventoryPacket();
        }

        @Override
        public void encode(@NotNull ByteBuf buf, @NotNull OpenAccessoryInventoryPacket packet) {
        }
    };

    public static void handle(OpenAccessoryInventoryPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                player.nextContainerCounter();
                player.containerMenu = new AccessoryInventoryMenu(player.containerCounter, player.getInventory());
                player.connection.send(new ClientboundOpenScreenPacket(player.containerCounter, OhmegaMenus.ACCESSORY_INVENTORY.get(), MutableComponent.create(PlainTextContents.LiteralContents.EMPTY)));
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
