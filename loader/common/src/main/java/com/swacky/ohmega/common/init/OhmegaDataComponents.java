package com.swacky.ohmega.common.init;

import com.mojang.serialization.Codec;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

public final class OhmegaDataComponents {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static DataComponentType<Boolean> getActive() {
        return IMPL.getActive();
    }

    public static DataComponentType<Integer> getSlotIndex() {
        return IMPL.getSlotIndex();
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
    protected static DataComponentType<Integer> createSlotIndex() {
        return DataComponentType.<Integer>builder()
                .persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.VAR_INT)
                .cacheEncoding()
                .build();
    }

    public interface Service {
        String ACTIVE_KEY = "active";
        String SLOT_INDEX_KEY = "slot_index";

        DataComponentType<Boolean> getActive();

        DataComponentType<Integer> getSlotIndex();
    }
}
