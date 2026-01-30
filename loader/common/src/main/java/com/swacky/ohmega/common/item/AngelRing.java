package com.swacky.ohmega.common.item;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.IAccessory;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

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
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull List<Component> tooltip, @NonNull TooltipFlag flag) {
        tooltip.add(AccessoryHelper.getBindTooltip(stack));
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
    public void tick(@NonNull Player player, @NonNull ItemStack stack) {
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
    public void onUse(@NonNull Player player, @NonNull ItemStack stack) {
        AccessoryHelper.toggleActive(player, stack);
    }

    // Makes the accessory have the enchanted glint when equipped
    // No super as it may be confusing if active when enchanted, also it is not intended to be enchantable.
    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return AccessoryHelper.isActive(stack);
    }

    // Adds modifiers to be applied when the accessory is equipped
    @Override
    public void addDefaultAttributeModifiers(AccessoryModifiers.@NonNull Builder builder) {
        // This modifier is only applied when the accessory is active
        builder.addPassive(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("426441fe-997d-4e84-a441-fe997d4e84e4"), "Attack damage", 1, AttributeModifier.Operation.ADD_VALUE));

        // This modifier is always applied
        builder.addActive(Attributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("3bfe251b-ac2b-4409-be25-1bac2bd409c1"), "Health boost", 4, AttributeModifier.Operation.ADD_VALUE));
    }

    // The sound to be played when equipped using a right click
    @Nullable
    @Override
    public Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GOLD;
    }
}
