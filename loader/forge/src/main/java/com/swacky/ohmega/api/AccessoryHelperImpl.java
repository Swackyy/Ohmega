package com.swacky.ohmega.api;

import com.swacky.ohmega.common.OhmegaMain;
import com.swacky.ohmega.common.dataattachment.AccessoryData;
import net.minecraft.world.entity.player.Player;

public final class AccessoryHelperImpl implements AccessoryHelper.Service {
    @Override
    public AccessoryData getData(Player player) {
        return player.getCapability(OhmegaMain.ACCESSORIES).orElseThrow(() ->
                new NullPointerException("Accessory data fetched on player '" + player.nameAndId() + "' is not present"));
    }

    public static boolean isPlayerDataPresent(Player player) {
        return player.getCapability(OhmegaMain.ACCESSORIES).isPresent();
    }
}
