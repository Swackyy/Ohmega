package com.swacky.ohmega.common.datacomponent;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Supplier;

public final class AccessoryItemDataComponent {
    private static final String SLOT_KEY = "Slot";
    private static final String ACTIVE_KEY = "Active";

    private final Supplier<CompoundTag> supplier;

    public AccessoryItemDataComponent(Supplier<CompoundTag> supplier) {
        this.supplier = supplier;
    }

    public void setSlot(int slot) {
        supplier.get().putInt(SLOT_KEY, slot);
    }

    public void setActive(boolean value) {
        supplier.get().putBoolean(ACTIVE_KEY, value);
    }

    public int getSlot() {
        return supplier.get().getInt(SLOT_KEY);
    }

    public boolean isActive() {
        return supplier.get().getBoolean(ACTIVE_KEY);
    }
}
