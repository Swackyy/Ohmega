package com.swacky.ohmega.common.accessorytype;

import com.google.gson.*;
import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public class AccessoryTypeLow {
    protected final String emptySlotPath;
    protected final int priority;
    protected final int hoverTextColour;
    protected final boolean displayHoverText;

    protected AccessoryTypeLow(String emptySlotPath, int priorityIndex, int hoverTextColour, boolean displayHoverText) {
        this.emptySlotPath = emptySlotPath;
        this.priority = priorityIndex;
        this.hoverTextColour = hoverTextColour;
        this.displayHoverText = displayHoverText;
    }

    public static class Deserializer implements JsonDeserializer<AccessoryTypeLow> {
        private static final Deserializer INSTANCE = new Deserializer();

        private Deserializer() {
        }

        public static Deserializer getInstance() {
            return INSTANCE;
        }

        private static final String DEFAULT_EMPTY_SLOT = Ohmega.MODID + ":item/accessory_slot_normal";

        @Override
        public AccessoryTypeLow deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = GsonHelper.convertToJsonObject(element, "entry");
            String emptySlotPath = GsonHelper.getAsString(json, "emptySlotTexture", DEFAULT_EMPTY_SLOT);
            if (emptySlotPath.isBlank()) {
                emptySlotPath = DEFAULT_EMPTY_SLOT;
            }
            return new AccessoryTypeLow(
                    emptySlotPath,
                    Math.abs(GsonHelper.getAsInt(json, "priority", 0)),
                    GsonHelper.getAsInt(json, "hoverTextColor", 0xffffff),
                    GsonHelper.getAsBoolean(json, "displayHoverText", true));
        }
    }
}
