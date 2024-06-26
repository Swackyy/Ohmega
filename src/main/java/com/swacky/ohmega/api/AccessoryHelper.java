package com.swacky.ohmega.api;

import com.swacky.ohmega.api.events.AccessoryEquipEvent;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.ModBinds;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class AccessoryHelper {
    // - - - INTERNAL USE START - - - //
    // Internal symbols are denoted by an underscore prefix
    // You can use these; however, it is discouraged

    public static final String _TAG_KEY = "OhmegaInternal";

    public static CompoundTag _internalTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(_TAG_KEY)) {
            return tag.getCompound(_TAG_KEY);
        }
        tag.put(_TAG_KEY, new CompoundTag());
        return tag.getCompound(_TAG_KEY);
    }

    // - - - INTERNAL USE END - - - //

    /**
     * Finds the slot that a given {@link IAccessory} is found
     * @param player the {@link Player} to check for
     * @param accessory the {@link IAccessory} to check if the player has it
     * @return the slot that the passed {@link IAccessory} was found. If not found, returns -1
     */
    public static <T extends Item & IAccessory> int getSlotFor(Player player, T accessory) {
        int[] out = {-1};
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
            for(int i = 0; i < a.getSlots(); i++) {
                if(a.getStackInSlot(i).getItem() == accessory) {
                    out[0] = i;
                }
            }
        });
        return out[0];
    }

    /**
     * Checks if a player has an accessory and allows for the calling of a function upon it
     * @param player the {@link Player} to check for
     * @param accessory the {@link Player} to check if the player has it
     * @param holder a holder class for a functional interface, allows users to run methods if the accessory is present
     * @return true if the {@link Player} has the passed accessory, false otherwise
     */
    public static <T extends Item & IAccessory> boolean runIfPresent(Player player, T accessory, Inner holder) {
        int slot = getSlotFor(player, accessory);
        if(slot != -1) {
            holder.apply(player, getStackInSlot(player, slot));
            return true;
        }
        return false;
    }

    /**
     * Checks if a player has an accessory, if true, the accessory will update
     * @param player the player to check for
     * @param accessory the {@link IAccessory} to check if the player has it
     * @return true if the player has the passed accessory, false otherwise
     */
    public static <T extends Item & IAccessory> boolean updateIfPresent(Player player, T accessory) {
        return runIfPresent(player, accessory, accessory::update);
    }

    /**
     * An inner class allowing functions to be called with a {@link Player} and {@link ItemStack} argument
     */
    @FunctionalInterface
    public interface Inner {
        void apply(Player player, ItemStack stack);
    }

    /**
     * A utility method to get a description for a key-bind activated {@link IAccessory}.
     * @param bindDescription what will be displayed for the bind to do / activate. Use "&lt;BIND&gt;" to show the bind letter
     * @param inSlot the accessory slot that the item is in
     * @param other the "other" tooltip, for displaying when {@link IAccessory} is not in an accessory slot (such as in the normal inventory)
     * @return if the slot is a normal category slot: The "other" {@link Component}
     * <p>
     * Otherwise, an example: "Press B to activate invisibility" When "Press &lt;BIND&gt; to activate invisibility" is provided and a slot with key-binding of key B
     */
    public static MutableComponent getBindTooltip(ComponentContents bindDescription, int inSlot, ComponentContents other) {
        return inSlot == -1 ? MutableComponent.create(other).withStyle(ChatFormatting.GRAY) : MutableComponent.create(new LiteralContents(MutableComponent.create(bindDescription).getString().replace("<BIND>", (inSlot == 3 ? ModBinds.UTILITY_0.getTranslatedKeyMessage().getString().toUpperCase() : inSlot == 4 ? ModBinds.UTILITY_1.getTranslatedKeyMessage().getString().toUpperCase() : ModBinds.SPECIAL.getTranslatedKeyMessage().getString().toUpperCase())))).withStyle(ChatFormatting.GRAY);
    }

    /**
     * A shortcut method to the one defined above, see description there.
     */
    public static MutableComponent getBindTooltip(ComponentContents bindDescription, ItemStack stack, ComponentContents other) {
        return getBindTooltip(bindDescription, getSlot(stack), other);
    }

    /**
     * A utility method to get the {@link AccessoryType} of {@link IAccessory} in a component
     * @param accessory the {@link IAccessory} to get the type from
     * @return a {@link MutableComponent} instance of "Accessory type: TYPE"
     */
    public static MutableComponent getTypeTooltip(IAccessory accessory) {
        return MutableComponent.create(new TranslatableContents("accessory.type", null, new Object[]{MutableComponent.create(accessory.getType().getTranslation()).getString()})).withStyle(ChatFormatting.DARK_GRAY);
    }

    /**
     * Gets the item's slot in the player's inventory from an {@link ItemStack}'s tag
     * @param stack the {@link ItemStack} to test against
     * @return the slot of the item if the tag is present, -1 otherwise
     * -1 is returned if not present because otherwise it would return 0, the first slot, which messes things up. Using -1 makes it an outlier
     */
    public static int getSlot(ItemStack stack) {
        return _internalTag(stack).contains("slot") ? _internalTag(stack).getInt("slot") : -1;
    }

    /**
     * Adds a tag to show the active state of an {@link IAccessory} item
     * @param player used to add/remove active only attribute modifiers to
     * @param stack the {@link IAccessory} to add the tag to
     * @param value the value of the active state
     * This is handled internally but for whatever reason you want to change the value of this, you can
     */
    public static void setActive(Player player, ItemStack stack, boolean value) {
        if(stack.getItem() instanceof IAccessory) {
            _internalTag(stack).putBoolean("active", value);
        }

        IAccessory.ModifierBuilder builder = IAccessory.ModifierBuilder.deserialize(stack);
        if(value) {
            player.getAttributes().addTransientAttributeModifiers(builder.getModifiersActiveOnly());
        } else {
            player.getAttributes().removeAttributeModifiers(builder.getModifiersActiveOnly());
        }
    }

    /**
     * Checks the active state of an {@link IAccessory} item by tag
     * @param stack the {@link ItemStack} to check the active state of
     * @return true if active, false if inactive or not an {{@link IAccessory} item
     */
    public static boolean isActive(ItemStack stack) {
        if(stack.getItem() instanceof IAccessory) {
            return _internalTag(stack).getBoolean("active");
        }
        return false;
    }

    /**
     * Activates an {@link IAccessory} item in a slot
     * @param player used to add/remove active only attribute modifiers to
     * @param stack the {@link ItemStack} to activate an accessory on
     */
    public static void activate(Player player, ItemStack stack) {
        setActive(player, stack, true);
    }

    /**
     * Deactivates an {@link IAccessory} item in a slot
     * @param player used to add/remove active only attribute modifiers to
     * @param stack the {@link ItemStack} to deactivate an accessory on
     */
    public static void deactivate(Player player, ItemStack stack) {
        setActive(player, stack, false);
    }

    /**
     * Toggles an {@link IAccessory}'s active state
     * @param player used to add/remove active only attribute modifiers to
     * @param stack the {@link ItemStack} to toggle an accessory's active state
     */
    public static void toggle(Player player, ItemStack stack) {
        if(isActive(stack)) {
            deactivate(player, stack);
        } else {
            activate(player, stack);
        }
    }

    /**
     * A utility method to allow for the equipping of accessories by a right-click
     * @param player the {@link Player} to put the {@link IAccessory} on
     * @param hand the {@link InteractionHand} to get the accessory held
     * @return an interaction result of success if the item is equipped, else a pass
     */
    @SuppressWarnings("unchecked")
    public static InteractionResultHolder<ItemStack> tryEquip(Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack>[] out = new InteractionResultHolder[]{InteractionResultHolder.pass(player.getItemInHand(hand))};
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof IAccessory acc) {
                int slot = getFirstOpenSlot(player, acc.getType());
                if (slot != -1) {
                    ItemStack stack0 = stack.copy();
                    stack0.setCount(1);

                    if(a.trySetStackInSlot(slot, stack0)) {
                        IAccessory.ModifierBuilder builder = IAccessory.ModifierBuilder.deserialize(stack);
                        player.getAttributes().addTransientAttributeModifiers(builder.getModifiers());

                        stack.shrink(1);
                        _internalTag(stack0).putInt("slot", slot);

                        AccessoryEquipEvent event = OhmegaHooks.accessoryEquipEvent(player, stack0);
                        if(!event.isCanceled()) {
                            acc.onEquip(player, stack0);
                        }
                        if(acc.getEquipSound() != null) {
                            player.playSound(acc.getEquipSound(), 1.0F, 1.0F);
                        }
                        out[0] = InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), player.level.isClientSide);
                    }
                }
            }
        });
        return out[0];
    }

    /**
     * Returns the first open accessory slot for a type
     * @param player the {@link Player} to test their slots against
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

    /**
     * Returns all the stacks from the player's accessory inventory
     * @param player the {@link Player} to get the stacks from
     * @return an {@link ArrayList} of all accessory slot stacks
     */
    public static ArrayList<ItemStack> getStacks(Player player) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a ->{
            for(int i = 0; i < 6; i++) {
                stacks.add(a.getStackInSlot(i));
            }
        });
        return stacks;
    }

    /**
     * Returns all the accessories from the player's accessory inventory
     * @param player the {@link Player} to get the stacks from
     * @return an {@link ArrayList} of all accessory slot accessories
     */
    public static ArrayList<IAccessory> getAccessories(Player player) {
        ArrayList<IAccessory> accessories = new ArrayList<>();
        for(ItemStack stack : getStacks(player)) {
            if(stack.getItem() instanceof IAccessory accessory) {
                accessories.add(accessory);
            }
        }
        return accessories;
    }

    /**
     * Returns the {@link ItemStack} corresponding to the slot index of the accessory inventory
     * @param player the {@link Player} to get the stacks from
     * @param slot the index of slot, 5 would be the special slot as an example
     * @return the stack in the slot provided
     */
    public static ItemStack getStackInSlot(Player player, int slot) {
        ItemStack[] stack = new ItemStack[1];
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> stack[0] = a.getStackInSlot(slot));
        return stack[0];
    }

    /**
     * Checks whether an {@link IAccessory} is exclusive, or in other words is the only instance of that {@link IAccessory} equipped
     * @param player the {@link Player} to test against
     * @param stack the stack of the {@link IAccessory} item
     * @return true if it is an exclusive type, false if not or not of the {@link IAccessory} type
     */
    public static boolean isExclusiveType(Player player, ItemStack stack) {
        for(ItemStack stack0 : getStacks(player)) {
            if(stack0.is(stack.getItem())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks compatibility of the {@link IAccessory} provided with all other equipped accessories
     * @param player the {@link Player} to get the accessory inventory of
     * @param accessory the {@link IAccessory} to test against
     * @return true if compatible, false if not
     */
    public static boolean compatibleWith(Player player, IAccessory accessory) {
        boolean[] out = {true};
        for(IAccessory accessory0 : getAccessories(player)) {
            if(!accessory0.isCompatibleWith(accessory)) {
                out[0] = false;
            }
        }
        return out[0];
    }

    /**
     * Converts {@link ItemStack} to {@link IAccessory}
     * <p>
     * <strong>IMPORTANT: CHECK FOR NULL CASE</strong>
     * @param stack the {@link ItemStack} to get the accessory of
     * @return {@link IAccessory} instance of the provided {@link ItemStack} if found, else null
     */
    public static IAccessory getFromStack(ItemStack stack) {
        if(stack.getItem() instanceof IAccessory a) {
            return a;
        }

        return null;
    }
}