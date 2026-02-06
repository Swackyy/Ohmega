package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public final class OhmegaDataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, OhmegaCommon.MODID);

    private static final IAttachmentSerializer<CompoundTag, AccessoryContainer> SERIALIZER = new IAttachmentSerializer<>() {
        @Override
        public @NonNull AccessoryContainer read(@NonNull IAttachmentHolder holder, @NonNull CompoundTag tag) {
            AccessoryContainer data = AccessoryContainer.CODEC.parse(NbtOps.INSTANCE, tag).result().orElseGet(AccessoryContainer::new);

            data.onAttach((Player) holder);
            return data;
        }

        @Override
        public CompoundTag write(@NonNull AccessoryContainer data) {
            return (CompoundTag) AccessoryContainer.CODEC.encodeStart(NbtOps.INSTANCE, data).result().orElseGet(CompoundTag::new);
        }
    };

    public static final Supplier<AttachmentType<AccessoryContainer>> ACCESSORY_HANDLER = register("accessory_data",
            () -> AttachmentType.builder(AccessoryContainer::new)
                    .serialize(SERIALIZER)
                    .build());

    private static <T> Supplier<AttachmentType<T>> register(String id, Supplier<AttachmentType<T>> sup) {
        return ATTACHMENT_TYPES.register(id, sup);
    }

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
