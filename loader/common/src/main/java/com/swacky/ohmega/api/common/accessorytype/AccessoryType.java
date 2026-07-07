package com.swacky.ohmega.api.common.accessorytype;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.common.item.datacomponent.AccessoryModifiers;
import com.swacky.ohmega.api.util.codec.OhmegaCodecs;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaTags;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.HexFormat;
import java.util.List;
import java.util.Map;
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
    public static final @NonNull String PREVENT_FALLBACK_KEY = "preventFallback";
    public static final @NonNull String PREVENT_REFERENCE_KEY = "preventReference";
    public static final @NonNull String PRIORITY_KEY = "priority";

    public static final @NonNull Codec<AccessoryType> INITIALISER_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Identifier.CODEC.fieldOf("id").forGetter(AccessoryType::getId),
            AccessoryModifiers.CODEC.fieldOf(ATTRIBUTE_MODIFIERS_KEY).forGetter(AccessoryType::getAttributeModifiers),
            Codec.BOOL.fieldOf(DISPLAY_HOVER_TEXT_KEY).forGetter(AccessoryType::displayHoverText),
            Identifier.CODEC.fieldOf(EMPTY_SLOT_TEXTURE_KEY).forGetter(AccessoryType::getEmptySlotLocation),
            OhmegaCodecs.COLOUR_INT.fieldOf(HOVER_TEXT_COLOUR_KEY).forGetter(AccessoryType::getHoverTextColour),
            Codec.BOOL.fieldOf(PREVENT_FALLBACK_KEY).forGetter(AccessoryType::shouldPreventFallback),
            Codec.BOOL.fieldOf(PREVENT_REFERENCE_KEY).forGetter(AccessoryType::shouldPreventReference),
            Codec.INT.fieldOf(PRIORITY_KEY).forGetter(AccessoryType::getPriority)
    ).apply(builder, AccessoryType::new));

    public static final @NonNull Codec<AccessoryType> CODEC = Identifier.CODEC.xmap(
            AccessoryTypeManager::get,
            AccessoryType::getId);

    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, AccessoryType> INITIALISER_STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AccessoryType::getId,
            AccessoryModifiers.STREAM_CODEC, AccessoryType::getAttributeModifiers,
            ByteBufCodecs.BOOL, AccessoryType::displayHoverText,
            Identifier.STREAM_CODEC, AccessoryType::getEmptySlotLocation,
            ByteBufCodecs.INT, AccessoryType::getHoverTextColour,
            ByteBufCodecs.BOOL, AccessoryType::shouldPreventFallback,
            ByteBufCodecs.BOOL, AccessoryType::shouldPreventReference,
            ByteBufCodecs.INT, AccessoryType::getPriority,
            AccessoryType::new);

    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, List<AccessoryType>> LIST_INITIALISER_STREAM_CODEC = INITIALISER_STREAM_CODEC.apply(
            ByteBufCodecs.list());

    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, AccessoryType> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> Identifier.STREAM_CODEC.encode(buf, value.getId()),
            buf -> AccessoryTypeManager.get(Identifier.STREAM_CODEC.decode(buf)));

    // Use these for data generation
    public static final @NonNull Identifier NONE_ID    = Ohmega.id("none");
    public static final @NonNull Identifier GENERIC_ID = Ohmega.id("generic");
    public static final @NonNull Identifier NORMAL_ID  = Ohmega.id("normal");
    public static final @NonNull Identifier UTILITY_ID = Ohmega.id("utility");
    public static final @NonNull Identifier SPECIAL_ID = Ohmega.id("special");

    // A placeholder or "unknown" accessory type. Do not use this
    public static final @NonNull AccessoryType NONE = new Builder()
            .preventReference()
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
    private final boolean preventFallback;
    private final boolean preventReference;
    private final int priority;

    /**
     * Called internally by Ohmega to construct accessory types.
     * If you wish to create a type in code, use the {@link Builder}
     * @param id unique identifier for this type
     * @param attributeModifiers any attribute modifiers to apply along with it
     * @param displayHoverText whether text should be displayed when hovering over a slot of this type. May be overridden globally by a client config option
     * @param emptySlotLocation the location of the texture to display when a slot of this type is empty
     * @param hoverTextColour the colour of the text displayed when hovering. Only for when {@code displayHoverText} is {@code true}
     * @param preventFallback prevents accessories from defaulting to this as a fallback type
     * @param preventReference prevents this type from being able to be explicitly referenced in most ways in-game
     * @param priority the priority index for this type to use if an item is tagged with multiple different types.
     *                 Lower indexes technically mean higher priority
     */
    private AccessoryType(
            @NonNull Identifier id,
            @NonNull AccessoryModifiers attributeModifiers,
            boolean displayHoverText,
            @NonNull Identifier emptySlotLocation,
            int hoverTextColour,
            boolean preventFallback,
            boolean preventReference,
            int priority) {
        this.id = id;
        this.attributeModifiers = attributeModifiers;
        this.displayHoverText = displayHoverText;
        this.emptySlotLocation = emptySlotLocation;
        this.hoverTextColour = hoverTextColour;
        this.preventFallback = preventFallback;
        this.preventReference = preventReference;
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

    // todo: move away from "prevent" prefix
    /**
     * Check whether this accessory type supports falling back
     * @return prevents accessories from defaulting to this as a fallback type
     */
    public boolean shouldPreventFallback() {
        return preventFallback;
    }

    // todo: move away from "prevent" prefix
    /**
     * Check if this type should not be able to be explicitly referenced in most ways in-game
     * @return {@code true} to prevent referencing this type, {@code false} otherwise
     */
    public boolean shouldPreventReference() {
        return preventReference;
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
     * @return the item tag associated with this type
     * @apiNote Do not use this in data generation, refer to {@link OhmegaTags#get(Identifier)}
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
                Codec.BOOL.fieldOf(PREVENT_FALLBACK_KEY).forGetter(inst -> inst.preventFallback),
                Codec.BOOL.fieldOf(PREVENT_REFERENCE_KEY).forGetter(inst -> inst.preventReference),
                Codec.INT.fieldOf(PRIORITY_KEY).forGetter(inst -> inst.priority)
        ).apply(builder, Builder::new));

        public static final @NonNull Codec<Map<String, Builder>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC);

        private static final String LOCATION_PREFIX = "container/slot/"; // Mojang sometimes changes this

        private @NonNull AccessoryModifiers attributeModifiers;
        private boolean displayHoverText;
        private @NonNull String emptySlotPath;
        private int hoverTextColour;
        private boolean preventFallback;
        private boolean preventReference;
        private int priority;

        private Builder(
                @NonNull AccessoryModifiers attributeModifiers,
                boolean displayHoverText,
                @NonNull String emptySlotPath,
                int hoverTextColour,
                boolean preventFallback,
                boolean preventReference,
                int priority) {
            this.attributeModifiers = attributeModifiers;
            this.displayHoverText = displayHoverText;
            this.emptySlotPath = emptySlotPath;
            this.hoverTextColour = hoverTextColour;
            this.preventFallback = preventFallback;
            this.preventReference = preventReference;
            this.priority = priority;
        }

        public Builder() {
            this.attributeModifiers = AccessoryModifiers.EMPTY;
            this.displayHoverText = true;
            this.emptySlotPath = Ohmega.id("accessory_slot_normal").toString();
            this.hoverTextColour = 0xffffff;
            this.preventFallback = false;
            this.preventReference = false;
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
         * Prevents accessories of this type from falling back to this type
         * @return the current builder instance
         */
        public @NonNull Builder preventFallback() {
            preventFallback = true;

            return this;
        }

        /**
         * Check if this type should not be able to be explicitly referenced in most ways in-game
         * @return the current builder instance
         */
        public @NonNull Builder preventReference() {
            this.preventReference = true;

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
                    preventFallback,
                    preventReference,
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
