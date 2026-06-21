package com.swacky.ohmega.api.common.accessorytype;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.common.item.datacomponent.AccessoryModifiers;
import com.swacky.ohmega.api.util.codec.OhmegaCodecs;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaTags;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * The pseudo registry object for accessory types, fetched from JSON data by the {@link AccessoryTypeManager}
 * <p>
 * View the <a href="https://github.com/Swackyy/Ohmega/wiki">wiki</a> to learn how to create your own unique accessory types
 */
public final class AccessoryType {
    // Keys
    public static final @NonNull String ATTRIBUTE_MODIFIERS_KEY = "attributeModifiers";
    public static final @NonNull String DISPLAY_HOVER_TEXT_KEY = "displayHoverText";
    public static final @NonNull String EMPTY_SLOT_TEXTURE_KEY = "emptySlotTexture";
    public static final @NonNull String HOVER_TEXT_COLOUR_KEY = "hoverTextColor";
    public static final @NonNull String NO_FALLBACK_KEY = "noFallback";
    public static final @NonNull String NO_SPECIFY_KEY = "noSpecify";
    public static final @NonNull String PRIORITY_KEY = "priority";

    public static final @NonNull Codec<AccessoryType> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Identifier.CODEC.fieldOf("id").forGetter(AccessoryType::getId),
            AccessoryModifiers.CODEC.fieldOf(ATTRIBUTE_MODIFIERS_KEY).forGetter(AccessoryType::getAttributeModifiers),
            Codec.BOOL.fieldOf(DISPLAY_HOVER_TEXT_KEY).forGetter(AccessoryType::displayHoverText),
            Identifier.CODEC.fieldOf(EMPTY_SLOT_TEXTURE_KEY).forGetter(AccessoryType::getEmptySlotLocation),
            OhmegaCodecs.COLOUR_INT.fieldOf(HOVER_TEXT_COLOUR_KEY).forGetter(AccessoryType::getHoverTextColour),
            Codec.BOOL.fieldOf(NO_FALLBACK_KEY).forGetter(AccessoryType::isNoFallback),
            Codec.BOOL.fieldOf(NO_SPECIFY_KEY).forGetter(AccessoryType::isNoSpecify),
            Codec.INT.fieldOf(PRIORITY_KEY).forGetter(AccessoryType::getPriority)
    ).apply(builder, AccessoryType::new));

    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, AccessoryType> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AccessoryType::getId,
            AccessoryModifiers.STREAM_CODEC, AccessoryType::getAttributeModifiers,
            ByteBufCodecs.BOOL, AccessoryType::displayHoverText,
            Identifier.STREAM_CODEC, AccessoryType::getEmptySlotLocation,
            ByteBufCodecs.INT, AccessoryType::getHoverTextColour,
            ByteBufCodecs.BOOL, AccessoryType::isNoFallback,
            ByteBufCodecs.BOOL, AccessoryType::isNoSpecify,
            ByteBufCodecs.INT, AccessoryType::getPriority,
            AccessoryType::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, Collection<AccessoryType>> COLLECTION_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NonNull Collection<AccessoryType> decode(@NonNull RegistryFriendlyByteBuf buf) {
            int size = VarInt.read(buf);
            Set<AccessoryType> map = new HashSet<>(size);

            for (int i = 0; i < size; i++) {
                map.add(AccessoryType.STREAM_CODEC.decode(buf));
            }

            return map;
        }

        @Override
        public void encode(@NonNull RegistryFriendlyByteBuf buf, @NonNull Collection<AccessoryType> values) {
            VarInt.write(buf, values.size());

            for (AccessoryType value : values) {
                AccessoryType.STREAM_CODEC.encode(buf, value);
            }
        }
    };

    // Use these for data generation
    public static final @NonNull Identifier NONE_ID    = Ohmega.id("none");
    public static final @NonNull Identifier GENERIC_ID = Ohmega.id("generic");
    public static final @NonNull Identifier NORMAL_ID  = Ohmega.id("normal");
    public static final @NonNull Identifier UTILITY_ID = Ohmega.id("utility");
    public static final @NonNull Identifier SPECIAL_ID = Ohmega.id("special");

    // A placeholder or "unknown" accessory type. Do not use this
    public static final @NonNull AccessoryType NONE = new Builder()
            .noSpecify()
            .priority(Integer.MAX_VALUE)
            .build(NONE_ID);
    // Deferred to ensure they are not 'ohmega:none'
    public static final @NonNull Supplier<AccessoryType> GENERIC = () -> AccessoryTypeManager.get(GENERIC_ID);
    public static final @NonNull Supplier<AccessoryType> NORMAL  = () -> AccessoryTypeManager.get(NORMAL_ID);
    public static final @NonNull Supplier<AccessoryType> UTILITY = () -> AccessoryTypeManager.get(UTILITY_ID);
    public static final @NonNull Supplier<AccessoryType> SPECIAL = () -> AccessoryTypeManager.get(SPECIAL_ID);

    private final @NonNull Identifier id;
    private final @NonNull AccessoryModifiers attributeModifiers;
    private final boolean displayHoverText;
    private final @NonNull Identifier emptySlotLocation;
    private final int hoverTextColour;
    private final boolean noSpecify;
    private final boolean noFallback;
    private final int priority;

    /**
     * Called internally by Ohmega to construct accessory types.
     * If you wish to create a type in code, use the {@link Builder}
     * @param id unique identifier for this type
     * @param attributeModifiers any attribute modifiers to apply along with it
     * @param displayHoverText whether text should be displayed when hovering over a slot of this type. May be overridden globally by a client config option
     * @param emptySlotLocation the location of the texture to display when a slot of this type is empty
     * @param hoverTextColour the colour of the text displayed when hovering. Only for when {@code displayHoverText} is {@code true}
     * @param noFallback prevents accessories with this as the priority type from defaulting to a fallback type when no slots of this type are present
     * @param noSpecify prevents items from being specifically tagged as this accessory type
     * @param priority the priority index for this type to use if an item is tagged with multiple different types.
     *                 Lower indexes technically mean higher priority
     */
    private AccessoryType(
            @NonNull Identifier id,
            @NonNull AccessoryModifiers attributeModifiers,
            boolean displayHoverText,
            @NonNull Identifier emptySlotLocation,
            int hoverTextColour,
            boolean noFallback,
            boolean noSpecify,
            int priority) {
        this.id = id;
        this.attributeModifiers = attributeModifiers;
        this.displayHoverText = displayHoverText;
        this.emptySlotLocation = emptySlotLocation;
        this.hoverTextColour = hoverTextColour;
        this.noFallback = noFallback;
        this.noSpecify = noSpecify;
        this.priority = priority;
    }

    /**
     * Get the unique identifier for this type
     * @return stored type ID
     */
    public @NonNull Identifier getId() {
        return id;
    }

    /**
     * Get the attribute modifiers to apply when an item is in a slot of this type
     * @return any attribute modifiers to apply along with it
     */
    public @NonNull AccessoryModifiers getAttributeModifiers() {
        return attributeModifiers;
    }

    /**
     * Check whether any text should be shown when hovering over an empty slot of this type. May be overridden globally by a client config option
     * @return whether text should be displayed when hovering over an empty slot of this type
     */
    public boolean displayHoverText() {
        return displayHoverText;
    }

    /**
     *  Get the empty slot texture location to use
     * @return the location of the texture to display when a slot of this type is empty
     */
    public @NonNull Identifier getEmptySlotLocation() {
        return emptySlotLocation;
    }

    /**
     * Get the colour of the text to display if hovering over an empty slot of this type
     * @return the colour of the text displayed when hovering. Only for when {@code displayHoverText} is {@code true}
     */
    public int getHoverTextColour() {
        return hoverTextColour;
    }

    /**
     * Check if this type should not be able to be defined specifically as an item's accessory type
     * @return {@code true} to prevent items from being tagged as this type, {@code false} otherwise
     */
    public boolean isNoSpecify() {
        return noSpecify;
    }

    /**
     * Check whether this accessory type supports falling back
     * @return prevents accessories with this as the priority type from defaulting to a fallback type when no slots of this type are present
     */
    public boolean isNoFallback() {
        return noFallback;
    }

    /**
     *
     * @return the priority index for this type to use if an item is tagged with multiple different types.
     * Lower indexes technically mean higher priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Check if this accessory type is the default to fall back to
     * @return {@code true} if this is the last fallback accessory type, being the default
     */
    public boolean isDefault() {
        return this == NONE;
    }

    /**
     * Retrieve the translation to use when hovering over a slot of this type
     * @return translatable content filled component for this type
     */
    public @NonNull MutableComponent getTranslation() {
        return Component.translatable("accessory_type." + id.getNamespace() + "." + id.getPath()).withStyle(Style.EMPTY.withColor(getHoverTextColour()));
    }

    /**
     * Conforms the string representation of the type to simply the unique ID
     * @return this type represented as a string
     */
    @Override
    public @NonNull String toString() {
        return id.toString();
    }

    /**
     * Retrieve the item tag for this type
     * <p>
     * <strong>Do not use this in data generation, refer to {@link OhmegaTags#get(Identifier)}</strong>
     * @return the item tag associated with this type
     */
    public @NonNull TagKey<Item> getTag() {
        return OhmegaTags.get(this);
    }

    /**
     * Check if this object is the same as another
     * @param object the reference object with which to compare
     * @return {@code} if {@link Object#equals(Object)} or if their IDs match. {@code false} otherwise
     */
    @Override
    public boolean equals(@NonNull Object object) {
        if (super.equals(object)) {
            return true;
        }

        if (object instanceof AccessoryType other) {
            // The Identifier is really the only one which matters here
            return id.equals(other.id);
        }

        return false;
    }

    /**
     * Compute the hashcode for this type, defers to {@link Identifier#hashCode()}
     * @return the hash for this object
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Builder class to create accessory types, publicly exposed
     */
    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder {
        public static final @NonNull Codec<Builder> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                AccessoryModifiers.CODEC.fieldOf(ATTRIBUTE_MODIFIERS_KEY).forGetter(inst -> inst.attributeModifiers),
                Codec.BOOL.fieldOf(DISPLAY_HOVER_TEXT_KEY).forGetter(inst -> inst.displayHoverText),
                Codec.STRING.fieldOf(EMPTY_SLOT_TEXTURE_KEY).forGetter(inst -> inst.emptySlotPath),
                OhmegaCodecs.COLOUR_INT.fieldOf(HOVER_TEXT_COLOUR_KEY).forGetter(inst -> inst.hoverTextColour),
                Codec.BOOL.fieldOf(NO_FALLBACK_KEY).forGetter(inst -> inst.noFallback),
                Codec.BOOL.fieldOf(NO_SPECIFY_KEY).forGetter(inst -> inst.noSpecify),
                Codec.INT.fieldOf(PRIORITY_KEY).forGetter(inst -> inst.priority)
        ).apply(builder, Builder::new));

        public static final @NonNull Codec<Map<String, Builder>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC);

        private static final String LOCATION_PREFIX = "container/slot/"; // Mojang sometimes changes this

        private @NonNull AccessoryModifiers attributeModifiers;
        private boolean displayHoverText;
        private @NonNull String emptySlotPath;
        private int hoverTextColour;
        private boolean noFallback;
        private boolean noSpecify;
        private int priority;

        private Builder(
                @NonNull AccessoryModifiers attributeModifiers,
                boolean displayHoverText,
                @NonNull String emptySlotPath,
                int hoverTextColour,
                boolean noFallback,
                boolean noSpecify,
                int priority) {
            this.attributeModifiers = attributeModifiers;
            this.displayHoverText = displayHoverText;
            this.emptySlotPath = emptySlotPath;
            this.hoverTextColour = hoverTextColour;
            this.noFallback = noFallback;
            this.noSpecify = noSpecify;
            this.priority = priority;
        }

        public Builder() {
            this.attributeModifiers = AccessoryModifiers.EMPTY;
            this.displayHoverText = true;
            this.emptySlotPath = Ohmega.id("accessory_slot_normal").toString();
            this.hoverTextColour = 0xffffff;
            this.noFallback = false;
            this.noSpecify = false;
            this.priority = 0;
        }

        /**
         * Add some attribute modifiers to apply when an item is in a slot of this type
         * @param modifiers attribute modifiers to apply
         * @return the current builder instance
         */
        public @NonNull Builder attributeModifiers(@NonNull AccessoryModifiers modifiers) {
            attributeModifiers = modifiers;

            return this;
        }

        /**
         * Set the texture location for the empty slot background
         * @param emptySlotPath texture location relative to {@link #LOCATION_PREFIX}
         * @return the current builder instance
         */
        public @NonNull Builder emptySlotPath(@NonNull String emptySlotPath) {
            this.emptySlotPath = emptySlotPath;

            return this;
        }

        /**
         * Set the texture location for the empty slot background with a different namespace than the assumed one
         * @param location texture location relative to {@link #LOCATION_PREFIX}
         * @return the current builder instance
         */
        @SuppressWarnings("unused")
        public @NonNull Builder emptySlotPath(@NonNull Identifier location) {
            this.emptySlotPath = location.toString();

            return this;
        }

        /**
         * Prevent the text shown when hovering over an empty slot of this type from appearing
         * @return the current builder instance
         */
        public @NonNull Builder hideHoverText() {
            displayHoverText = false;

            return this;
        }

        /**
         * Set the colour of the text shown when hovering over an empty slot of this type
         * @param hoverTextColour colour to show as, in decimal format
         * @return the current builder instance
         */
        public @NonNull Builder hoverTextColour(int hoverTextColour) {
            this.hoverTextColour = hoverTextColour;

            return this;
        }

        /**
         * Set the colour of the text shown when hovering over an empty slot of this type
         * @param hoverTextColour colour to show as, in hexadecimal format without any prefix
         * @return the current builder instance
         */
        public @NonNull Builder hoverTextColour(@NonNull String hoverTextColour) {
            this.hoverTextColour = HexFormat.fromHexDigits(hoverTextColour);

            return this;
        }

        /**
         * Prevents accessories of this type from falling back to a different type if no slots of this type are present
         * @return the current builder instance
         */
        public @NonNull Builder noFallback() {
            noFallback = true;

            return this;
        }

        /**
         * Enabling this prevents tagging items with this accessory type
         * @return the current builder instance
         */
        public @NonNull Builder noSpecify() {
            this.noSpecify = true;

            return this;
        }

        /**
         * Set the priority index of the accessory type.
         * Lower values technically mean higher priority
         * @param priority index to set as
         * @return the current builder instance
         */
        public @NonNull Builder priority(int priority) {
            this.priority = priority;

            return this;
        }

        /**
         * Build the accessory type from {@link Builder} data.
         * This does not add it to the {@link AccessoryTypeManager}, use data generation to define a type in code and have it be registered in-game
         * @param namespace usually the mod ID, but can be anything
         * @param path the name of this accessory
         * @return the constructed accessory type
         */
        public @NonNull AccessoryType build(@NonNull String namespace, @NonNull String path) {
            return new AccessoryType(
                    Identifier.fromNamespaceAndPath(namespace, path),
                    attributeModifiers,
                    displayHoverText,
                    emptySlotPath.indexOf(':') == -1 ?
                            Identifier.fromNamespaceAndPath(namespace, LOCATION_PREFIX + emptySlotPath) :
                            Identifier.parse(emptySlotPath).withPrefix(LOCATION_PREFIX),
                    hoverTextColour,
                    noSpecify,
                    noFallback,
                    priority);
        }

        /**
         * Alternate to {@link #build(String, String)} that pulls the {@code namespace} and {@code path} from an {@link Identifier}
         * @param id accessory type unique ID to build with
         * @return the constructed accessory type
         */
        public @NonNull AccessoryType build(@NonNull Identifier id) {
            return build(id.getNamespace(), id.getPath());
        }
    }
}
