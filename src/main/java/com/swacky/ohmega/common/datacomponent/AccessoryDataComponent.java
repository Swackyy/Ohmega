package com.swacky.ohmega.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.ModifierHolder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class AccessoryDataComponent {
    public static final Codec<AccessoryDataComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("slot").forGetter(AccessoryDataComponent::getSlot),
            Codec.BOOL.fieldOf("active").forGetter(AccessoryDataComponent::isActive),
            ModifierHolder.CODEC.fieldOf("modifierHolder").forGetter(AccessoryDataComponent::getModifiers)
    ).apply(inst, AccessoryDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AccessoryDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            AccessoryDataComponent::getSlot,
            ByteBufCodecs.BOOL,
            AccessoryDataComponent::isActive,
            ModifierHolder.STREAM_CODEC,
            AccessoryDataComponent::getModifiers,
            AccessoryDataComponent::new
    );

    private int slot;
    private boolean active;
    private ModifierHolder modifierHolder;

    public AccessoryDataComponent(int slot, boolean active, ModifierHolder modifierHolder) {
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
}
