package com.swacky.ohmega.api;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * A class for adding default {@link AttributeModifier}s to accessory items.
 * <p>
 * Supports attributes added when equipped, and when equipped but also active ({@link AccessoryHelper#isActive(ItemStack)}
 */
// todo: allow for other conditions here
public final class AccessoryModifiers {
    public static final AccessoryModifiers EMPTY = Builder.EMPTY.build();

    private final ItemAttributeModifiers passiveModifiers;
    private final ItemAttributeModifiers activeModifiers;

    private AccessoryModifiers(ItemAttributeModifiers passiveModifiers, ItemAttributeModifiers activeModifiers) {
        this.passiveModifiers = passiveModifiers;
        this.activeModifiers = activeModifiers;
    }

    public ItemAttributeModifiers getPassive() {
        return passiveModifiers;
    }

    public ItemAttributeModifiers getActive() {
        return activeModifiers;
    }

    public static class Builder {
        public static final Builder EMPTY = new Builder();

        private ItemAttributeModifiers.Builder passiveModifiers = ItemAttributeModifiers.builder();
        private ItemAttributeModifiers.Builder activeModifiers = ItemAttributeModifiers.builder();

        private void add(Holder<Attribute> attribute, AttributeModifier modifier, boolean active) {
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
        public Builder addPassive(Holder<Attribute> attribute, AttributeModifier modifier) {
            add(attribute, modifier, false);
            return this;
        }

        /**
         * Add a modifier to the accessory applied when the item is equipped and active
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public Builder addActive(Holder<Attribute> attribute, AttributeModifier modifier) {
            add(attribute, modifier, true);
            return this;
        }

        /**
         * @return all default attribute modifiers that will be applied when built ({@link #build()}) into a {@link AccessoryModifiers}
         */
        @SuppressWarnings("unused")
        public ItemAttributeModifiers getPassiveModifiers() {
            return passiveModifiers.build();
        }

        /**
         * @return all default attribute modifiers that will only be applied when the accessory is active when built ({@link #build()}) into a {@link AccessoryModifiers}
         */
        @SuppressWarnings("unused")
        public ItemAttributeModifiers getActiveModifiers() {
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
        public AccessoryModifiers build() {
            return new AccessoryModifiers(passiveModifiers.build(), activeModifiers.build());
        }
    }
}