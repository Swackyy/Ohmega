package com.swacky.ohmega.compat.trinkets.mixin;

import com.swacky.ohmega.api.common.menu.AccessorySlot;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import eu.pb4.trinkets.api.TrinketInventory;
import eu.pb4.trinkets.api.TrinketsApi;
import eu.pb4.trinkets.impl.slots.TrinketSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

// Trinkets reorders its slots and breaks the client-server duality here, had to do some tinkering to fix it
@Mixin(AbstractContainerMenu.class)
abstract class AbstractContainerMenuMixin {
    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @Shadow
    @Final
    private NonNullList<ItemStack> lastSlots;

    @Shadow
    @Final
    private NonNullList<RemoteSlot> remoteSlots;

    @Shadow
    @Nullable
    private ContainerSynchronizer synchronizer;

    @Inject(
            method = "addSlot",
            at = @At(
                    value = "HEAD"), cancellable = true)
    private void addSlot(Slot slot, CallbackInfoReturnable<Slot> cir) {
        if (slot instanceof TrinketSlot trinketSlot && this instanceof IAccessoryMenu accessoryMenu) {
            List<AccessorySlot> accessorySlots = accessoryMenu.getSlots();

            if (accessorySlots != null && !accessorySlots.isEmpty()) {
                // Slot order is already broken by an offset of the number of trinket slots
                int trinketSlots = 0;

                for (TrinketInventory inventory : TrinketsApi.getAttachment(trinketSlot.getOwner()).getInventories().values()) {
                    trinketSlots += inventory.getContainerSize();
                }

                int insertBeforeIndex = accessorySlots.getFirst().index - trinketSlots;
                slot.index = insertBeforeIndex;

                slots.add(insertBeforeIndex, slot);
                lastSlots.add(insertBeforeIndex, ItemStack.EMPTY);

                RemoteSlot remoteSlot;

                if (synchronizer != null) {
                    remoteSlot = synchronizer.createSlot();
                } else {
                    remoteSlot = RemoteSlot.PLACEHOLDER;
                }

                remoteSlots.add(remoteSlot);
                cir.setReturnValue(slot);
            }
        }
    }
}
