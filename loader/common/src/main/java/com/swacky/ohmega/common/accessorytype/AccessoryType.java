package com.swacky.ohmega.common.accessorytype;

import com.google.common.collect.ImmutableSet;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public final class AccessoryType {
    public static final StreamCodec<FriendlyByteBuf, AccessoryType> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, AccessoryType::getId,
            ResourceLocation.STREAM_CODEC, AccessoryType::getEmptySlotLocation,
            ByteBufCodecs.INT, AccessoryType::getPriority,
            ByteBufCodecs.INT, AccessoryType::getHoverTextColour,
            ByteBufCodecs.BOOL, AccessoryType::displayHoverText,
            AccessoryType::new);

    public static final StreamCodec<FriendlyByteBuf, ImmutableSet<AccessoryType>> SET_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NonNull ImmutableSet<AccessoryType> decode(@NonNull FriendlyByteBuf buf) {
            int size = VarInt.read(buf);
            ImmutableSet.Builder<AccessoryType> builder = ImmutableSet.builderWithExpectedSize(size);

            for (int i = 0; i < size; i++) {
                builder.add(AccessoryType.STREAM_CODEC.decode(buf));
            }

            return builder.build();
        }

        @Override
        public void encode(@NonNull FriendlyByteBuf buf, @NonNull ImmutableSet<AccessoryType> values) {
            VarInt.write(buf, values.size());

            for (AccessoryType value : values) {
                AccessoryType.STREAM_CODEC.encode(buf, value);
            }
        }
    };

    private static final String LOCATION_PREFIX = "container/slot/"; // Mojang sometimes changes this

    public static final ResourceLocation GENERIC_ID = OhmegaCommon.rl("generic");
    public static final ResourceLocation NORMAL_ID  = OhmegaCommon.rl("normal");
    public static final ResourceLocation UTILITY_ID = OhmegaCommon.rl("utility");
    public static final ResourceLocation SPECIAL_ID = OhmegaCommon.rl("special");

    // Deferred to ensure they are non-null
    public static final Supplier<AccessoryType> GENERIC = () -> AccessoryTypeManager.getInstance().get(GENERIC_ID);
    public static final Supplier<AccessoryType> NORMAL  = () -> AccessoryTypeManager.getInstance().get(NORMAL_ID);
    public static final Supplier<AccessoryType> UTILITY = () -> AccessoryTypeManager.getInstance().get(UTILITY_ID);
    public static final Supplier<AccessoryType> SPECIAL = () -> AccessoryTypeManager.getInstance().get(SPECIAL_ID);

    private final ResourceLocation id;
    private final ResourceLocation emptySlotLocation;
    private final int priority;
    private final int hoverTextColour;
    private final boolean displayHoverText;

    private AccessoryType(ResourceLocation id, ResourceLocation emptySlotLocation, int priority, int hoverTextColour, boolean displayHoverText) {
        this.id = id;
        this.emptySlotLocation = emptySlotLocation;
        this.priority = priority;
        this.hoverTextColour = hoverTextColour;
        this.displayHoverText = displayHoverText;
    }

    AccessoryType(String namespace, String path, ProtoAccessoryType data) {
        this(
                ResourceLocation.fromNamespaceAndPath(namespace, path),
                data.emptySlotPath.indexOf(':') == -1 ?
                        ResourceLocation.fromNamespaceAndPath(namespace, LOCATION_PREFIX + data.emptySlotPath) :
                        ResourceLocation.parse(data.emptySlotPath).withPrefix(LOCATION_PREFIX),
                data.priority,
                data.hoverTextColour,
                data.displayHoverText);
    }

    public ResourceLocation getId() {
        return id;
    }

    public ResourceLocation getEmptySlotLocation() {
        return emptySlotLocation;
    }

    public int getHoverTextColour() {
        return hoverTextColour;
    }

    public int getPriority() {
        return priority;
    }

    public boolean displayHoverText() {
        return displayHoverText;
    }

    public boolean isDefault() {
        return this == NORMAL.get();
    }

    public String getTranslationKey() {
        return "accessory_type." + id.getNamespace() + "." + id.getPath();
    }

    public MutableComponent getTranslation() {
        return Component.translatable(getTranslationKey()).withStyle(Style.EMPTY.withColor(getHoverTextColour()));
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }

        if (obj instanceof AccessoryType other) {
            // The Identifier is really the only one which matters here
            return id.equals(other.id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
