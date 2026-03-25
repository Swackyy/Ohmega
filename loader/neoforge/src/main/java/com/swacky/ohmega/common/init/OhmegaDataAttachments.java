package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public final class OhmegaDataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Ohmega.MODID);

    private static final IAttachmentSerializer<AccessoryContainer> SERIALIZER = new IAttachmentSerializer<>() {
        @SuppressWarnings("deprecation")
        @Override
        public @NonNull AccessoryContainer read(@NonNull IAttachmentHolder holder, ValueInput input) {
            AccessoryContainer data = input.read(AccessoryContainer.MAP_CODEC).orElseThrow();

            data.onAttach((Player) holder);
            return data;
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean write(@NonNull AccessoryContainer data, ValueOutput output) {
            output.store(AccessoryContainer.MAP_CODEC, data);
            return true;
        }
    };

    public static final Supplier<AttachmentType<AccessoryContainer>> ACCESSORY_HANDLER = register("accessory_data",
            () -> AttachmentType.builder(AccessoryContainer::new)
                    .serialize(SERIALIZER)
                    //.sync(AccessoryInvDataAttachment.STREAM_CODEC)
                    .build());

    private static <T> Supplier<AttachmentType<T>> register(String id, Supplier<AttachmentType<T>> sup) {
        return ATTACHMENT_TYPES.register(id, sup);
    }

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
