package com.swacky.ohmega.common.accessorytype;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import com.swacky.ohmega.api.common.item.datacomponent.AccessoryModifiers;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaTags;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;

import java.lang.reflect.Type;
import java.util.HexFormat;
import java.util.function.Supplier;

// todo: move JSON serialisation to just use codecs
public final class AccessoryType {
    public static final StreamCodec<RegistryFriendlyByteBuf, AccessoryType> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AccessoryType::getId,
            AccessoryModifiers.STREAM_CODEC, AccessoryType::getAttributeModifiers,
            ByteBufCodecs.BOOL, AccessoryType::displayHoverText,
            Identifier.STREAM_CODEC, AccessoryType::getEmptySlotLocation,
            ByteBufCodecs.INT, AccessoryType::getHoverTextColour,
            ByteBufCodecs.BOOL, AccessoryType::isNoFallback,
            ByteBufCodecs.INT, AccessoryType::getPriority,
            AccessoryType::new);

    // JSON keys
    public static final String ATTRIBUTE_MODIFIERS_KEY = "attributeModifiers";
    public static final String DISPLAY_HOVER_TEXT_KEY = "displayHoverText";
    public static final String EMPTY_SLOT_TEXTURE_KEY = "emptySlotTexture";
    public static final String HOVER_TEXT_COLOUR_KEY = "hoverTextColor";
    public static final String NO_FALLBACK_KEY = "noFallback";
    public static final String PRIORITY_KEY = "priority";

    // Use these for data generation
    public static final Identifier NONE_ID    = Ohmega.id("none");
    public static final Identifier GENERIC_ID = Ohmega.id("generic");
    public static final Identifier NORMAL_ID  = Ohmega.id("normal");
    public static final Identifier UTILITY_ID = Ohmega.id("utility");
    public static final Identifier SPECIAL_ID = Ohmega.id("special");

    // A placeholder or "unknown" accessory type. Do not use this
    public static final AccessoryType NONE = new AccessoryType.Builder()
            .priority(Integer.MAX_VALUE)
            .build(NONE_ID);
    // Deferred to ensure they are correct
    public static final Supplier<AccessoryType> GENERIC = () -> AccessoryTypeManager.get(GENERIC_ID);
    public static final Supplier<AccessoryType> NORMAL  = () -> AccessoryTypeManager.get(NORMAL_ID);
    public static final Supplier<AccessoryType> UTILITY = () -> AccessoryTypeManager.get(UTILITY_ID);
    public static final Supplier<AccessoryType> SPECIAL = () -> AccessoryTypeManager.get(SPECIAL_ID);

    private final Identifier id;
    private final AccessoryModifiers attributeModifiers;
    private final boolean displayHoverText;
    private final Identifier emptySlotLocation;
    private final int hoverTextColour;
    private final boolean noFallback;
    private final int priority;

    private AccessoryType(
            Identifier id,
            AccessoryModifiers attributeModifiers,
            boolean displayHoverText,
            Identifier emptySlotLocation,
            int hoverTextColour,
            boolean noFallback,
            int priority) {
        this.id = id;
        this.attributeModifiers = attributeModifiers;
        this.displayHoverText = displayHoverText;
        this.emptySlotLocation = emptySlotLocation;
        this.hoverTextColour = hoverTextColour;
        this.noFallback = noFallback;
        this.priority = priority;
    }

    public Identifier getId() {
        return id;
    }

    public AccessoryModifiers getAttributeModifiers() {
        return attributeModifiers;
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

    public boolean isNoFallback() {
        return noFallback;
    }

    public boolean isDefault() {
        return this == NONE;
    }

    public String getTranslationKey() {
        return "accessory_type." + id.getNamespace() + "." + id.getPath();
    }

    public MutableComponent getTranslation() {
        return Component.translatable(getTranslationKey()).withStyle(Style.EMPTY.withColor(getHoverTextColour()));
    }

    @Override
    public String toString() {
        return id.toString();
    }

    /**
     * Do not use this in data generation, refer to {@link OhmegaTags#get(Identifier)}
     */
    public TagKey<Item> getTag() {
        return OhmegaTags.get(this);
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            return true;
        }

        if (object instanceof AccessoryType other) {
            // The Identifier is really the only one which matters here
            return id.equals(other.id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder {
        private static final String LOCATION_PREFIX = "container/slot/"; // Mojang sometimes changes this

        private AccessoryModifiers attributeModifiers = AccessoryModifiers.EMPTY;
        private boolean displayHoverText = true;
        private String emptySlotPath = Ohmega.id("accessory_slot_normal").toString();
        private int hoverTextColour = 0xffffff;
        private boolean noFallback = false;
        private int priority = 0;

        public Builder attributeModifiers(AccessoryModifiers modifiers) {
            attributeModifiers = modifiers;

            return this;
        }

        public Builder displayHoverText(boolean value) {
            displayHoverText = value;

            return this;
        }

        public Builder emptySlotPath(String emptySlotPath) {
            this.emptySlotPath = emptySlotPath;

            return this;
        }

        @SuppressWarnings("unused")
        public Builder emptySlotPath(Identifier location) {
            this.emptySlotPath = location.toString();

            return this;
        }

        public Builder hideHoverText() {
            displayHoverText = false;

            return this;
        }

        public Builder hoverTextColour(int hoverTextColour) {
            this.hoverTextColour = hoverTextColour;

            return this;
        }

        public Builder noFallback(boolean value) {
            noFallback = value;

            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;

            return this;
        }

        public AccessoryType build(String namespace, String path) {
            return new AccessoryType(
                    Identifier.fromNamespaceAndPath(namespace, path),
                    attributeModifiers,
                    displayHoverText,
                    emptySlotPath.indexOf(':') == -1 ?
                            Identifier.fromNamespaceAndPath(namespace, LOCATION_PREFIX + emptySlotPath) :
                            Identifier.parse(emptySlotPath).withPrefix(LOCATION_PREFIX),
                    hoverTextColour,
                    noFallback,
                    priority);
        }

        public AccessoryType build(Identifier id) {
            return build(id.getNamespace(), id.getPath());
        }
    }

    public static final class Deserializer implements JsonDeserializer<Builder> {
        public static final Gson GSON = new GsonBuilder()
                .registerTypeAdapter(AccessoryType.Builder.class, new Deserializer())
                .create();

        private Deserializer() {}

        @Override
        public Builder deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            Builder builder = new Builder();
            JsonObject json = GsonHelper.convertToJsonObject(element, "entry");

            if (json.has(ATTRIBUTE_MODIFIERS_KEY)) {
                builder.attributeModifiers(AccessoryModifiers.CODEC.parse(
                        JsonOps.INSTANCE,
                        json.get(ATTRIBUTE_MODIFIERS_KEY)
                ).result().orElse(AccessoryModifiers.EMPTY));
            }

            if (json.has(DISPLAY_HOVER_TEXT_KEY)) {
                builder.displayHoverText(GsonHelper.convertToBoolean(json.get(DISPLAY_HOVER_TEXT_KEY), DISPLAY_HOVER_TEXT_KEY));
            }

            if (json.has(EMPTY_SLOT_TEXTURE_KEY)) {
                builder.emptySlotPath(GsonHelper.convertToString(json.get(EMPTY_SLOT_TEXTURE_KEY), EMPTY_SLOT_TEXTURE_KEY));
            }

            if (json.has(HOVER_TEXT_COLOUR_KEY)) {
                JsonElement hoverTextColourElement = json.get("hoverTextColor");

                if (GsonHelper.isNumberValue(hoverTextColourElement)) {
                    builder.hoverTextColour(hoverTextColourElement.getAsInt());
                } else {
                    if (hoverTextColourElement.isJsonPrimitive()) {
                        String string = hoverTextColourElement.getAsString();

                        if (string.startsWith("0x")) {
                            builder.hoverTextColour(HexFormat.fromHexDigits(string, 2, string.length()));
                        } else {
                            builder.hoverTextColour(HexFormat.fromHexDigits(string));
                        }
                    } else {
                        throw new JsonSyntaxException("Expected " + HOVER_TEXT_COLOUR_KEY + " to be an Int or a string, was " + GsonHelper.getType(json));
                    }
                }
            }

            if (json.has(NO_FALLBACK_KEY)) {
                builder.noFallback(GsonHelper.convertToBoolean(json.get(NO_FALLBACK_KEY), NO_FALLBACK_KEY));
            }

            if (json.has(PRIORITY_KEY)) {
                builder.priority(GsonHelper.convertToInt(json.get(PRIORITY_KEY), PRIORITY_KEY));
            }

            return builder;
        }
    }

    public static final class Serializer implements JsonSerializer<Builder> {
        public static final Gson GSON = new GsonBuilder()
                .registerTypeAdapter(AccessoryType.Builder.class, new Serializer())
                .create();

        private Serializer() {}

        @Override
        public JsonElement serialize(Builder builder, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            object.add(ATTRIBUTE_MODIFIERS_KEY, AccessoryModifiers.CODEC
                    .encodeStart(JsonOps.INSTANCE, builder.attributeModifiers)
                    .result()
                    .orElseGet(JsonArray::new));
            object.addProperty(DISPLAY_HOVER_TEXT_KEY, builder.displayHoverText);
            object.addProperty(EMPTY_SLOT_TEXTURE_KEY, builder.emptySlotPath);
            object.addProperty(HOVER_TEXT_COLOUR_KEY, builder.hoverTextColour);
            object.addProperty(NO_FALLBACK_KEY, builder.noFallback);
            object.addProperty(PRIORITY_KEY, builder.priority);

            return object;
        }
    }
}
