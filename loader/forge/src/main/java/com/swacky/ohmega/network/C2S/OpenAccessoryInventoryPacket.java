package com.swacky.ohmega.network.C2S;

import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.network.BasePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class OpenAccessoryInventoryPacket extends BasePacket {
    public static final OpenAccessoryInventoryPacket INSTANCE = new OpenAccessoryInventoryPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenAccessoryInventoryPacket> CODEC = StreamCodec.unit(INSTANCE);

    private OpenAccessoryInventoryPacket() {}

    public static void handle(OpenAccessoryInventoryPacket packet, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.containerMenu.removed(player);
                player.nextContainerCounter();
                player.containerMenu = new AccessoryInventoryMenu(player.containerCounter, player.getInventory());
                player.connection.send(new ClientboundOpenScreenPacket(player.containerCounter, OhmegaMenus.ACCESSORY_INVENTORY.get(), MutableComponent.create(PlainTextContents.LiteralContents.EMPTY)));
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    protected String getId() {
        return "open_accessory_inventory";
    }
}