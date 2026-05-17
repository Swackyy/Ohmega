package com.swacky.ohmega.common.init;

import com.mojang.serialization.Codec;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
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
