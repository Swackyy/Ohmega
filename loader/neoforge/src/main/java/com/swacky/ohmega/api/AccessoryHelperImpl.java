package com.swacky.ohmega.api;

import com.swacky.ohmega.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import net.minecraft.world.entity.player.Player;

public final class AccessoryHelperImpl implements AccessoryHelper.Service {
    @Override
    public AccessoryData getData(Player player) {
        return player.getData(OhmegaDataAttachments.ACCESSORY_HANDLER);
    }
}
