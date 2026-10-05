package com.swacky.ohmega.api.common.accessorytype;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaTags;
import com.swacky.ohmega.api.common.item.datacomponent.AccessorySlotModifiers;
import com.swacky.ohmega.api.util.codec.OhmegaCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The pseudo registry object for accessory types, fetched from JSON data by the {@link AccessoryTypeManager}
 * <p>
 * View the <a href="https://github.com/Swackyy/Ohmega/wiki">wiki</a> to learn how to create your own unique accessory types
 */
public final class AccessoryType {
    // Keys
    private static final @NonNull String ADVANCEMENT_REWARDS_KEY = "advancement_rewards";
    private static final @NonNull String ALLOW_FALLBACK_KEY = "allow_fallback";
    private static final @NonNull String ALLOW_REFERENCE_KEY = "allow_reference";
    private static final @NonNull String ATTRIBUTE_MODIFIERS_KEY = "attribute_modifiers";
    private static final @NonNull String DEFAULT_SLOTS_KEY = "default_slots";
    private static final @NonNull String DISPLAY_HOVER_TEXT_KEY = "display_hover_text";
    private static final @NonNull String EMPTY_SLOT_TEXTURE_KEY = "empty_slot_texture";
    private static final @NonNull String HOVER_TEXT_COLOUR_KEY = "hover_text_color";
    private static final @NonNull String SLOT_PRIORITY_KEY = "slot_priority";
    private static final @NonNull String TYPE_PRIORITY_KEY = "type_priority";

