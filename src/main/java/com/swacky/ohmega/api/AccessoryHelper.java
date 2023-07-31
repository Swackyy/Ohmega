package com.swacky.ohmega.api;

import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.ModBinds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
     * A utility method to get a description for a key-bind activated accessory.
     * @param bindDescription what will be displayed for the bind to do / activate. Use "&lt;BIND&gt;" to show the bind letter
     * @param inSlot the accessory slot that the item is in
     * @param other the "other" argument, for displaying when the key-bind cannot be retrieved as the accessory is not in an accessory slot
     * @return
     * If the slot is a normal category slot: An empty component
     * Otherwise, an example: "Press B to activate invisibility" When "Press %1$s to activate invisibility" is provided and a slot with key-binding of key B
     */
    public static TextComponent getBindTooltip(Component bindDescription, int inSlot, Component other) {
        return inSlot == -1 ? (TextComponent) new TextComponent(other.getString()).withStyle(ChatFormatting.GRAY) : (TextComponent) new TextComponent(bindDescription.getString().replace("<BIND>", (inSlot == 3 ? ModBinds.UTILITY_0.getTranslatedKeyMessage() : inSlot == 4 ? ModBinds.UTILITY_1.getTranslatedKeyMessage() : ModBinds.SPECIAL.getTranslatedKeyMessage()).getString().toUpperCase())).withStyle(ChatFormatting.GRAY);
    }

    public static TextComponent getBindTooltip(Component bindDescription, ItemStack stack, Component other) {
        return getBindTooltip(bindDescription, getSlot(stack), other);
    }

    /**
     * A utility method to get the type of accessory in a component
     * @param accessory the accessory to get the type from
     * @return a TextComponent instance of "Accessory type: TYPE"
     */
    public static TextComponent getTypeTooltip(IAccessory accessory) {
        return (TextComponent) new TextComponent("Accessory type: " + accessory.getType().getTranslation().getString()).withStyle(ChatFormatting.DARK_GRAY);
    }

    /**
     * Gets the item's slot in the player's inventory from an ItemStack's tag
     * @param stack the itemstack to test against
     * @return the slot of the item if the tag is present, -1 otherwise
     * -1 is returned if not present because otherwise it would return 0, the first slot, which messes things up. Using -1 makes it an outlier
     */
    public static int getSlot(ItemStack stack) {
        return stack.getOrCreateTag().contains("slot") ? stack.getOrCreateTag().getInt("slot") : -1;
    }

    /**
     * Adds a tag to show the active state of an accessory item
     * @param stack the ItemStack to add the tag to
     * @param value the value of the active state
     * This is handled internally but for whatever reason you want to change the value of this, you can
     */
    public static void addActiveTag(ItemStack stack, boolean value) {
        if(stack.getItem() instanceof IAccessory) {
            stack.getOrCreateTag().putBoolean("active", value);
        }
    }

    /**
     * Checks the active state of an accessory item by tag
     * @param stack the ItemStack to check the active state of
     * @return true if active, false if inactive or not an accessory item
     */
    public static boolean isTagActive(ItemStack stack) {
        if(stack.getItem() instanceof IAccessory) {
            return stack.getOrCreateTag().getBoolean("active");
        }
        return false;
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
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> a.setActive(slot, false));
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
    @SuppressWarnings("unchecked")
    public static InteractionResultHolder<ItemStack> tryEquip(Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack>[] ret = new InteractionResultHolder[]{InteractionResultHolder.pass(player.getItemInHand(hand))};
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof IAccessory acc) {
                int slot = getFirstOpenSlot(player, acc.getType());
                if (slot != -1) {
                    ItemStack stack0 = stack.copy();
                    stack0.setCount(1);

                    if(a.trySetStackInSlot(slot, stack0)) {
                        stack.shrink(1);
                        stack0.getOrCreateTag().putInt("slot", slot);
                        acc.onEquip(player, stack0);
                        if(stack0.getEquipSound() != null) {
                            player.playSound(stack0.getEquipSound(), 1.0F, 1.0F);
                        }
                        ret[0] = InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), player.level.isClientSide);
                    }
                }
            }
        });
        return ret[0];
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
                            return;
                        }
                    }
                }
                case UTILITY -> {
                    for (int i = 3; i < 5; i++) {
                        if(a.getStackInSlot(i).isEmpty()) {
                            out[0] = i;
                            return;
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

    public static boolean isExclusiveType(Player player, ItemStack stack) {
        final boolean[] ret = new boolean[1];
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
            if(stack.getItem() instanceof IAccessory) {
                for(int i = 0; i < 6; i++) {
                    if(a.getStackInSlot(i).is(stack.getItem())) {
                        return;
                    }
                }
                ret[0] = true;
            }
        });
        return ret[0];
    }
}