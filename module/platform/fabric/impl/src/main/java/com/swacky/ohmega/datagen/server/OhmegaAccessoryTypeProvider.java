package com.swacky.ohmega.datagen.server;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.datagen.server.AccessoryTypeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

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
                .typePriority(Integer.MAX_VALUE));
        add(AccessoryType.NORMAL_ID, new AccessoryType.Builder()
                .defaultSlots(3)
                .emptySlotPath("accessory_slot_normal")
                .slotPriority(1000)
                .typePriority(3000));
        add(AccessoryType.UTILITY_ID, new AccessoryType.Builder()
                .defaultSlots(2)
                .emptySlotPath("accessory_slot_utility")
                .slotPriority(2000)
                .typePriority(2000));
        add(AccessoryType.SPECIAL_ID, new AccessoryType.Builder()
                .defaultSlots(1)
                .emptySlotPath("accessory_slot_special")
                .slotPriority(3000)
                .typePriority(1000));
    }
}