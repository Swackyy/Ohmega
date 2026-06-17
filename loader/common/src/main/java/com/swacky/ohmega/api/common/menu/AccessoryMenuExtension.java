package com.swacky.ohmega.api.common.menu;

import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.network.C2S.SetExtensionVisiblePacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * A way to add extra slots and functionality to the default inventory.
 * This does not override any vanilla behaviour such as inventory slots, it is purely an extension
 * <p>
 * This <strong>is</strong> a client class, and so you shouldn't assume behaviour will be replaced on the logical server.
 * The only reason this is in the {@code common} package is because Ohmega defines one singular server implementation for internal use
 */
public abstract class AccessoryMenuExtension {
    private final @NonNull AbstractContainerMenu menu;
    private final @NonNull IAccessoryMenu accessoryMenu;
    private final @NonNull Player owner;

    private @Nullable List<AccessorySlot> slots = null;
    private boolean visible = false;

    public AccessoryMenuExtension(@NonNull AbstractContainerMenu menu, @NonNull Player owner) {
        this.menu = menu;
        this.accessoryMenu = (IAccessoryMenu) menu;
        this.owner = owner;
    }

    /**
     * Retrieve the actual {@link AbstractContainerMenu} instance which is also {@link #getAccessoryMenu()}
     * @return container menu bound to the extension interface holding this extension
     */
    public @NonNull AbstractContainerMenu getMenu() {
        return menu;
    }

    /**
     * Retrieve the {@link IAccessoryMenu} which holds this as the active accessory menu extension
     * @return accessory menu interface holding this extension
     */
    public @NonNull IAccessoryMenu getAccessoryMenu() {
        return accessoryMenu;
    }

    /**
     * Retrieve the owner of this accessory inventory
     * @return owner of the menu linked to the accessory extension
     */
    public @NonNull Player getOwner() {
        return owner;
    }

    /**
     * Get a list of the accessory slots added to the extension's parent menu.
     * This is stored as to eliminate the need for looping through all the slots just to perform operations on our custom ones
     * @return a list of strictly {@link AccessorySlot}s added with the accessory extension
     */
    public @Nullable List<AccessorySlot> getSlots() {
        return slots;
    }

    /**
     * Initially sets the list for added slots in the accessory extension.
     * This is called internally and should most likely not be replicated
     * @param slots added slots
     */
    public void setSlots(@NonNull List<AccessorySlot> slots) {
        if (this.slots == null) {
            this.slots = slots;
        } else {
            throw new IllegalStateException("Slot list cannot be set as it has already been initialised");
        }
    }

    /**
     * Call {@link IAccessoryMenu#isAccessoryExtensionVisible()} instead
     * <p>
     * Determines whether the extension should be shown
     * @return {@code true} if the accessory extension should be shown, {@code false} otherwise
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Call {@link IAccessoryMenu#setAccessoryExtensionVisible(boolean)} instead
     * <p>
     * Set the visibility of the extension
     * @param value {@code true} to make the extension visible, {@code false} to hide it
     */
    public void setVisible(boolean value) {
        visible = value;

        if (owner.level().isClientSide()) {
            OhmegaNetworking.C2S.send(new SetExtensionVisiblePacket(value));
        }
    }

    /**
     * Add slots to the accessory inventory, these will be displayed in the extension pop-up.
     * You should ensure that you add the correct amount of slots ({@code AccessoryHelper.getSlotTypes().size()})
     * <p>
     * This is only called on the logical client, whilst the server specific behaviour is handled internally by Ohmega
     * @param adder a function reference to {@link AbstractContainerMenu#addSlot(Slot)} with some behaviour automatically handled
     */
    public abstract void addSlots(@NonNull SlotAdder adder);

    /**
     * A simple interface to add slots to the extension, where other operations have been abstracted away to be handled internally
     */
    public interface SlotAdder {
        /**
         * Adds a slot to the accessory extension, with position relative to the extension itself
         * @param index slot index to add
         * @param x x-coordinate relative to the accessory extension
         * @param y y-coordinate relative to the accessory extension
         */
        void add(int index, int x, int y);
    }

    public interface Factory {
        @NonNull AccessoryMenuExtension construct(@NonNull AbstractContainerMenu menu, @NonNull Player player);
    }
}
