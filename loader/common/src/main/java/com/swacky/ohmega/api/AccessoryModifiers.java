package com.swacky.ohmega.api;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

/**
 * A class for adding default {@link AttributeModifier}s to accessory items.
 * <p>
 * Supports attributes added when equipped, and when equipped but also active ({@link AccessoryHelper#isActive(ItemStack)}
 */
public final class AccessoryModifiers {
    private final Multimap<Attribute, AttributeModifier> passiveModifiers;
    private final Multimap<Attribute, AttributeModifier> activeModifiers;

    private AccessoryModifiers(Multimap<Attribute, AttributeModifier> passiveModifiers, Multimap<Attribute, AttributeModifier> activeModifiers) {
        this.passiveModifiers = passiveModifiers;
        this.activeModifiers = activeModifiers;
    }

    public Multimap<Attribute, AttributeModifier> getPassive() {
        return passiveModifiers;
    }

    public Multimap<Attribute, AttributeModifier> getActive() {
        return activeModifiers;
    }

    public static class Builder {
        private ImmutableMultimap.Builder<Attribute, AttributeModifier> passiveModifiers = new ImmutableMultimap.Builder<>();
        private ImmutableMultimap.Builder<Attribute, AttributeModifier> activeModifiers = new ImmutableMultimap.Builder<>();

        /**
         * Add a modifier to the accessory to be applied when equipped
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         * @param active if true, the modifier will only be applied when the accessory is active
         */
        public void add(Attribute attribute, AttributeModifier modifier, boolean active) {
            if (active) {
                activeModifiers.put(attribute, modifier);
            } else {
                passiveModifiers.put(attribute, modifier);
            }
        }

        /**
         * A shortcut method to add a modifier to the accessory applied when the item is equipped
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public void addPassive(Attribute attribute, AttributeModifier modifier) {
            add(attribute, modifier, false);
        }

        /**
         * A shortcut method to add a modifier to the accessory applied when the item is equipped and active
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public void addActive(Attribute attribute, AttributeModifier modifier) {
            add(attribute, modifier, true);
        }

        /**
         * @return all default attribute modifiers that will be applied when built ({@link #build()}) into a {@link AccessoryModifiers}
         */
        @SuppressWarnings("unused")
        public Multimap<Attribute, AttributeModifier> getPassiveModifiers() {
            return passiveModifiers.build();
        }

        /**
         * @return all default attribute modifiers that will only be applied when the accessory is active when built ({@link #build()}) into a {@link AccessoryModifiers}
         */
        @SuppressWarnings("unused")
        public Multimap<Attribute, AttributeModifier> getActiveModifiers() {
            return activeModifiers.build();
        }

        /**
         * Clears all attribute modifiers
         */
        public void clear() {
            passiveModifiers = ImmutableMultimap.builder();
            activeModifiers = ImmutableMultimap.builder();
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