package com.swacky.ohmega.api.common.item.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.common.init.OhmegaDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.NonNull;

/**
 * A class for adding default {@link AttributeModifier}s to accessory slots to apply when items are in them
 * <p>
 * Supports attributes added when an item is in the slot (passive), and when in the slot but also active ({@link OhmegaDataComponents#isActive(ItemStack)}
 */
public final class AccessorySlotModifiers {
    public static final @NonNull AccessorySlotModifiers EMPTY = new Builder().build();

    public static final @NonNull Codec<AccessorySlotModifiers> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ItemAttributeModifiers.CODEC.fieldOf("active").forGetter(AccessorySlotModifiers::getActive),
            ItemAttributeModifiers.CODEC.fieldOf("passive").forGetter(AccessorySlotModifiers::getPassive)
    ).apply(builder, AccessorySlotModifiers::new));

    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, AccessorySlotModifiers> STREAM_CODEC = StreamCodec.composite(
            ItemAttributeModifiers.STREAM_CODEC, AccessorySlotModifiers::getPassive,
            ItemAttributeModifiers.STREAM_CODEC, AccessorySlotModifiers::getActive,
            AccessorySlotModifiers::new);

    private final @NonNull ItemAttributeModifiers activeModifiers;
    private final @NonNull ItemAttributeModifiers passiveModifiers;

    private AccessorySlotModifiers(@NonNull ItemAttributeModifiers activeModifiers, @NonNull ItemAttributeModifiers passiveModifiers) {
        this.activeModifiers = activeModifiers;
        this.passiveModifiers = passiveModifiers;
    }

    public @NonNull ItemAttributeModifiers getActive() {
        return activeModifiers;
    }

    public @NonNull ItemAttributeModifiers getPassive() {
        return passiveModifiers;
    }

    public static class Builder {
        private ItemAttributeModifiers.@NonNull Builder activeModifiers = ItemAttributeModifiers.builder();
        private ItemAttributeModifiers.@NonNull Builder passiveModifiers = ItemAttributeModifiers.builder();

        /**
         * Add a modifier to the accessory applied when the item is equipped and active
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public Builder addActive(@NonNull Holder<Attribute> attribute, @NonNull AttributeModifier modifier) {
            activeModifiers.add(attribute, modifier, EquipmentSlotGroup.ANY);
            return this;
        }

        /**
         * Add a modifier to the accessory applied when the item is equipped
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public Builder addPassive(@NonNull Holder<Attribute> attribute, @NonNull AttributeModifier modifier) {
            passiveModifiers.add(attribute, modifier, EquipmentSlotGroup.ANY);
            return this;
        }

        /**
         * @return all default attribute modifiers that will only be applied when the accessory is active when built ({@link #build()}) into a {@link AccessorySlotModifiers}
         */
        @SuppressWarnings("unused")
        public @NonNull ItemAttributeModifiers getActiveModifiers() {
            return activeModifiers.build();
        }

        /**
         * @return all default attribute modifiers that will be applied when built ({@link #build()}) into a {@link AccessorySlotModifiers}
         */
        @SuppressWarnings("unused")
        public @NonNull ItemAttributeModifiers getPassiveModifiers() {
            return passiveModifiers.build();
        }

        /**
         * Clears all attribute modifiers
         */
        public void clear() {
            passiveModifiers = ItemAttributeModifiers.builder();
            activeModifiers = ItemAttributeModifiers.builder();
        }

        /**
         * Called internally
         * @return the built {@link AccessorySlotModifiers}
         */
        public @NonNull AccessorySlotModifiers build() {
            return new AccessorySlotModifiers(activeModifiers.build(), passiveModifiers.build());
        }
    }
}