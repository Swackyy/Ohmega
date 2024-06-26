package com.swacky.ohmega.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// The base interface for all accessory items
@SuppressWarnings("unused")
public interface IAccessory {
    @NotNull AccessoryType getType();

    // Gets called every tick
    default void tick(Player player, ItemStack stack) {
    }

    // Is called upon the player equipping the accessory
    default void onEquip(Player player, ItemStack stack) {
    }

    // Is called upon the player un-equipping the accessory
    default void onUnequip(Player player, ItemStack stack) {
    }

    // If true, the player can equip the accessory
    default boolean canEquip(Player player, ItemStack stack) {
        return true;
    }

    // If true, the player can un-equip the accessory
    default boolean canUnequip(Player player, ItemStack stack) {
        return true;
    }

    // Called when the user chooses, such as for the fly ring (example item),
    // this could be used to make sure the player can still fly upon switching game modes.
    default void update(Player player, ItemStack stack) {
    }

    // Do not override this as it can cause issues. Used internally only like the Enchantment system.
    default boolean isCompatibleWith(IAccessory other) {
        return this.checkCompatibility(other) && other.checkCompatibility(this);
    }

    // Override this to make accessories only equipable when certain other accessories are not equipped. Default is just self
    default boolean checkCompatibility(IAccessory other) {
        return this != other;
    }

    // Called when a key bind is pressed for this slot. Will only work for utility and special slots.
    // It is recommended that when this is overridden and used, that a tooltip will be provided.
    // A component for the tooltip can be acquired from the AccessoryHelper utility class.
    default void onUse(Player player, ItemStack stack) {
    }

    // If true, will automatically sync with the client every tick
    default boolean autoSync(Player player) {
        return false;
    }

    @Nullable
    default SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GENERIC;
    }

    // Attribute modifiers to be applied when the accessory is equipped (or only when active)
    default void addDefaultAttributeModifiers(ModifierBuilder builder) {
    }

    /**
     * A utility class for adding default attribute modifiers to accessory items.
     */
    class ModifierBuilder {
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        Multimap<Attribute, AttributeModifier> modifiersActiveOnly = HashMultimap.create();

        public static final ModifierBuilder EMPTY = new ModifierBuilder();

        /**
         * Add a modifier to the accessory to be applied when equipped
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         * @param activeOnly if true, the modifier will only be applied when the accessory is active
         */
        public void addModifier(Attribute attribute, AttributeModifier modifier, boolean activeOnly) {
            if(activeOnly) {
                modifiersActiveOnly.put(attribute, modifier);
            } else {
                modifiers.put(attribute, modifier);
            }
        }

        /**
         * A shortcut method to add a modifier to the accessory applied when the item is equipped
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public void addModifier(Attribute attribute, AttributeModifier modifier) {
            addModifier(attribute, modifier, false);
        }

        /**
         * A shortcut method to add a modifier to the accessory applied when the item is equipped and active
         * @param attribute the attribute to modify
         * @param modifier defines how the attribute supplied will be modified
         */
        public void addModifierActiveOnly(Attribute attribute, AttributeModifier modifier) {
            addModifier(attribute, modifier, true);
        }

        /**
         * @return all default attribute modifiers that will be applied
         */
        public Multimap<Attribute, AttributeModifier> getModifiers() {
            return modifiers;
        }

        /**
         * @return all default attribute modifiers that will only be applied when the accessory is active
         */
        public Multimap<Attribute, AttributeModifier> getModifiersActiveOnly() {
            return modifiersActiveOnly;
        }

        public void clear() {
            this.modifiers.clear();
            this.modifiersActiveOnly.clear();
        }

        public ListTag serialize() {
            ListTag list = new ListTag();

            for(Attribute attribute : this.modifiers.keys()) {
                for(AttributeModifier modifier : this.modifiers.get(attribute)) {
                    CompoundTag element = modifier.save();
                    element.putString("AttributeName", Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(attribute)).toString());
                    element.putBoolean("ActiveOnly", false);

                    list.add(element);
                }
            }

            for(Attribute attribute : this.modifiersActiveOnly.keys()) {
                for(AttributeModifier modifier : this.modifiersActiveOnly.get(attribute)) {
                    CompoundTag element = modifier.save();
                    element.putString("AttributeName", Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey(attribute)).toString());
                    element.putBoolean("ActiveOnly", true);

                    list.add(element);
                }
            }

            return list;
        }

        public static ModifierBuilder deserialize(ItemStack stack) {
            ModifierBuilder builder = new ModifierBuilder();

            for(Tag _tag : AccessoryHelper._internalTag(stack).getList("AccessoryAttributeModifiers", 10)) {
                CompoundTag tag = (CompoundTag) _tag;
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.tryParse(tag.getString("AttributeName")));
                AttributeModifier modifier = AttributeModifier.load(tag);

                if(tag.getBoolean("ActiveOnly")) {
                    builder.modifiersActiveOnly.put(attribute, modifier);
                } else {
                    builder.modifiers.put(attribute, modifier);
                }
            }

            return builder;
        }
    }
}
