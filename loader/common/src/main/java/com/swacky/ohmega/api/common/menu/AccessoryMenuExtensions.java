package com.swacky.ohmega.api.common.menu;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtensions;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Holds known screen extension factories and common methods related to screen extensions
 * <p>
 * In order for your extension to function properly, you should call:
 * <ul>
 *     <li>{@link #onConstruct(AbstractContainerMenu, Player)}</li>
 *     <li>{@link #onQuickMoveStack(AbstractContainerMenu, Player, int)}</li>
 * </ul>
 */
public final class AccessoryMenuExtensions {
    private static final Map<Identifier, AccessoryMenuExtension.Factory> MENU_EXTENSIONS = new HashMap<>();

    /**
     * Use this to register a menu extension type, this will then be an available option to choose from in the server config,
     * allowing you to pick which menu extension to use.
     * <p>
     * You must also register a corresponding screen extension with {@link AccessoryScreenExtensions#register(Identifier, AccessoryScreenExtension.Factory)}
     * @param id identifier corresponding to the type in the server config
     * @param factory factory for the extension, will be constructed later
     */
    public static void register(Identifier id, AccessoryMenuExtension.Factory factory) {
        if (!MENU_EXTENSIONS.containsKey(id)) {
            MENU_EXTENSIONS.put(id, factory);
        }
    }

    /**
     * Check if a menu extension with the given {@link Identifier} has been registered
     * @param id first parameter passed to {@link #register(Identifier, AccessoryMenuExtension.Factory)} to search for
     * @return {@code true} if a menu extension with the given {@link Identifier} exists, {@code false} otherwise
     */
    public static boolean exists(Identifier id) {
        return MENU_EXTENSIONS.containsKey(id);
    }

    public static Set<Identifier> getKeys() {
        return MENU_EXTENSIONS.keySet();
    }

    /**
     * Retrieve the active menu extension factory by parsing the server config value to an {@link Identifier}
     * @return the currently in-use menu extension factory
     */
    public static AccessoryMenuExtension.@Nullable Factory getActiveFactory() {
        return MENU_EXTENSIONS.get(Identifier.tryParse(OhmegaConfig.Server.menuExtensionId()));
    }

    /**
     * Sets the accessory extension to the target menu
     * @param menu parent menu
     * @param owner player which this menu belongs
     */
    public static void setExtension(AbstractContainerMenu menu, Player owner) {
        if (menu instanceof IAccessoryMenu accessoryMenu) {
            AccessoryMenuExtension.Factory factory = getActiveFactory();

            if (factory != null) {
                accessoryMenu.setAccessoryExtension(factory.construct(menu, owner));
            }
        } else {
            throw new IllegalArgumentException("Menu " + menu + " does not implement " + IAccessoryMenu.class);
        }
    }

    /**
     * Get the accessory slots associated with this accessory extension menu
     * @param menu parent menu
     * @param owner player which this menu belongs
     * @return the slots to be added with the accessory extension
     */
    public static List<AccessorySlot> getAccessorySlots(AbstractContainerMenu menu, Player owner) {
        if (menu instanceof IAccessoryMenu accessoryMenu) {
            AccessoryMenuExtension extension = accessoryMenu.getAccessoryExtension();

            if (extension != null) {
                ImmutableList<AccessoryType> types = AccessoryHelper.getSlotTypes();
                int requiredCount = types.size();
                List<AccessorySlot> addedSlots = new ArrayList<>(requiredCount);

                if (owner.level().isClientSide()) {
                    extension.addSlotsClient((index, x, y) -> addedSlots.add(new AccessorySlot(
                            owner,
                            index,
                            x + accessoryMenu.getAccessoryExtensionX(),
                            y + accessoryMenu.getAccessoryExtensionY(),
                            types.get(index))));

                    int actualCount = addedSlots.size();

                    if (actualCount != requiredCount) {
                        throw new IllegalStateException("Slots added by extension '" + extension + "' (" + actualCount + ") differ in length from expected " + requiredCount);
                    }
                } else {
                    for (int i = 0; i < requiredCount; i++) {
                        addedSlots.add(new AccessorySlot(owner, i, 0, 0, types.get(i)));
                    }
                }

                return addedSlots;
            }
        } else {
            throw new IllegalArgumentException("Menu " + menu + " does not implement " + IAccessoryMenu.class);
        }

        return List.of();
    }

    /**
     * This must be called at the end of your target menu's constructor to assign the accessory extension and add slots.
     * Technically, you can instead call {@link #setExtension(AbstractContainerMenu, Player)} independently,
     * but only do this if you have good reason and know what you are doing
     * <p>
     * Ohmega calls this in another place internally, do not replicate -- it is to fix an annoying edge case
     * @param menu parent menu
     * @param owner player which this menu belongs
     */
    public static void onConstruct(AbstractContainerMenu menu, Player owner) {
        setExtension(menu, owner);

        for (AccessorySlot slot : getAccessorySlots(menu, owner)) {
            menu.addSlot(slot);
        }
    }

    /**
     * This should be the return value of your target menu's {@link AbstractContainerMenu#quickMoveStack(Player, int)}
     * if it does not return {@code null}. In such a case, you should use a fallback method.
     * See {@link InventoryMenu#quickMoveStack(Player, int)} for an example of a fallback
     * @param menu supplied by caller {@code this} instance usually: menu we are moving a stack in
     * @param player supplied by method override: player which this menu belongs
     * @param index supplied by method override: slot index we are moving from
     * @return {@link ItemStack} after moving, or {@code null} on failing, at which point you should rely on a fallback
     */
    public static @Nullable ItemStack onQuickMoveStack(AbstractContainerMenu menu, Player player, int index) {
        if (menu instanceof IAccessoryMenu accessoryMenu) {
            AccessoryMenuExtension extension = accessoryMenu.getAccessoryExtension();

            if (extension != null) {
                return extension.quickMoveStack(menu, player, index, accessoryMenu.isAccessoryExtensionVisible());
            }
        } else {
            throw new IllegalArgumentException("Menu " + menu + " does not implement " + IAccessoryMenu.class);
        }

        return null;
    }
}
