package com.swacky.ohmega.api;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import net.minecraft.world.entity.player.Player;

public final class AccessoryHelperImpl implements AccessoryHelper.Service {
    @Override
    public AccessoryContainer getContainer(Player player) {
        return player.getCapability(Ohmega.ACCESSORIES).orElseThrow(() ->
                new NullPointerException("Accessory data fetched on player '" + player.getScoreboardName() + '#' + player.getId() + "' is not present"));
    }

    public static boolean isPlayerDataPresent(Player player) {
        return player.getCapability(Ohmega.ACCESSORIES).isPresent();
    }
}
