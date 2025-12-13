package com.swacky.ohmega.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * A class for adding default {@link AttributeModifier}s to accessory items.
 */
public class ModifierHolder {
    public static final Codec<ModifierHolder> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemAttributeModifiers.CODEC.fieldOf("passiveModifiers").forGetter(ModifierHolder::getPassive),
            ItemAttributeModifiers.CODEC.fieldOf("activeModifiers").forGetter(ModifierHolder::getActive)
    ).apply(inst, ModifierHolder::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifierHolder> STREAM_CODEC = StreamCodec.composite(
            ItemAttributeModifiers.STREAM_CODEC,
            ModifierHolder::getPassive,
            ItemAttributeModifiers.STREAM_CODEC,
            ModifierHolder::getActive,
            ModifierHolder::new
    );

    public static final ModifierHolder EMPTY = new Builder().build();

    private final ItemAttributeModifiers passiveModifiers;
    private final ItemAttributeModifiers activeModifiers;

    private ModifierHolder(ItemAttributeModifiers passiveModifiers, ItemAttributeModifiers activeModifiers) {
        this.passiveModifiers = passiveModifiers;
        this.activeModifiers = activeModifiers;
    }

    public ItemAttributeModifiers getPassive() {
        return this.passiveModifiers;
    }

    public ItemAttributeModifiers getActive() {
        return this.activeModifiers;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ModifierHolder other) {
            return this.passiveModifiers.equals(other.passiveModifiers) && this.activeModifiers.equals(other.activeModifiers);
        }

        return false;
    }

    public static class Builder {
        private ItemAttributeModifiers.Builder passiveModifiers = ItemAttributeModifiers.builder();
        private ItemAttributeModifiers.Builder activeModifiers = ItemAttributeModifiers.builder();

        /**
         * Add a modifier to the accessory to be applied when equipped
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         * @param active if true, the modifier will only be applied when the accessory is active
         */
        public void add(Holder<Attribute> attribute, AttributeModifier modifier, boolean active) {
            if (active) {
                activeModifiers.add(attribute, modifier, EquipmentSlotGroup.ANY);
            } else {
                passiveModifiers.add(attribute, modifier, EquipmentSlotGroup.ANY);
            }
        }

        /**
         * A shortcut method to add a modifier to the accessory applied when the item is equipped
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public void addPassive(Holder<Attribute> attribute, AttributeModifier modifier) {
            add(attribute, modifier, false);
        }

        /**
         * A shortcut method to add a modifier to the accessory applied when the item is equipped and active
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public void addActive(Holder<Attribute> attribute, AttributeModifier modifier) {
            add(attribute, modifier, true);
        }

        /**
         * @return all default attribute modifiers that will be applied when built ({@link #build()}) into a {@link ModifierHolder}
         */
        @SuppressWarnings("unused")
        public ItemAttributeModifiers getPassiveModifiers() {
            return this.passiveModifiers.build();
        }

        /**
         * @return all default attribute modifiers that will only be applied when the accessory is active when built ({@link #build()}) into a {@link ModifierHolder}
         */
        @SuppressWarnings("unused")
        public ItemAttributeModifiers getActiveModifiers() {
            return this.activeModifiers.build();
        }

        public void clear() {
            this.passiveModifiers = ItemAttributeModifiers.builder();
            this.activeModifiers = ItemAttributeModifiers.builder();
        }

        public ModifierHolder build() {
            return new ModifierHolder(this.passiveModifiers.build(), this.activeModifiers.build());
        }
    }
}