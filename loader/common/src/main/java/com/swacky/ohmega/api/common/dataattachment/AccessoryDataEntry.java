package com.swacky.ohmega.api.common.dataattachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.item.IAccessory;
import com.swacky.ohmega.api.common.item.SoundData;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A data holder for {@link AccessoryData} to ensure sizes remain uniform
 * @apiNote Holding a reference to this is not recommended, they may be reconstructed when needed and your references will be invalidated
 */
public final class AccessoryDataEntry {
    public static final @NonNull Codec<AccessoryDataEntry> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AccessoryType.CODEC.fieldOf("type").forGetter(AccessoryDataEntry::getType),
            ItemStack.OPTIONAL_CODEC.fieldOf("stack").forGetter(AccessoryDataEntry::getStack),
            Codec.BOOL.fieldOf("hidden").forGetter(AccessoryDataEntry::isHidden)
    ).apply(builder, AccessoryDataEntry::new));

    public static final @NonNull Codec<ArrayList<AccessoryDataEntry>> ARRAY_LIST_CODEC = CODEC.listOf().xmap(ArrayList::new, Function.identity());

    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, AccessoryDataEntry> STREAM_CODEC = StreamCodec.composite(
            AccessoryType.STREAM_CODEC, AccessoryDataEntry::getType,
            ItemStack.OPTIONAL_STREAM_CODEC, AccessoryDataEntry::getStack,
            ByteBufCodecs.BOOL, AccessoryDataEntry::isHidden,
            AccessoryDataEntry::new);

    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, ArrayList<AccessoryDataEntry>> ARRAY_LIST_STREAM_CODEC = ByteBufCodecs.collection(
            ArrayList::new,
            STREAM_CODEC);

    private @NonNull AccessoryType type;
    private @NonNull ItemStack stack;
    private boolean hidden;

    /**
     * Private constructor used in codec deserialisation
     * @param type the {@link AccessoryType} of the slot corresponding to this instance
     * @param stack held {@link ItemStack}
     * @param hidden {@code true} if the accessory equipped in this entry should not be rendered
     */
    private AccessoryDataEntry(@NonNull AccessoryType type, @NonNull ItemStack stack, boolean hidden) {
        this.type = type;
        this.stack = stack;
        this.hidden = hidden;
    }

    /**
     * Public constructor used for adding to {@link AccessoryData}.
     * Initialises {@link #stack} with {@link ItemStack#EMPTY} and {@link #hidden} with {@code false}
     * @param type the {@link AccessoryType} of the slot corresponding to this instance
     */
    public AccessoryDataEntry(@NonNull AccessoryType type) {
        this(type, ItemStack.EMPTY, false);
    }

    /**
     * Retrieve the {@link AccessoryType} of this entry, corresponding to the accessory slot type
     * @return this entry's {@link AccessoryType}
     */
    public @NonNull AccessoryType getType() {
        return type;
    }

    // todo: allow forwards simulation for removal of a type
    /**
     * Predicate function to assert validity of an {@link ItemStack} candidate to place it in a slot
     * @param entity the entity attempting to equip this item / this call is relevant towards
     * @param stack the {@link ItemStack} to check validity for
     * @param context the context of this call
     * @return {@code true} if valid, {@code false} if invalid
     */
    public boolean isItemValid(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull AccessoryType type, @NonNull EquipContext context) {
        if (stack.isEmpty()) {
            return true;
        }

        Item item = stack.getItem();
        Accessory accessory = Accessories.get(item);

        if (accessory != null && (AccessoryHelper.compatibleWith(entity, stack) || ItemStack.isSameItem(this.stack, stack))) {
            return Accessories.getType(entity, item).equals(type) && accessory.canEquip(entity, stack, context);
        }

        return false;
    }

    /**
     * Predicate function to assert validity of an {@link ItemStack} candidate to place it in a slot
     * @param entity the entity attempting to equip this item / this call is relevant towards
     * @param stack the {@link ItemStack} to check validity for
     * @param context the context of this call
     * @return {@code true} if valid, {@code false} if invalid
     */
    public boolean isItemValid(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
        return isItemValid(entity, stack, type, context);
    }

    /**
     * Attempts to move the {@link ItemStack} in the given index and return it to the entity's inventory if they are a player.
     * If the entity is not a player or the player's inventory could not add the item, it will drop in front of them
     * @param entity the entity that this entry belongs to
     * @param context the context surrounding this invocation
     */
    public void moveOrDropStack(@NonNull LivingEntity entity, @NonNull EquipContext context) {
        if (!stack.isEmpty()) {
            doUnequip(entity, stack, context);

            if (!(entity instanceof Player player) || !player.addItem(stack)) {
                entity.drop(stack, false, true);
            }
        }
    }

    /**
     * Sets this entry's {@link AccessoryType} to the given value and performs some extra operations to
     * @param entity the entity that this entry belongs to
     * @param type the {@link AccessoryType} which this entry will be set to
     * @param context the context surrounding this invocation
     */
    public void setType(@NonNull LivingEntity entity, @NonNull AccessoryType type, @NonNull EquipContext context) {
        if (this.type != type && !isItemValid(entity, stack, type, context)) {
            moveOrDropStack(entity, context);
        }

        this.type = type;
    }

    /**
     * Retrieve the stored {@link ItemStack} in this entry
     * @return the stored {@link ItemStack}
     */
    public @NonNull ItemStack getStack() {
        return stack;
    }

    /**
     * Checks if the accessory held within this entry should be rendered, may be overridden as false by a server config value
     * @return {@code true} if rendering should be prevented for this entry, {@code false} to allow it to happen
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets the visibility of the accessory stored within this entry
     * @value should be {@code true} if rendering should be prevented for this entry, {@code false} to allow it to happen
     */
    public void setHidden(boolean value) {
        hidden = value;
    }

    /**
     * Toggle this entry's hidden state
     * @param entity the entity that this entry belongs to
     * @param index the slot index of this data entry
     */
    public void toggleHidden(@NonNull LivingEntity entity, int index) {
        if (OhmegaConfig.Server.getData().allowHideAccessories().get()) {
            hidden = !hidden;

            if (entity.level().isClientSide()) {
                OhmegaNetworking.C2S.send(new SetHiddenPacket(index, hidden));
            }
        }
    }

    /**
     * Perform necessary operations that occur when un-equipping an accessory
     * @param entity the entity un-equipping the accessory
     * @param stack the accessory's {@link ItemStack} representation being un-equipped
     * @param context the context surrounding this invocation
     */
    public static void doUnequip(@NonNull LivingEntity entity, @NonNull ItemStack stack, @NonNull EquipContext context) {
        Accessory accessory = Accessories.get(stack.getItem());

        if (accessory != null) {
            AccessoryHelper.setNoSlot(stack);
            AccessoryHelper.changeModifiers(entity, stack.get(DataComponents.ATTRIBUTE_MODIFIERS), false);
            accessory.onUnequip(entity, stack, context);
        }
    }

    /**
     * Perform necessary operations that occur when un-equipping an accessory
     * @param entity the entity un-equipping the accessory
     * @param stack the accessory's {@link ItemStack} representation being un-equipped
     * @param index the slot index of this data entry
     * @param context the context surrounding this invocation
     */
    public void doEquip(@NonNull LivingEntity entity, @NonNull ItemStack stack, int index, @NonNull EquipContext context) {
        Accessory accessory = Accessories.get(stack.getItem());

        if (accessory != null) {
            AccessoryHelper.setSlot(stack, index);
            AccessoryHelper.changeModifiers(entity, type.getAttributeModifiers().getPassive(), true);
            AccessoryHelper.changeModifiers(entity, stack.get(DataComponents.ATTRIBUTE_MODIFIERS), true);
            accessory.onEquip(entity, stack, context);

            if (context == EquipContext.USE_HELD) {
                SoundData data = accessory.getEquipSound(stack);

                if (data != null) {
                    entity.playSound(data.sound().value(), data.volume(), data.pitch());
                }
            }
        }
    }

    /**
     * Synchronise the requested data stored on the server with this instance with all clients
     * @param entity the entity that this entry belongs to
     * @param index the slot index of this data entry
     * @param stack the matching {@link ItemStack} to synchronise as, corresponding to the {@code index}
     */
    public void trySendSync(@NonNull LivingEntity entity, int index, @NonNull ItemStack stack) {
        if (entity.level() instanceof ServerLevel level) {
            for (ServerPlayer receiver : level.players()) {
                OhmegaNetworking.S2C.send(receiver, new SyncStacksPacket(entity.getId(), new int[]{index}, List.of(stack), true));
            }
        }
    }

    /**
     * Perform the actual stack setting and other related operations
     * @param entity the entity that this entry belongs to
     * @param stack the {@link ItemStack} to set as in this entry
     * @param index the slot index of this data entry
     * @param context the context surrounding this invocation
     * @param forceOnEquip {@code true} if {@link IAccessory#onEquip(LivingEntity, ItemStack, EquipContext)} should be force-called, {@code false} otherwise
     * @param sync {@code true} if this invocation should be synced with clients
     */
    private void doSetStack(@NonNull LivingEntity entity, @NonNull ItemStack stack, int index, @NonNull EquipContext context, boolean forceOnEquip, boolean sync) {
        if (!ItemStack.matches(this.stack, stack)) {
            doUnequip(entity, this.stack, context);

            if (stack.isEmpty()) {
                AccessoryHelper.changeModifiers(entity, type.getAttributeModifiers().getPassive(), false);
            }

            this.stack = stack;

            if (forceOnEquip || AccessoryHelper.isActive(stack)) {
                doEquip(entity, stack, index, context);
            }

            if (sync) {
                trySendSync(entity, index, stack);
            }
        }
    }

    /**
     * Set the {@link ItemStack} in a certain index
     * @param entity the entity that this entry belongs to
     * @param stack the {@link ItemStack} to set as in the provided slot index
     * @param index the slot index of this data entry
     * @param context the context surrounding this invocation
     * @param bypassValidation {@code true} will not check {@link #isItemValid(LivingEntity, ItemStack, EquipContext)} before setting
     * @param forceOnEquip {@code true} if {@link IAccessory#onEquip(LivingEntity, ItemStack, EquipContext)} should be force-called, {@code false} otherwise
     * @return {@code true} if successful ({@link #isItemValid(LivingEntity, ItemStack, EquipContext)}
     * result if {@code bypassValidation} is {@code false}
     */
    // todo fix: Syncing with this is bugged as it will always call Accessory#onEquip afaik
    // todo update: confirmed, this happens because forceOnEquip = true from AccessorySlot#set, but its effects are only visible on Forge for some reason
    public boolean setStack(@NonNull LivingEntity entity, @NonNull ItemStack stack, int index, @NonNull EquipContext context, boolean bypassValidation, boolean forceOnEquip) {
        if (bypassValidation || isItemValid(entity, stack, context)) {
            doSetStack(entity, stack, index, context, forceOnEquip, true);
            return true;
        }

        return false;
    }

    /**
     * Set the {@link ItemStack} in a certain index.
     * This is most likely the overload you want to use
     * @param entity the entity that this entry belongs to
     * @param stack the {@link ItemStack} to set as in the provided slot index
     * @param index the slot index of this data entry
     * @param context the context surrounding this invocation
     * @return the result of {@link #isItemValid(LivingEntity, ItemStack, EquipContext)}
     */
    public boolean setStack(@NonNull LivingEntity entity, @NonNull ItemStack stack, int index, @NonNull EquipContext context) {
        return setStack(entity, stack, index, context, false, true);
    }

    /**
     * Remove the {@link ItemStack} from the provided index, returning the removed stack
     * @param entity the entity that this entry belongs to
     * @param amount the amount to remove
     * @param context the context surrounding this invocation
     * @return the removed {@link ItemStack}
     */
    public ItemStack remove(@NonNull LivingEntity entity, int amount, @NonNull EquipContext context) {
        ItemStack stack;

        if (amount < 0) {
            stack = this.stack;
            this.stack = ItemStack.EMPTY;
        } else {
            stack = this.stack.split(amount);
        }

        if (!ItemStack.isSameItemSameComponents(this.stack, stack)) {
            doUnequip(entity, stack, context);
            AccessoryHelper.changeModifiers(entity, type.getAttributeModifiers().getPassive(), false);
        }

        return stack;
    }

    /**
     * Performs a deep copy of this entry, allowing it to be mutated without affecting the original instance
     * @return a deep copy of all data stored within this entry, safe to mutate
     */
    public AccessoryDataEntry copy() {
        return new AccessoryDataEntry(type, stack.copy(), hidden);
    }
}
