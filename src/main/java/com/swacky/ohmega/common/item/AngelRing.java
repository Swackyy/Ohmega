package com.swacky.ohmega.common.item;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.AccessoryType;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

// This is simply a test and example accessory.
// Its code may be used as a reference to create your own.
// You may also add a recipe to this if desired for survival use.
// Manipulation of its methods can be achieved through the use of events.
public class AngelRing extends Item implements IAccessory {
    public AngelRing() {
        super(new Properties().stacksTo(1));
    }

    // This method uses the utility class to easily add tooltips onto the accessory
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(AccessoryHelper.getBindTooltip(new TranslatableContents("item." + Ohmega.MODID + ".flyring.tooltip.keybind", null, TranslatableContents.NO_ARGS), stack, new TranslatableContents("item." + Ohmega.MODID + ".flyring.tooltip", null, TranslatableContents.NO_ARGS)));
        tooltip.add(AccessoryHelper.getTypeTooltip(this));
    }

    // Can only place in utility marked slots, meaning it may also be keybinded
    @Override
    public @NotNull AccessoryType getType() {
        return AccessoryType.UTILITY;
    }

    // Activates the accessory when you equip it
    @Override
    public void onEquip(Player player, ItemStack stack) {
        AccessoryHelper.activate(player, stack);
    }

    // Deactivates when unequipped, also this makes it not force a creative player to stop flying when taking off the accessory
    @Override
    public void onUnequip(Player player, ItemStack stack) {
        if(!(player.isCreative() || player.isSpectator())) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
        }
        AccessoryHelper.deactivate(player, stack);
    }

    // Tick method is needed here as there are so many edge cases to account for, it is easier to just use the tick method
    @Override
    public void tick(Player player, ItemStack stack) {
        if(!(player.isCreative() || player.isSpectator())) {
            if(AccessoryHelper.isActive(stack)) {
                player.getAbilities().mayfly = true;
            } else {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
            }
        }
    }

    // Toggle the accessory being active when the keybind is pressed
    @Override
    public void onUse(Player player, ItemStack stack) {
        AccessoryHelper.toggle(player, stack);
    }

    // This is different to the method above, this is the vanilla method that causes the right-clicking behaviour
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return AccessoryHelper.tryEquip(player, hand);
    }

    // Makes the accessory have the enchanted glint when equipped
    // No super as it may be confusing if active when enchanted, also it is not intended to have any enchantments.
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return AccessoryHelper.isActive(stack);
    }

    // The sound to be played when equipped using a right click
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GOLD.get();
    }

    // Adds modifiers to be applied when the accessory is equipped
    @Override
    public void addDefaultAttributeModifiers(ModifierBuilder builder) {
        // This modifier is only applied when the accessory is active
        builder.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("04622de9-8f97-46c5-a8dd-20133aa44e4e"), "Strength", 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

        // This modifier is always applied
        builder.addModifierActiveOnly(Attributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("854a57c3-592c-434b-aa7a-f6658a7857cb"), "MaxHealth", 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }
}
