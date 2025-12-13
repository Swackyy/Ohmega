package com.swacky.ohmega.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.ModifierHolder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class AccessoryItemDataComponent {
    public static final Codec<AccessoryItemDataComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("slot").forGetter(AccessoryItemDataComponent::getSlot),
            Codec.BOOL.fieldOf("active").forGetter(AccessoryItemDataComponent::isActive),
            ModifierHolder.CODEC.fieldOf("modifierHolder").forGetter(AccessoryItemDataComponent::getModifiers)
    ).apply(inst, AccessoryItemDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AccessoryItemDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AccessoryItemDataComponent::getSlot,
            ByteBufCodecs.BOOL, AccessoryItemDataComponent::isActive,
            ModifierHolder.STREAM_CODEC, AccessoryItemDataComponent::getModifiers,
            AccessoryItemDataComponent::new
    );

    private int slot;
    private boolean active;
    private ModifierHolder modifierHolder;

    public AccessoryItemDataComponent(int slot, boolean active, ModifierHolder modifierHolder) {
        this.slot = slot;
        this.active = active;
        this.modifierHolder = modifierHolder;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setActive(boolean value) {
        this.active = value;
    }

    public void setModifiers(ModifierHolder modifierHolder) {
        this.modifierHolder = modifierHolder;
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean isActive() {
        return this.active;
    }

    public ModifierHolder getModifiers() {
        return this.modifierHolder;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AccessoryItemDataComponent other) {
            return this.slot == other.slot && this.active == other.active && this.modifierHolder.equals(other.modifierHolder);
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
