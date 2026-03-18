package com.swacky.ohmega.common.accessorytype;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;

import java.lang.reflect.Type;
import java.util.HexFormat;
import java.util.function.Supplier;

public final class AccessoryType {
    // JSON keys
    public static final String DISPLAY_HOVER_TEXT_KEY = "displayHoverText";
    public static final String EMPTY_SLOT_TEXTURE_KEY = "emptySlotTexture";
    public static final String HOVER_TEXT_COLOUR_KEY = "hoverTextColor";
    public static final String PRIORITY_KEY = "priority";

    // Use these for data generation
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
    private final boolean displayHoverText;
    private final ResourceLocation emptySlotLocation;
    private final int hoverTextColour;
    private final int priority;

    private AccessoryType(ResourceLocation id, boolean displayHoverText, ResourceLocation emptySlotLocation, int hoverTextColour, int priority) {
        this.id = id;
        this.displayHoverText = displayHoverText;
        this.emptySlotLocation = emptySlotLocation;
        this.hoverTextColour = hoverTextColour;
        this.priority = priority;
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
        return new TranslatableComponent(getTranslationKey()).withStyle(Style.EMPTY.withColor(getHoverTextColour()));
    }

    @Override
    public String toString() {
        return id.toString();
    }

    /**
     * Do not use this in data generation, refer to {@link OhmegaTags#get(ResourceLocation)}
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
            // The ResourceLocation is really the only one which matters here
            return id.equals(other.id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static AccessoryType read(FriendlyByteBuf buf) {
        return new AccessoryType(
                buf.readResourceLocation(),
                buf.readBoolean(),
                buf.readResourceLocation(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeBoolean(displayHoverText);
        buf.writeResourceLocation(emptySlotLocation);
        buf.writeInt(hoverTextColour);
        buf.writeInt(priority);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder {
        private static final String LOCATION_PREFIX = "textures/item/"; // Mojang sometimes changes this

        private boolean displayHoverText = true;
        private String emptySlotPath = OhmegaCommon.rl("accessory_slot_normal").toString();
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

        public Builder emptySlotPath(ResourceLocation location) {
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
            ResourceLocation id = new ResourceLocation(namespace, path);
            ResourceLocation location;

            if (emptySlotPath.indexOf(':') == -1){
                location = new ResourceLocation(namespace, LOCATION_PREFIX + emptySlotPath);
            } else {
                location = ResourceLocation.tryParse(emptySlotPath);

                if (location != null) {
                    location = new ResourceLocation(location.getNamespace(), LOCATION_PREFIX + location.getPath());
                }
            }

            if (location != null) {
                if (!location.getPath().endsWith(".png")) {
                    location = new ResourceLocation(location.getNamespace(), location.getPath() + ".png");
                }

                return new AccessoryType(
                        id,
                        displayHoverText,
                        location,
                        hoverTextColour,
                        priority);
            }

            return null;
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
