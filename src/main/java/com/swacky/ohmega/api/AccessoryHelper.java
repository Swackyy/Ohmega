package com.swacky.ohmega.api;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.ModBinds;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AccessoryHelper {
    /**
     * Checks if a player has an accessory
     * @param player the player to check for
     * @param accessory the accessory to check if the player has it
     * @return true if the player has the passed accessory, false otherwise
     */
    public static <T extends Item & IAccessory> boolean hasAccessory(ServerPlayer player, T accessory) {
        boolean[] out = new boolean[1];
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
            for(int i = 0; i < a.getSlots(); i++) {
                if(a.getStackInSlot(i).getItem() == accessory) {
                    out[0] = true;
                }
            }
        });
        return out[0];
    }

    /**
     * Checks if a player has an accessory and allows for the calling of a function upon it
     * @param player the player to check for
     * @param accessory the accessory to check if the player has it
     * @param holder a holder class for a functional interface, allows users to run methods if the accessory is present
     * @return true if the player has the passed accessory, false otherwise
     */
    public static <T extends Item & IAccessory> boolean runIfHasAccessory(ServerPlayer player, T accessory, Inner holder) {
        if(hasAccessory(player, accessory)) {
            holder.apply(player);
            return true;
        }
        return false;
    }

    /**
     * Checks if a player has an accessory, if true, the accessory will update
     * @param player the player to check for
     * @param accessory the accessory to check if the player has it
     * @return true if the player has the passed accessory, false otherwise
     */
    public static <T extends Item & IAccessory> boolean updateIfHasAccessory(ServerPlayer player, T accessory) {
        return runIfHasAccessory(player, accessory, accessory::update);
    }

    /**
     * An inner class allowing functions to be called with a Player argument
     */
    @FunctionalInterface
    interface Inner {
        void apply(ServerPlayer player);
    }

    /**
     * A utility method to get a description for a key bind activated accessory.
     * @param bindDescription what will be displayed for the bind to do / activate
     * @param inSlot the accessory slot that the item is in.
     * @return
     * If the slot is a normal category slot: An empty component
     * Otherwise, an example: "Press B to activate invisibility" When "Press %1s to activate invisibility" is provided and a slot with keybinding of key B
     */
    public static TextComponent getBindTooltip(String bindDescription, int inSlot) {
        return new TextComponent(bindDescription.replace("%s", (inSlot == 3 ? ModBinds.UTILITY_0.getTranslatedKeyMessage() : inSlot == 4 ? ModBinds.UTILITY_1.getTranslatedKeyMessage() : ModBinds.SPECIAL.getTranslatedKeyMessage()).getString().toUpperCase()));
    }

    /**
     * Same as the method above, just gets the slot through a stack tag which is stored upon equipping/de-quipping
     */
    public static TextComponent getBindTooltip(String bindDescription, ItemStack stack) {
        if (stack.getTag() != null) {
            return getBindTooltip(bindDescription, stack.getTag().getInt("slot"));
        }
        return new TextComponent(bindDescription.replace("%s","TAG_ERR"));
    }

    /**
     * Gets the item's slot in the player's inventory from an ItemStack's tag
     * @param stack the itemstack to test against
     * @return the slot of the item if the tag is present, -1 otherwise
     */
    public static int getSlot(ItemStack stack) {
        if(stack.getTag() != null) {
            return stack.getTag().getInt("slot");
        }
        return -1;
    }

    /**
     * Activates an accessory in a slot
     * @param player the player to activate an accessory on
     * @param slot the slot, must range from 3-5
     */
    public static void activate(ServerPlayer player, int slot) {
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> a.setActive(slot));
    }

    public static void activate(ServerPlayer player, ItemStack stack) {
        activate(player, getSlot(stack));
    }

    /**
     * Deactivates an accessory in a slot
     * @param player the player to deactivate an accessory on
     * @param slot the slot, must range from 3-5
     */
    public static void deactivate(ServerPlayer player, int slot) {
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> a.setActive(slot));
    }

    public static void deactivate(ServerPlayer player, ItemStack stack) {
        deactivate(player, getSlot(stack));
    }

    /**
     * Toggles an accessory in a slot
     * @param player the player to toggle an accessory on
     * @param slot the slot, must range from 3-5
     */
    public static void toggle(ServerPlayer player, int slot) {
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> a.toggle(slot));
    }

    public static void toggle(ServerPlayer player, ItemStack stack) {
        toggle(player, getSlot(stack));
    }

    /**
     * Checks if an accessory is active
     * IMPORTANT -- Testing the active state can be performed on the client, but changing the state cannot for security reasons
     * @param player the player to deactivate an accessory on
     * @param slot the slot, must range from 3-5
     * @return true if the provided accessory is active, false if out of range or inactive
     */
    public static boolean isActive(Player player, int slot) {
        boolean[] ret = new boolean[1];
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> ret[0] = a.isActive(slot));
        return ret[0];
    }

    public static boolean isActive(Player player, ItemStack stack) {
        return isActive(player, getSlot(stack));
    }

    /**
     * A utility method to allow for the equipping of accessories by a right-click
     * @param player the player to put the accessory on
     * @param hand the hand to get the accessory held
     * @return an interaction result success if the item is equipped, else a pass
     */
    public static InteractionResultHolder<ItemStack> tryEquip(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResultHolder<ItemStack>[] out = new InteractionResultHolder[]{InteractionResultHolder.pass(stack)};
        if(stack.getItem() instanceof IAccessory acc) {
            player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                int slot = getFirstOpenSlot(player, acc.getType());
                if(slot != -1) {
                    a.insertItem(slot, stack, false);
                    stack.shrink(1);
                }
            });
        }
        return out[0];
    }

    /**
     * Returns the first open accessory slot for a type
     * @param player the player to test their slots against
     * @param type the accessory type to check for the slots
     * @return the first open slot of the type, else if none is found then -1
     */
    public static int getFirstOpenSlot(Player player, AccessoryType type) {
        int[] out = {-1};
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
            switch (type) {
                case NORMAL -> {
                    for (int i = 0; i < 3; i++) {
                        if(a.getStackInSlot(i).isEmpty()) {
                            out[0] = i;
                        }
                    }
                }
                case UTILITY -> {
                    for (int i = 3; i < 5; i++) {
                        if(a.getStackInSlot(i).isEmpty()) {
                            out[0] = i;
                        }
                    }
                }
                case SPECIAL -> {
                    if(a.getStackInSlot(5).isEmpty()) {
                        out[0] = 5;
                    }
                }
            }
        });
        return out[0];
    }
}