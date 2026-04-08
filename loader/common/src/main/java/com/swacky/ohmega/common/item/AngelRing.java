package com.swacky.ohmega.common.item;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.SoundData;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * This is simply a test and example accessory; its code may be used as a reference to create your own
 * <p>
 * You may also add a recipe to this if desired for survival use, or change how it functions through the use of events provided by the API
 */
public class AngelRing extends Item implements IAccessory {
    public AngelRing(Properties properties) {
        super(properties);
    }

    // This method uses the utility class to easily add tooltips onto the accessory
    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, @NonNull TooltipFlag flag) {
        tooltip.accept(AccessoryHelper.getBindTooltip(stack));
    }

    // Activates the accessory when you equip it
    @Override
    public void onEquip(@NonNull Player player, @NonNull ItemStack stack) {
        AccessoryHelper.setActive(player, stack, true);
    }

    // Deactivates when unequipped, also this makes it not force a creative player to stop flying when taking off the accessory
    @Override
    public void onUnequip(@NonNull Player player, @NonNull ItemStack stack) {
        if (!(player.isCreative() || player.isSpectator())) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
        }

        AccessoryHelper.setActive(player, stack, false);
    }

    // Tick method is needed here as there are so many edge cases to account for, it is easier to just use the tick method
    @Override
    public void accessoryTick(@NonNull Player player, @NonNull ItemStack stack) {
        if (!(player.isCreative() || player.isSpectator())) {
            if (AccessoryHelper.isActive(stack)) {
                player.getAbilities().mayfly = true;
            } else {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
            }
        }
    }

    // Toggle the accessory being active when the keybind is pressed
    @Override
    public void onKeybindUse(@NonNull Player player, @NonNull ItemStack stack) {
        AccessoryHelper.toggleActive(player, stack);
    }

    // Makes the accessory have the enchanted glint when equipped
    // No super() call as it may be confusing if active when enchanted, and is not intended to be enchantable.
    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return AccessoryHelper.isActive(stack);
    }

    // Adds modifiers to be applied when the accessory is equipped
    @Override
    public void addAttributeModifiers(AccessoryModifiers.@NonNull Builder builder) {
        builder
                // This modifier is always applied
                .addPassive(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        Ohmega.id(BuiltInRegistries.ITEM.getKey(this).toLanguageKey() + ".effect.strength"),
                        1,
                        AttributeModifier.Operation.ADD_VALUE))
                // This modifier is only applied when the accessory is active
                .addActive(Attributes.MAX_HEALTH, new AttributeModifier(
                        Ohmega.id(BuiltInRegistries.ITEM.getKey(this).toLanguageKey() + ".effect.health_boost"),
                        4,
                        AttributeModifier.Operation.ADD_VALUE));
    }

    // The sound to be played when equipped using a right-click
    @Nullable
    @Override
    public SoundData getEquipSound() {
        return new SoundData(SoundEvents.ARMOR_EQUIP_GOLD);
    }

    // The angel ring is a gold item, so we consider it "safe" to piglins
    @Override
    public boolean isPiglinSafe(ItemStack stack) {
        return true;
    }
}
