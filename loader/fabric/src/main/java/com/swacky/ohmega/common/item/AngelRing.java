package com.swacky.ohmega.common.item;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.ModifierHolder;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(AccessoryHelper.getBindTooltip(new TranslatableContents(this.getDescriptionId() + ".tooltip.keybind", null, TranslatableContents.NO_ARGS), stack, new TranslatableContents(this.getDescriptionId() + ".tooltip", null, TranslatableContents.NO_ARGS)));
        Component component = AccessoryHelper.getTypeTooltip(this);
        if (component != null) {
            tooltip.accept(component);
        }
    }

    // Activates the accessory when you equip it
    @Override
    public void onEquip(Player player, ItemStack stack) {
        AccessoryHelper.activate(player, stack);
    }

    // Deactivates when unequipped, also this makes it not force a creative player to stop flying when taking off the accessory
    @Override
    public void onUnequip(Player player, ItemStack stack) {
        if (!(player.isCreative() || player.isSpectator())) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
        }
        AccessoryHelper.deactivate(player, stack);
    }

    // Tick method is needed here as there are so many edge cases to account for, it is easier to just use the tick method
    @Override
    public void tick(Player player, ItemStack stack) {
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
    public void onUse(Player player, ItemStack stack) {
        AccessoryHelper.toggle(player, stack);
    }

    // Makes the accessory have the enchanted glint when equipped
    // No super as it may be confusing if active when enchanted, also it is not intended to be enchantable.
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return AccessoryHelper.isActive(stack);
    }

    // Adds modifiers to be applied when the accessory is equipped
    @Override
    public void addDefaultAttributeModifiers(ModifierHolder.Builder builder) {
        // This modifier is only applied when the accessory is active
        builder.addPassive(Attributes.ATTACK_DAMAGE, new AttributeModifier(OhmegaCommon.rl(BuiltInRegistries.ITEM.getKey(this).toLanguageKey() + ".effect.strength"), 1, AttributeModifier.Operation.ADD_VALUE));

        // This modifier is always applied
        builder.addActive(Attributes.MAX_HEALTH, new AttributeModifier(OhmegaCommon.rl(BuiltInRegistries.ITEM.getKey(this).toLanguageKey() + ".effect.health_boost"), 4, AttributeModifier.Operation.ADD_VALUE));
    }

    // The sound to be played when equipped using a right click
    @Nullable
    @Override
    public Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GOLD;
    }
}
