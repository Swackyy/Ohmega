package com.swacky.ohmega.datagen.server;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.datagen.server.AccessoryTypeProvider;
import net.minecraft.data.PackOutput;

public final class OhmegaAccessoryTypeProvider extends AccessoryTypeProvider {
    public OhmegaAccessoryTypeProvider(PackOutput output) {
        super(output, Ohmega.MODID);
    }

    @Override
    protected void addTypes() {
        add(AccessoryType.GENERIC_ID, new AccessoryType.Builder()
                .hideHoverText()
                .emptySlotPath("accessory_slot_generic")
                .preventFallback()
                .preventReference()
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