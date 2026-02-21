package com.swacky.ohmega.api;

import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.extension.AttachmentHolder;
import net.minecraft.world.entity.player.Player;

public final class AccessoryHelperImpl implements AccessoryHelper.Service {
    @Override
    public AccessoryContainer getContainer(Player player) {
        return ((AttachmentHolder) player).ohmega$getContainer();
    }
}
