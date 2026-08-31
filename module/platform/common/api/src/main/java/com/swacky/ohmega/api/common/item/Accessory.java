package com.swacky.ohmega.api.common.item;

import com.swacky.ohmega.api.common.event.OhmegaHooks;
import it.unimi.dsi.fastutil.booleans.BooleanBooleanPair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This is not the accessory interface, but a wrapper class for it that is mostly handled internally by Ohmega,
 * you'll want to use {@link IAccessory} instead if creating an accessory,
 * however this wrapper may be stored as the return type of {@link Accessories#get(Item)}, hence its place in the {@code api} package
 * <p>
 * An immutable decorator class for {@link IAccessory} that wraps functions with corresponding event invocations.
 * Every accessory will be wrapped with this class.
 */
public final class Accessory implements IAccessory {
    private final @NonNull IAccessory inner;

    /**
     * Wrap an {@link IAccessory} instance
     * @param inner the wrapped instance
     */
    Accessory(@NonNull IAccessory inner) {
        this.inner = inner;
    }

    /**
     * Do not use this unless you have good reason
     * @return the wrapped {@link IAccessory} instance stored within this class
     * @apiNote The decision of exposing the wrapped object instead of simply overriding {@link Object#equals(Object)} was to give developers
     * more versatility with the API, such as for checking if other accessories are subclasses of this (wrapped) one
     */
    public @NonNull IAccessory unwrap() {
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
    public boolean preferInventoryTick(@NonNull ItemStack stack) {
        return OhmegaHooks.preferInventoryTick(stack, inner.preferInventoryTick(stack));
    }

    @Override
    public void onEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
        if (!OhmegaHooks.equip(entity, stack, context)) {
            inner.onEquip(entity, stack, context);
        }
    }

    @Override
    public void onUnequip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
        if (!OhmegaHooks.unequip(entity, stack, context)) {
            inner.onUnequip(entity, stack, context);
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
    public boolean onKeybindUse(@NonNull Player player, @NonNull ItemStack stack) {
        BooleanBooleanPair pair = OhmegaHooks.keybindUse(player, stack);

        if (!pair.firstBoolean()) {
            return inner.onKeybindUse(player, stack);
        }

        return pair.secondBoolean();
    }

    @Override
    public boolean autoSync(@NonNull ItemStack stack) {
        return OhmegaHooks.autoSync(stack, inner.autoSync(stack));
    }

    @Override
    public byte autoSyncModulo(@NonNull ItemStack stack) {
        return OhmegaHooks.autoSyncModulo(stack, inner.autoSyncModulo(stack));
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
    public boolean shouldDropOnDeath(@NonNull ItemStack stack, @NonNull LivingEntity entity) {
        return OhmegaHooks.shouldDropOnDeath(stack, entity, inner.shouldDropOnDeath(stack, entity));
    }

    @Override
    public boolean allowWalkOnPowderSnow(@NonNull ItemStack stack) {
        return OhmegaHooks.allowWalkOnPowderSnow(stack, inner.allowWalkOnPowderSnow(stack));
    }

    @Override
    public double getMobVisibilityMultiplier(@NonNull ItemStack stack, @Nullable Entity targetingEntity) {
        return OhmegaHooks.mobVisibility(stack, targetingEntity, inner.getMobVisibilityMultiplier(stack, targetingEntity));
    }

    @Override
    public boolean isPiglinSafe(@NonNull ItemStack stack) {
        return OhmegaHooks.isPiglinSafe(stack, inner.isPiglinSafe(stack));
    }
}
