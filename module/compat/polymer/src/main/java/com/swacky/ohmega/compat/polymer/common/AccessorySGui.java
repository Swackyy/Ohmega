package com.swacky.ohmega.compat.polymer.common;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.MenuType;

public class AccessorySGui extends SimpleGui {
    private final AccessoryData data;

    public AccessorySGui(ServerPlayer player) {
        super(MenuType.GENERIC_9x6, player, false);

        this.data = OhmegaDataAttachments.getData(player);

        open();
    }

    private void playClickSound() {
        player.connection.send(new ClientboundSoundEntityPacket(
                SoundEvents.UI_BUTTON_CLICK,
                SoundSource.UI, player,
                0.5f,
                1,
                player.getRandom().nextLong()));
    }

    public static void open(ServerPlayer player) {
        new AccessorySGui(player).playClickSound();
    }
}
