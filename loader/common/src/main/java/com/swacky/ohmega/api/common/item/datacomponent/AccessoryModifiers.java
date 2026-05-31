package com.swacky.ohmega.api.common.item.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
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
 * Supports attributes added when an item is in the slot (passive), and when in the slot but also active ({@link AccessoryHelper#isActive(ItemStack)}
 */
// todo: refactor uses of this and reevaluate the existence of this class
public final class AccessoryModifiers {
    public static final @NonNull AccessoryModifiers EMPTY = Builder.EMPTY.build();

    public static final @NonNull Codec<AccessoryModifiers> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ItemAttributeModifiers.CODEC.fieldOf("passive").forGetter(AccessoryModifiers::getPassive),
            ItemAttributeModifiers.CODEC.fieldOf("active").forGetter(AccessoryModifiers::getActive)
    ).apply(builder, AccessoryModifiers::new));

    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, AccessoryModifiers> STREAM_CODEC = StreamCodec.composite(
            ItemAttributeModifiers.STREAM_CODEC, AccessoryModifiers::getPassive,
            ItemAttributeModifiers.STREAM_CODEC, AccessoryModifiers::getActive,
            AccessoryModifiers::new);

    private final @NonNull ItemAttributeModifiers passiveModifiers;
    private final @NonNull ItemAttributeModifiers activeModifiers;

    private AccessoryModifiers(@NonNull ItemAttributeModifiers passiveModifiers, @NonNull ItemAttributeModifiers activeModifiers) {
        this.passiveModifiers = passiveModifiers;
        this.activeModifiers = activeModifiers;
    }

    public @NonNull ItemAttributeModifiers getPassive() {
        return passiveModifiers;
    }

    public @NonNull ItemAttributeModifiers getActive() {
        return activeModifiers;
    }

    public static class Builder {
        public static final @NonNull Builder EMPTY = new Builder();

        private ItemAttributeModifiers.@NonNull Builder passiveModifiers = ItemAttributeModifiers.builder();
        private ItemAttributeModifiers.@NonNull Builder activeModifiers = ItemAttributeModifiers.builder();

        private void add(@NonNull Holder<Attribute> attribute, @NonNull AttributeModifier modifier, boolean active) {
            if (active) {
                activeModifiers.add(attribute, modifier, EquipmentSlotGroup.ANY);
            } else {
                passiveModifiers.add(attribute, modifier, EquipmentSlotGroup.ANY);
            }
        }

        /**
         * Add a modifier to the accessory applied when the item is equipped
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public Builder addPassive(@NonNull Holder<Attribute> attribute, @NonNull AttributeModifier modifier) {
            add(attribute, modifier, false);
            return this;
        }

        /**
         * Add a modifier to the accessory applied when the item is equipped and active
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public Builder addActive(@NonNull Holder<Attribute> attribute, @NonNull AttributeModifier modifier) {
            add(attribute, modifier, true);
            return this;
        }

        /**
         * @return all default attribute modifiers that will be applied when built ({@link #build()}) into a {@link AccessoryModifiers}
         */
        @SuppressWarnings("unused")
        public @NonNull ItemAttributeModifiers getPassiveModifiers() {
            return passiveModifiers.build();
        }

        /**
         * @return all default attribute modifiers that will only be applied when the accessory is active when built ({@link #build()}) into a {@link AccessoryModifiers}
         */
        @SuppressWarnings("unused")
        public @NonNull ItemAttributeModifiers getActiveModifiers() {
            return activeModifiers.build();
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
         * @return the built {@link AccessoryModifiers}
         */
        public @NonNull AccessoryModifiers build() {
            return new AccessoryModifiers(passiveModifiers.build(), activeModifiers.build());
        }
    }
}