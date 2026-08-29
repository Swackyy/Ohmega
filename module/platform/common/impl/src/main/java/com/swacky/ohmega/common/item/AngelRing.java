package com.swacky.ohmega.common.item;

import com.swacky.ohmega.api.client.item.AccessoryHelper;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.init.OhmegaDataComponents;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.IAccessory;
import com.swacky.ohmega.api.common.item.SoundData;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
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
 * @apiNote Although this is technically an implementation of the API, it is in the {@code api} package as others may use extend from it if desired
 */
public class AngelRing extends Item implements IAccessory {
    public AngelRing(Properties properties) {
        super(properties);
    }

    // This method uses the utility class to easily add tooltips onto the accessory.
    // The accessory type tooltip is added internally by Ohmega
    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, @NonNull TooltipFlag flag) {
        tooltip.accept(AccessoryHelper.getBindTooltip(stack));
    }

    // Activates the accessory upon equipping
    @Override
    public void onEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
        OhmegaDataAttachments.getData(entity).setActive(entity, stack, true);
    }

    // Deactivates and prevents a player in survival from flying upon un-equipping
    @Override
    public void onUnequip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
        if (entity instanceof Player player && !(player.isCreative() || player.isSpectator())) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
        }

        OhmegaDataAttachments.getData(entity).setActive(entity, stack, false);
    }

    // Tick method is needed here as there are so many edge cases to account for, it is easier to just use the tick method
    @Override
    public void accessoryTick(@NonNull LivingEntity entity, @NonNull ItemStack stack) {
        if (entity instanceof Player player && !(player.isCreative() || player.isSpectator())) {
            if (OhmegaDataComponents.isActive(stack)) {
                player.getAbilities().mayfly = true;
            } else {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
            }
        }
    }

    // Toggle the accessory being active when the keybind is pressed
    @Override
    public boolean onKeybindUse(@NonNull Player player, @NonNull ItemStack stack) {
        OhmegaDataAttachments.getData(player).setActive(player, stack, !OhmegaDataComponents.isActive(stack));
        return true;
    }

    // Makes the accessory have the enchanted glint when equipped.
    // No super() call as it may be confusing if active when enchanted, and is not intended to be enchantable.
    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return OhmegaDataComponents.isActive(stack);
    }

    // The sound to be played when equipped using a right-click
    @Nullable
    @Override
    public SoundData getEquipSound(@NonNull ItemStack stack) {
        return new SoundData(SoundEvents.ARMOR_EQUIP_GOLD);
    }

    // The angel ring is a gold item, so we consider it "safe" to piglins
    @Override
    public boolean isPiglinSafe(@NonNull ItemStack stack) {
        return true;
    }
}
