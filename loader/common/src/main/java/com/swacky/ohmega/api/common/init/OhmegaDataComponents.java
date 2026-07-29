package com.swacky.ohmega.api.common.init;

import com.mojang.serialization.Codec;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public final class OhmegaDataComponents {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static DataComponentType<ItemAttributeModifiers> getAccessoryActiveModifiers() {
        return IMPL.getAccessoryActiveModifiers();
    }

    public static DataComponentType<Boolean> getActive() {
        return IMPL.getActive();
    }

    public static DataComponentType<Integer> getSlotIndex() {
        return IMPL.getSlotIndex();
    }

    public static DataComponentType<ItemAttributeModifiers> getSlotActiveModifiers() {
        return IMPL.getSlotActiveModifiers();
    }

    @SuppressWarnings("ProtectedMemberInFinalClass")
    protected static DataComponentType<Boolean> createActive() {
        return DataComponentType.<Boolean>builder()
                .persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL)
                .cacheEncoding()
                .build();
    }

    @SuppressWarnings("ProtectedMemberInFinalClass")
    protected static DataComponentType<ItemAttributeModifiers> createItemAttributeModifiers() {
        return DataComponentType.<ItemAttributeModifiers>builder()
                .persistent(ItemAttributeModifiers.CODEC)
                .networkSynchronized(ItemAttributeModifiers.STREAM_CODEC)
                .cacheEncoding()
                .build();
    }

    @SuppressWarnings("ProtectedMemberInFinalClass")
    protected static DataComponentType<Integer> createSlotIndex() {
        return DataComponentType.<Integer>builder()
                .persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.VAR_INT)
                .cacheEncoding()
                .build();
    }

    /**
     * Gets the accessory active modifiers on an {@link ItemStack} and avoids {@code null} dereferencing by providing a default value fallback
     * @param stack the {@link ItemStack} to fetch the bound data from
     * @return the {@link ItemAttributeModifiers} used as accessory attribute modifiers bound to the passed stack
     */
    public static ItemAttributeModifiers getAccessoryActiveModifiers(ItemStack stack) {
        return stack.getOrDefault(getAccessoryActiveModifiers(), ItemAttributeModifiers.EMPTY);
    }

    /**
     * Gets the active state on an {@link ItemStack} and avoids {@code null} dereferencing by providing a default value fallback
     * @param stack the {@link ItemStack} to fetch the bound data from
     * @return the active state bound to the passed stack
     */
    public static boolean isActive(ItemStack stack) {
        return stack.getOrDefault(getActive(), false);
    }

    /**
     * Gets the slot index on an {@link ItemStack} and avoids {@code null} dereferencing by providing a default value fallback
     * @param stack the {@link ItemStack} to fetch the bound data from
     * @return the slot index bound to the passed stack
     */
    public static int getSlotIndex(ItemStack stack) {
        return stack.getOrDefault(getSlotIndex(), -1);
    }

    /**
     * Gets the slot active modifiers on an {@link ItemStack} and avoids {@code null} dereferencing by providing a default value fallback
     * @param stack the {@link ItemStack} to fetch the bound data from
     * @return the {@link ItemAttributeModifiers} used as slot attribute modifiers bound to the passed stack
     */
    public static ItemAttributeModifiers getSlotActiveModifiers(ItemStack stack) {
        return stack.getOrDefault(getSlotActiveModifiers(), ItemAttributeModifiers.EMPTY);
    }

    public interface Service {
        String ACCESSORY_ACTIVE_MODIFIERS_KEY = "accessory_active_modifiers";
        String ACTIVE_KEY = "active";
        String SLOT_INDEX_KEY = "slot_index";
        String SLOT_ACTIVE_MODIFIERS_KEY = "slot_active_modifiers";

        DataComponentType<ItemAttributeModifiers> getAccessoryActiveModifiers();

        DataComponentType<Boolean> getActive();

        DataComponentType<Integer> getSlotIndex();

        DataComponentType<ItemAttributeModifiers> getSlotActiveModifiers();
    }
}
