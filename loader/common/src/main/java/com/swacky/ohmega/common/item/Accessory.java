package com.swacky.ohmega.common.item;

import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.EquipContext;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.SoundData;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

    /**
     * Do not use this unless you have good reason
     * <p>
     * The option of exposing the wrapped object instead of simply overriding {@link Object#equals(Object)} was chosen
     * to give developers more versatility with the API, such as for checking if other accessories are subclasses of this (wrapped) one
     * @return the wrapped {@link IAccessory} instance stored within this class
     */
    public IAccessory unwrap() {
        return inner;
    }

    @Override
    public void accessoryTick(@NonNull LivingEntity entity, @NonNull ItemStack stack) {
        if (!OhmegaHooks.accessoryTickPre(entity, stack)) {
            inner.accessoryTick(entity, stack);
            OhmegaHooks.accessoryTickPost(entity, stack);
        }
    }

    @Override
    public void onEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
        if (!OhmegaHooks.equip(entity, stack, context)) {
            inner.onEquip(entity, stack, context);
        }
    }

    @Override
    public void onUnequip(@NonNull LivingEntity entity, @NonNull ItemStack stack) {
        if (!OhmegaHooks.unequip(entity, stack)) {
            inner.onUnequip(entity, stack);
        }
    }

    @Override
    public boolean canEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
        return OhmegaHooks.canEquip(entity, stack, context, inner.canEquip(entity, stack, context));
    }

    @Override
    public boolean canUnequip(@NonNull LivingEntity entity, @NonNull ItemStack stack) {
        return OhmegaHooks.canUnequip(entity, stack, inner.canUnequip(entity, stack));
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
