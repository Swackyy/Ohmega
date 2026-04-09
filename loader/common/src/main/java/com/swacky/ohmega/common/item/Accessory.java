package com.swacky.ohmega.common.item;

import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.EquipContext;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.SoundData;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * An immutable decorator class for {@link IAccessory} that wraps functions with corresponding event invocations.
 * Every accessory will be wrapped with this class.
 */
public final class Accessory implements IAccessory {
    private final IAccessory inner;

    public Accessory(IAccessory inner) {
        this.inner = inner;
    }

    @Override
    public void accessoryTick(@NonNull Player player, @NonNull ItemStack stack) {
        if (!OhmegaHooks.accessoryTickPre(player, stack)) {
            inner.accessoryTick(player, stack);
            OhmegaHooks.accessoryTickPost(player, stack);
        }
    }

    @Override
    public void onEquip(@NonNull Player player, @NonNull ItemStack stack, @NonNull EquipContext context) {
        if (!OhmegaHooks.equip(player, stack, context)) {
            inner.onEquip(player, stack, context);
        }
    }

    @Override
    public void onUnequip(@NonNull Player player, @NonNull ItemStack stack) {
        if (!OhmegaHooks.unequip(player, stack)) {
            inner.onUnequip(player, stack);
        }
    }

    @Override
    public boolean canEquip(@NonNull Player player, @NonNull ItemStack stack, @NonNull EquipContext context) {
        return OhmegaHooks.canEquip(player, stack, context, inner.canEquip(player, stack, context));
    }

    @Override
    public boolean canUnequip(@NonNull Player player, @NonNull ItemStack stack) {
        return OhmegaHooks.canUnequip(player, stack, inner.canUnequip(player, stack));
    }

    @Override
    public boolean compatibleWith(@NonNull ItemStack stack, @NonNull ItemStack other) {
        return OhmegaHooks.compatibleWith(stack, other, inner.compatibleWith(stack, other));
    }

    @Override
    public void onKeybindUse(@NonNull Player player, @NonNull ItemStack stack) {
        if (!OhmegaHooks.keybindUse(player, stack)) {
            inner.onKeybindUse(player, stack);
        }
    }

    @Override
    public boolean autoSync(@NonNull ItemStack stack) {
        return OhmegaHooks.autoSync(stack, inner.autoSync(stack));
    }

    @Override
    public void addAttributeModifiers(@NonNull ItemStack stack, AccessoryModifiers.@NonNull Builder builder) {
        inner.addAttributeModifiers(stack, builder);
        OhmegaHooks.attributeModifiers(stack, builder);
    }

    @Override
    public boolean preferVanillaUse(@NonNull ItemStack stack) {
        return OhmegaHooks.preferVanillaUse(stack, inner.preferVanillaUse(stack));
    }

    @Override
    public @Nullable SoundData getEquipSound(@NonNull ItemStack stack) {
        return OhmegaHooks.equipSound(stack, inner.getEquipSound(stack));
    }

    @Override
    public boolean allowWalkOnPowderSnow(@NonNull ItemStack stack) {
        return OhmegaHooks.allowWalkOnPowderSnow(stack, inner.allowWalkOnPowderSnow(stack));
    }

    @Override
    public double getMobVisibilityMultiplier(@NonNull ItemStack stack, @NonNull Entity targetingEntity) {
        return OhmegaHooks.mobVisibility(stack, targetingEntity, inner.getMobVisibilityMultiplier(stack, targetingEntity));
    }

    @Override
    public boolean isPiglinSafe(@NonNull ItemStack stack) {
        return OhmegaHooks.isPiglinSafe(stack, inner.isPiglinSafe(stack));
    }
}
