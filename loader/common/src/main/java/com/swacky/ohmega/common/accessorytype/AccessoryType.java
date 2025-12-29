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
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public final class AccessoryType {
    public static final StreamCodec<FriendlyByteBuf, AccessoryType> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AccessoryType::getId,
            Identifier.STREAM_CODEC, AccessoryType::getEmptySlotLocation,
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

    public static final Identifier GENERIC_ID = OhmegaCommon.id("generic");
    public static final Identifier NORMAL_ID  = OhmegaCommon.id("normal");
    public static final Identifier UTILITY_ID = OhmegaCommon.id("utility");
    public static final Identifier SPECIAL_ID = OhmegaCommon.id("special");

    // Deferred to ensure they are non-null
    public static final Supplier<AccessoryType> GENERIC = () -> AccessoryTypeManager.getInstance().get(GENERIC_ID);
    public static final Supplier<AccessoryType> NORMAL  = () -> AccessoryTypeManager.getInstance().get(NORMAL_ID);
    public static final Supplier<AccessoryType> UTILITY = () -> AccessoryTypeManager.getInstance().get(UTILITY_ID);
    public static final Supplier<AccessoryType> SPECIAL = () -> AccessoryTypeManager.getInstance().get(SPECIAL_ID);

    private final Identifier id;
    private final Identifier emptySlotLocation;
    private final int priority;
    private final int hoverTextColour;
    private final boolean displayHoverText;

    private AccessoryType(Identifier id, Identifier emptySlotLocation, int priority, int hoverTextColour, boolean displayHoverText) {
        this.id = id;
        this.emptySlotLocation = emptySlotLocation;
        this.priority = priority;
        this.hoverTextColour = hoverTextColour;
        this.displayHoverText = displayHoverText;
    }

    AccessoryType(String namespace, String path, ProtoAccessoryType data) {
        this(
                Identifier.fromNamespaceAndPath(namespace, path),
                data.emptySlotPath.indexOf(':') == -1 ?
                        Identifier.fromNamespaceAndPath(namespace, LOCATION_PREFIX + data.emptySlotPath) :
                        Identifier.parse(data.emptySlotPath).withPrefix(LOCATION_PREFIX),
                data.priority,
                data.hoverTextColour,
                data.displayHoverText);
    }

    public Identifier getId() {
        return id;
    }

    public Identifier getEmptySlotLocation() {
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
