package com.swacky.ohmega.datagen.server;

import com.swacky.ohmega.api.datagen.server.AccessoryTypeProvider;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.data.PackOutput;

public class OhmegaAccessoryTypeProvider extends AccessoryTypeProvider {
    public OhmegaAccessoryTypeProvider(PackOutput output) {
        super(output, OhmegaCommon.MODID);
    }

    @Override
    public void addTypes() {
        add(AccessoryType.GENERIC_ID, new AccessoryType.Builder()
                .emptySlotPath("accessory_slot_generic")
                .hideHoverText()
                .priority(Integer.MAX_VALUE));

        add(AccessoryType.NORMAL_ID, new AccessoryType.Builder()
                .emptySlotPath("accessory_slot_normal")
                .priority(Integer.MAX_VALUE));

        add(AccessoryType.UTILITY_ID, new AccessoryType.Builder()
                .emptySlotPath("accessory_slot_utility")
                .priority(Integer.MAX_VALUE - 1));

        add(AccessoryType.SPECIAL_ID, new AccessoryType.Builder()
                .emptySlotPath("accessory_slot_special")
                .priority(Integer.MAX_VALUE - 2));
    }
}