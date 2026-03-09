package com.swacky.ohmega.common.accessorytype;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Type;
import java.util.HexFormat;
import java.util.function.Supplier;

public final class AccessoryType {
    public static final StreamCodec<FriendlyByteBuf, AccessoryType> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AccessoryType::getId,
            ByteBufCodecs.BOOL, AccessoryType::displayHoverText,
            Identifier.STREAM_CODEC, AccessoryType::getEmptySlotLocation,
            ByteBufCodecs.INT, AccessoryType::getHoverTextColour,
            ByteBufCodecs.INT, AccessoryType::getPriority,
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

    // Json keys
    public static final String DISPLAY_HOVER_TEXT_KEY = "displayHoverText";
    public static final String EMPTY_SLOT_TEXTURE_KEY = "emptySlotTexture";
    public static final String HOVER_TEXT_COLOUR_KEY = "hoverTextColor";
    public static final String PRIORITY_KEY = "priority";

    // Use these for data generation
    public static final Identifier GENERIC_ID = OhmegaCommon.id("generic");
    public static final Identifier NORMAL_ID  = OhmegaCommon.id("normal");
    public static final Identifier UTILITY_ID = OhmegaCommon.id("utility");
    public static final Identifier SPECIAL_ID = OhmegaCommon.id("special");

    // todo: change this comment when the new strategy is applied
    // Deferred to ensure they are non-null
    public static final Supplier<AccessoryType> GENERIC = () -> AccessoryTypeManager.get(GENERIC_ID);
    public static final Supplier<AccessoryType> NORMAL  = () -> AccessoryTypeManager.get(NORMAL_ID);
    public static final Supplier<AccessoryType> UTILITY = () -> AccessoryTypeManager.get(UTILITY_ID);
    public static final Supplier<AccessoryType> SPECIAL = () -> AccessoryTypeManager.get(SPECIAL_ID);

    private final Identifier id;
    private final boolean displayHoverText;
    private final Identifier emptySlotLocation;
    private final int hoverTextColour;
    private final int priority;

    private AccessoryType(Identifier id, boolean displayHoverText, Identifier emptySlotLocation, int hoverTextColour, int priority) {
        this.id = id;
        this.displayHoverText = displayHoverText;
        this.emptySlotLocation = emptySlotLocation;
        this.hoverTextColour = hoverTextColour;
        this.priority = priority;
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

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder {
        private static final String LOCATION_PREFIX = "container/slot/"; // Mojang sometimes changes this

        private boolean displayHoverText = true;
        private String emptySlotPath = OhmegaCommon.id("accessory_slot_normal").toString();
        private int hoverTextColour = 0xffffff;
        private int priority = 0;

        public Builder displayHoverText(boolean value) {
            this.displayHoverText = value;

            return this;
        }

        public Builder emptySlotPath(String emptySlotPath) {
            this.emptySlotPath = emptySlotPath;

            return this;
        }

        public Builder emptySlotPath(Identifier location) {
            this.emptySlotPath = location.toString();

            return this;
        }

        public Builder hideHoverText() {
            this.displayHoverText = false;

            return this;
        }

        public Builder hoverTextColour(int hoverTextColour) {
            this.hoverTextColour = hoverTextColour;

            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;

            return this;
        }

        public AccessoryType build(String namespace, String path) {
            return new AccessoryType(
                    Identifier.fromNamespaceAndPath(namespace, path),
                    displayHoverText,
                    emptySlotPath.indexOf(':') == -1 ?
                            Identifier.fromNamespaceAndPath(namespace, LOCATION_PREFIX + emptySlotPath) :
                            Identifier.parse(emptySlotPath).withPrefix(LOCATION_PREFIX),
                    hoverTextColour,
                    priority);
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

            if (json.has(PRIORITY_KEY)) {
                builder.priority(GsonHelper.getAsInt(json, PRIORITY_KEY));
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
        public JsonElement serialize(Builder src, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            object.addProperty(DISPLAY_HOVER_TEXT_KEY, src.displayHoverText);
            object.addProperty(EMPTY_SLOT_TEXTURE_KEY, src.emptySlotPath);
            object.addProperty(HOVER_TEXT_COLOUR_KEY, src.hoverTextColour);
            object.addProperty(PRIORITY_KEY, src.priority);

            return object;
        }
    }
}