    public static final @NonNull Codec<AccessoryType> INITIALISER_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Identifier.CODEC.fieldOf("id").forGetter(AccessoryType::getId),
            AdvancementSlotRewards.CODEC.fieldOf(ADVANCEMENT_REWARDS_KEY).forGetter(AccessoryType::getAdvancementRewards),
            Codec.BOOL.fieldOf(ALLOW_FALLBACK_KEY).forGetter(AccessoryType::allowFallback),
            Codec.BOOL.fieldOf(ALLOW_REFERENCE_KEY).forGetter(AccessoryType::allowReference),
            AccessorySlotModifiers.CODEC.fieldOf(ATTRIBUTE_MODIFIERS_KEY).forGetter(AccessoryType::getAttributeModifiers),
            Codec.INT.fieldOf(DEFAULT_SLOTS_KEY).forGetter(AccessoryType::getDefaultSlots),
            Codec.BOOL.fieldOf(DISPLAY_HOVER_TEXT_KEY).forGetter(AccessoryType::displayHoverText),
            Identifier.CODEC.fieldOf(EMPTY_SLOT_TEXTURE_KEY).forGetter(AccessoryType::getEmptySlotLocation),
            OhmegaCodecs.COLOUR_INT.fieldOf(HOVER_TEXT_COLOUR_KEY).forGetter(AccessoryType::getHoverTextColour),
            Codec.INT.fieldOf(SLOT_PRIORITY_KEY).forGetter(AccessoryType::getSlotPriority),
            Codec.INT.fieldOf(TYPE_PRIORITY_KEY).forGetter(AccessoryType::getTypePriority)
    ).apply(builder, AccessoryType::new));

    public static final @NonNull Codec<AccessoryType> CODEC = Identifier.CODEC.xmap(
            AccessoryTypeManager::get,
            AccessoryType::getId);

    public static final @NonNull StreamCodec<RegistryFriendlyByteBuf, AccessoryType> INITIALISER_STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AccessoryType::getId,
            AdvancementSlotRewards.STREAM_CODEC, AccessoryType::getAdvancementRewards,
            ByteBufCodecs.BOOL, AccessoryType::allowFallback,
            ByteBufCodecs.BOOL, AccessoryType::allowReference,
            AccessorySlotModifiers.STREAM_CODEC, AccessoryType::getAttributeModifiers,
            ByteBufCodecs.VAR_INT, AccessoryType::getDefaultSlots,
            ByteBufCodecs.BOOL, AccessoryType::displayHoverText,
            Identifier.STREAM_CODEC, AccessoryType::getEmptySlotLocation,
            ByteBufCodecs.INT, AccessoryType::getHoverTextColour,
            ByteBufCodecs.INT, AccessoryType::getSlotPriority,
            ByteBufCodecs.INT, AccessoryType::getTypePriority,
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
            .typePriority(Integer.MAX_VALUE)
            .build(NONE_ID);
    // Deferred to ensure they are not 'ohmega:none'
    public static final @NonNull Supplier<AccessoryType> GENERIC = () -> AccessoryTypeManager.get(GENERIC_ID);
    public static final @NonNull Supplier<AccessoryType> NORMAL  = () -> AccessoryTypeManager.get(NORMAL_ID);
    public static final @NonNull Supplier<AccessoryType> UTILITY = () -> AccessoryTypeManager.get(UTILITY_ID);
    public static final @NonNull Supplier<AccessoryType> SPECIAL = () -> AccessoryTypeManager.get(SPECIAL_ID);

    private final @NonNull Identifier id;
    private final @NonNull AdvancementSlotRewards advancementSlotRewards;
    private final boolean allowFallback;
    private final boolean allowReference;
    private final @NonNull AccessorySlotModifiers attributeModifiers;
    private final int defaultSlots;
    private final boolean displayHoverText;
    private final @NonNull Identifier emptySlotLocation;
    private final int hoverTextColour;
    private final int slotPriority;
    private final int typePriority;

    /**
     * Called internally by Ohmega to construct accessory types from parsed data.
     * If you wish to create a type in code (for data-generation), use the {@link Builder}
     * @param id unique identifier for this type
     * @param advancementSlotRewards numbers of slots to grant of this accessory type when matching advancements are achieved
     * @param allowFallback allows accessories to default to this as a fallback type
     * @param allowReference allows this type to be able to be explicitly referenced in most ways in-game
     * @param attributeModifiers any attribute modifiers to apply along with it
     * @param defaultSlots the number of slots to suggest to the default accessory slots list of this type
     * @param displayHoverText whether text should be displayed when hovering over a slot of this type. May be overridden globally by a client config option
     * @param emptySlotLocation the location of the texture to display when a slot of this type is empty
     * @param hoverTextColour the colour of the text displayed when hovering. Only for when {@code displayHoverText} is {@code true}
     * @param slotPriority the priority index for use in the ordering of accessory slots, more specifically, for the default slot types.
     *                     A lower priority index technically signifies a higher priority
     * @param typePriority the priority index for this type to use if an item is tagged with multiple different types.
     *                     A lower priority index technically signifies a higher priority
     */
    private AccessoryType(
            @NonNull Identifier id,
            @NonNull AdvancementSlotRewards advancementSlotRewards,
            boolean allowFallback,
            boolean allowReference,
            @NonNull AccessorySlotModifiers attributeModifiers,
            int defaultSlots,
            boolean displayHoverText,
            @NonNull Identifier emptySlotLocation,
            int hoverTextColour,
            int slotPriority,
            int typePriority) {
        this.id = id;
        this.advancementSlotRewards = advancementSlotRewards;
        this.allowFallback = allowFallback;
        this.allowReference = allowReference;
        this.attributeModifiers = attributeModifiers;
        this.defaultSlots = defaultSlots;
        this.displayHoverText = displayHoverText;
        this.emptySlotLocation = emptySlotLocation;
        this.hoverTextColour = hoverTextColour;
        this.slotPriority = slotPriority;
        this.typePriority = typePriority;
    }

    /**
     * Get the unique identifier for this type
     * @return stored type ID
     */
    public @NonNull Identifier getId() {
        return id;
    }

    /**
     * Gets the {@link AdvancementSlotRewards} data structure,
     * which contains data pertaining to granting slots of this type when specified advancements are achieved
     * @return held advancement reward map
     */
    public @NonNull AdvancementSlotRewards getAdvancementRewards() {
        return advancementSlotRewards;
    }

    /**
     * Check whether this accessory type supports falling back
     * @return {@code true} if this type allows accessories to default to this as a fallback type
     */
    public boolean allowFallback() {
        return allowFallback;
    }

    /**
     * Check if this type should be able to be explicitly referenced in most ways in-game
     * @return {@code true} to allow referencing this type, {@code false} otherwise
     */
    public boolean allowReference() {
        return allowReference;
    }

    /**
     * Get the attribute modifiers to apply when an item is in a slot of this type
     * @return any attribute modifiers to apply along with it
     */
    public @NonNull AccessorySlotModifiers getAttributeModifiers() {
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
     * Get the number of slots to add to the default accessory slots list (by default, as it is inherently mutable)
     * @return the number of slots to suggest to the default accessory slots list of this type
     */
    public int getDefaultSlots() {
        return defaultSlots;
    }

    /**
     * Get the empty slot texture location to use
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
     * Get the priority for use in the ordering of the default slot types list
     * @return the priority index for use in the ordering of accessory slots, more specifically, for the default slot types.
     * A lower priority index technically signifies a higher priority
     */
    public int getSlotPriority() {
        return slotPriority;
    }

    /**
     * Get the priority for use in items' accessory type conflicts
     * @return the priority index for this type to use if an item is tagged with multiple different types.
     * A lower priority index technically signifies a higher priority
     */
    public int getTypePriority() {
        return typePriority;
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
        return Component.translatable(Ohmega.MODID + ".accessory_type." + id.getNamespace() + "." + id.getPath()).withColor(getHoverTextColour());
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
    public @NonNull TagKey<@NonNull Item> getTag() {
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
        private static final @NonNull String LOCATION_PREFIX = "container/slot/"; // Mojang sometimes changes this
        private static final AdvancementSlotRewards ADVANCEMENT_REWARDS_DEFAULT = AdvancementSlotRewards.EMPTY;
        private static final boolean ALLOW_FALLBACK_DEFAULT = true;
        private static final boolean ALLOW_REFERENCE_DEFAULT = true;
        private static final AccessorySlotModifiers ATTRIBUTE_MODIFIERS_DEFAULT = AccessorySlotModifiers.EMPTY;
        private static final int DEFAULT_SLOTS_DEFAULT = 0;
        private static final boolean DISPLAY_HOVER_TEXT_DEFAULT = true;
        private static final String EMPTY_SLOT_TEXTURE_DEFAULT = Ohmega.id("accessory_slot_normal").toString();
        private static final int HOVER_TEXT_COLOUR_DEFAULT = 0xffffff;
        private static final int SLOT_PRIORITY_DEFAULT = 0;
        private static final int TYPE_PRIORITY_DEFAULT = 0;

        // Purely for data-generation
        public static final @NonNull Codec<Builder> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                AdvancementSlotRewards.CODEC.optionalFieldOf(ADVANCEMENT_REWARDS_KEY, ADVANCEMENT_REWARDS_DEFAULT).forGetter(inst -> inst.advancementSlotRewards),
                Codec.BOOL.optionalFieldOf(ALLOW_FALLBACK_KEY, ALLOW_FALLBACK_DEFAULT).forGetter(inst -> inst.allowFallback),
                Codec.BOOL.optionalFieldOf(ALLOW_REFERENCE_KEY, ALLOW_REFERENCE_DEFAULT).forGetter(inst -> inst.allowReference),
                AccessorySlotModifiers.CODEC.optionalFieldOf(ATTRIBUTE_MODIFIERS_KEY, ATTRIBUTE_MODIFIERS_DEFAULT).forGetter(inst -> inst.attributeModifiers),
                Codec.INT.optionalFieldOf(DEFAULT_SLOTS_KEY, DEFAULT_SLOTS_DEFAULT).forGetter(inst -> inst.defaultSlots),
                Codec.BOOL.optionalFieldOf(DISPLAY_HOVER_TEXT_KEY, DISPLAY_HOVER_TEXT_DEFAULT).forGetter(inst -> inst.displayHoverText),
                Codec.STRING.optionalFieldOf(EMPTY_SLOT_TEXTURE_KEY, EMPTY_SLOT_TEXTURE_DEFAULT).forGetter(inst -> inst.emptySlotPath),
                OhmegaCodecs.COLOUR_INT.optionalFieldOf(HOVER_TEXT_COLOUR_KEY, HOVER_TEXT_COLOUR_DEFAULT).forGetter(inst -> inst.hoverTextColour),
                Codec.INT.optionalFieldOf(SLOT_PRIORITY_KEY, SLOT_PRIORITY_DEFAULT).forGetter(inst -> inst.slotPriority),
                Codec.INT.optionalFieldOf(TYPE_PRIORITY_KEY, TYPE_PRIORITY_DEFAULT).forGetter(inst -> inst.typePriority)
        ).apply(builder, Builder::new));

        public static final @NonNull Codec<Map<String, Builder>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, CODEC);

        private @NonNull AdvancementSlotRewards advancementSlotRewards;
        private boolean allowFallback;
        private boolean allowReference;
        private @NonNull AccessorySlotModifiers attributeModifiers;
        private int defaultSlots;
        private boolean displayHoverText;
        private @NonNull String emptySlotPath;
        private int hoverTextColour;
        private int slotPriority;
        private int typePriority;

        private Builder(
                @NonNull AdvancementSlotRewards advancementSlotRewards,
                boolean allowFallback,
                boolean allowReference,
                @NonNull AccessorySlotModifiers attributeModifiers,
                int defaultSlots,
                boolean displayHoverText,
                @NonNull String emptySlotPath,
                int hoverTextColour,
                int slotPriority,
                int typePriority) {
            this.advancementSlotRewards = advancementSlotRewards;
            this.allowFallback = allowFallback;
            this.allowReference = allowReference;
            this.attributeModifiers = attributeModifiers;
            this.defaultSlots = defaultSlots;
            this.displayHoverText = displayHoverText;
            this.emptySlotPath = emptySlotPath;
            this.hoverTextColour = hoverTextColour;
            this.slotPriority = slotPriority;
            this.typePriority = typePriority;
        }

        public Builder() {
            this.advancementSlotRewards = ADVANCEMENT_REWARDS_DEFAULT;
            this.allowFallback = ALLOW_FALLBACK_DEFAULT;
            this.allowReference = ALLOW_REFERENCE_DEFAULT;
            this.attributeModifiers = ATTRIBUTE_MODIFIERS_DEFAULT;
            this.defaultSlots = DEFAULT_SLOTS_DEFAULT;
            this.displayHoverText = DISPLAY_HOVER_TEXT_DEFAULT;
            this.emptySlotPath = EMPTY_SLOT_TEXTURE_DEFAULT;
            this.hoverTextColour = HOVER_TEXT_COLOUR_DEFAULT;
            this.slotPriority = SLOT_PRIORITY_DEFAULT;
            this.typePriority = TYPE_PRIORITY_DEFAULT;
        }

        /**
         * Add a number of slots of this type when specified advancements are achieved
         * @param rewards advancement reward map to apply
         * @return the current builder instance
         */
        public @NonNull Builder advancementRewards(@NonNull AdvancementSlotRewards rewards) {
            advancementSlotRewards = rewards;

            return this;
        }

        /**
         * Add a number of slots of this type when specified advancements are achieved
         * Shortcut method to {@link #advancementRewards(AdvancementSlotRewards)}, supplying a builder in to {@link Consumer} method reference
         * @param factory method reference accepting a {@link AdvancementSlotRewards.Builder},
         *                finishing with returning the result of {@link AdvancementSlotRewards.Builder#build()}
         * @return the current builder instance
         */
        public @NonNull Builder advancementRewards(@NonNull Function<AdvancementSlotRewards.@NonNull Builder, @NonNull AdvancementSlotRewards> factory) {
            return advancementRewards(factory.apply(new AdvancementSlotRewards.Builder()));
        }

        /**
         * Add some attribute modifiers to apply when an item is in a slot of this type
         * @param modifiers attribute modifiers to apply
         * @return the current builder instance
         */
        public @NonNull Builder attributeModifiers(@NonNull AccessorySlotModifiers modifiers) {
            attributeModifiers = modifiers;

            return this;
        }

        /**
         * Add some attribute modifiers to apply when an item is in a slot of this type.
         * @param factory method reference accepting a {@link AccessorySlotModifiers.Builder},
         *                finishing with returning the result of {@link AccessorySlotModifiers.Builder#build()}
         * @return the current builder instance
         */
        public @NonNull Builder attributeModifiers(@NonNull Function<AccessorySlotModifiers.@NonNull Builder, @NonNull AccessorySlotModifiers> factory) {
            return attributeModifiers(factory.apply(new AccessorySlotModifiers.Builder()));
        }

        /**
         * Set the number of slots to add to the default slots list
         * @param defaultSlots number of slots to add (by default) of this type
         * @return the current builder instance
         */
        public @NonNull Builder defaultSlots(int defaultSlots) {
            this.defaultSlots = defaultSlots;

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
            allowFallback = false;

            return this;
        }

        /**
         * Check if this type should not be able to be explicitly referenced in most ways in-game
         * @return the current builder instance
         */
        public @NonNull Builder preventReference() {
            this.allowReference = false;

            return this;
        }

        /**
         * Set the priority for use in the ordering of the default slot types list
         * @param priority index to set as
         * @return the current builder instance
         * @apiNote A lower priority index technically signifies a higher priority
         */
        public @NonNull Builder slotPriority(int priority) {
            this.slotPriority = priority;

            return this;
        }

        /**
         * Set the priority for use in items' accessory type conflicts
         * @param priority index to set as
         * @return the current builder instance
         * @apiNote A lower priority index technically signifies a higher priority
         */
        public @NonNull Builder typePriority(int priority) {
            this.typePriority = priority;

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
            Identifier emptySlotPathProcessed;

            if (emptySlotPath.indexOf(':') == -1) {
                emptySlotPathProcessed = Identifier.fromNamespaceAndPath(namespace, LOCATION_PREFIX + emptySlotPath);
            } else {
                emptySlotPathProcessed = Identifier.parse(emptySlotPath).withPrefix(LOCATION_PREFIX);
            }

            return new AccessoryType(
                    Identifier.fromNamespaceAndPath(namespace, path),
                    advancementSlotRewards,
                    allowFallback,
                    allowReference,
                    attributeModifiers,
                    defaultSlots,
                    displayHoverText,
                    emptySlotPathProcessed,
                    hoverTextColour,
                    slotPriority,
                    typePriority);
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
