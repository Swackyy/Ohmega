package com.swacky.ohmega.api.common.menu;

import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.common.item.EquipContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * Base accessory slot, extend to add your own custom slot behaviour for your accessory extension(s).
 * By default, this class provides all the functionality required for the slot to function properly and as intended
 * @apiNote Although this class resides within the {@code common} directory, custom slots will only be provided on the client,
 * and this class will be used as the default implementation for the server, hence it not being {@code abstract}.
 * This should not cause issues as custom slots should only be modifying client-specific behaviour.
 * To enforce this, many methods have been marked with the {@code final} qualifier.
 * When modifying these safe methods, you can safely avoid using a logical side guard as custom slot types are guaranteed to only exist on the client
 */
public class AccessorySlot extends Slot implements IAccessorySlot {
    private static final Container EMPTY_CONTAINER = new SimpleContainer(0);

    protected final Player player;
    protected final AccessoryData handler;
    private final AccessoryType type;
    private final int originalX;
    private final int originalY;

    public AccessorySlot(Player player, int index, int x, int y) {
        super(EMPTY_CONTAINER, index, x, y);

        this.player = player;
        this.handler = OhmegaDataAttachments.getData(player);
        this.type = handler.getEntry(index).getType();
        this.originalX = x;
        this.originalY = y;
    }

    public final @NonNull AccessoryType getType() {
        return type;
    }

    public final void applyOffset(int xo, int yo) {
        this.x = originalX + xo;
        this.y = originalY + yo;
    }

    @Override
    public boolean isActive() {
        if (player.containerMenu instanceof IAccessoryMenu menu) {
            if (!menu.isAccessoryExtensionVisible()) {
                return false;
            }
        }

        if (player.level().isClientSide()) {
            return AccessoryScreens.areExtensionWidgetsVisible();
        }

        return true;
    }

    @Override
    public final boolean mayPlace(@NonNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        Accessory accessory = Accessories.get(item);

        if (accessory != null) {
            return handler.getEntry(getContainerSlot()).isItemValid(player, stack, EquipContext.SLOT);
        }

        return false;
    }

    @Override
    public final boolean mayPickup(@NonNull Player player) {
        ItemStack stack = getItem();

        if (stack.isEmpty()) {
            return true;
        }

        if (EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
            return false;
        }

        Accessory accessory = Accessories.get(stack.getItem());

        if (accessory != null) {
            return accessory.canUnequip(player, getItem());
        }

        return super.mayPickup(player);
    }

    @Override
    public final @NonNull ItemStack getItem() {
        return handler.getEntry(getContainerSlot()).getStack();
    }

    @Override
    public final void onQuickCraft(@NonNull ItemStack oldStack, @NonNull ItemStack newStack) {}

    @Override
    public final int getMaxStackSize() {
        return 1;
    }

    @Override
    public final int getMaxStackSize(@NonNull ItemStack stack) {
        return getMaxStackSize();
    }

    @Override
    public final @NonNull ItemStack remove(int amount) {
        return handler.getEntry(getContainerSlot()).remove(player, amount, EquipContext.SLOT);
    }

    @Override
    public final void set(@NonNull ItemStack stack) {
        handler.getEntry(getContainerSlot()).setStack(player, stack, getContainerSlot(), EquipContext.SLOT);
    }

    @Override
    public final void setChanged() {}

    @Override
    public final boolean allowModification(@NonNull Player player) {
        return true;
    }

    @Override
    public Identifier getNoItemIcon() {
        return type.getEmptySlotLocation();
    }

    @Override
    protected final void onQuickCraft(@NonNull ItemStack stack, int amount) {
        super.onQuickCraft(stack, amount);
    }

    @Override
    protected final void onSwapCraft(int amount) {
        super.onSwapCraft(amount);
    }

    @Override
    protected final void checkTakeAchievements(@NonNull ItemStack stack) {
        super.checkTakeAchievements(stack);
    }

    @Override
    public final void onTake(@NonNull Player player, @NonNull ItemStack stack) {
        super.onTake(player, stack);
    }

    @Override
    public final boolean hasItem() {
        return super.hasItem();
    }

    @Override
    public final void setByPlayer(@NonNull ItemStack stack) {
        super.setByPlayer(stack);
    }

    @Override
    public final void setByPlayer(@NonNull ItemStack stack, @NonNull ItemStack previous) {
        super.setByPlayer(stack, previous);
    }

    @Override
    public final @NonNull Optional<ItemStack> tryRemove(int amount, int max, @NonNull Player player) {
        return super.tryRemove(amount, max, player);
    }

    @Override
    public final @NonNull ItemStack safeTake(int amount, int max, @NonNull Player player) {
        return super.safeTake(amount, max, player);
    }

    @Override
    public final @NonNull ItemStack safeClone(@NonNull Player player) {
        return super.safeClone(player);
    }

    @Override
    public final @NonNull ItemStack safeInsert(@NonNull ItemStack stack) {
        return super.safeInsert(stack);
    }

    @Override
    public final @NonNull ItemStack safeInsert(@NonNull ItemStack stack, int amount) {
        return super.safeInsert(stack, amount);
    }

    @Override
    public final int getContainerSlot() {
        return super.getContainerSlot();
    }

    @Override
    public final boolean isFake() {
        return super.isFake();
    }

    public interface Factory {
        @NonNull AccessorySlot construct(@NonNull Player player, int index, int x, int y);
    }
}
