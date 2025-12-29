package com.swacky.ohmega.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class AccessoryItemDataComponent {
    public static final Codec<AccessoryItemDataComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("slot").forGetter(AccessoryItemDataComponent::getSlot),
            Codec.BOOL.fieldOf("active").forGetter(AccessoryItemDataComponent::isActive)
    ).apply(inst, AccessoryItemDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AccessoryItemDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AccessoryItemDataComponent::getSlot,
            ByteBufCodecs.BOOL, AccessoryItemDataComponent::isActive,
            AccessoryItemDataComponent::new);

    private int slot;
    private boolean active;

    private AccessoryItemDataComponent(int slot, boolean active) {
        this.slot = slot;
        this.active = active;
    }

    public AccessoryItemDataComponent() {
        this(-1, false);
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setActive(boolean value) {
        active = value;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AccessoryItemDataComponent other) {
            return slot == other.slot && active == other.active;
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * slot + Boolean.hashCode(active);
    }
}
