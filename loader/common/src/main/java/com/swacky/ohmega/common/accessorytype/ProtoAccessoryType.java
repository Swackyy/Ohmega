package com.swacky.ohmega.common.accessorytype;

import com.google.gson.*;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.HexFormat;

public final class ProtoAccessoryType {
    final String emptySlotPath;
    final int priority;
    final int hoverTextColour;
    final boolean displayHoverText;

    private ProtoAccessoryType(String emptySlotPath, int priorityIndex, int hoverTextColour, boolean displayHoverText) {
        this.emptySlotPath = emptySlotPath;
        this.priority = priorityIndex;
        this.hoverTextColour = hoverTextColour;
        this.displayHoverText = displayHoverText;
    }

    public static class Deserializer implements JsonDeserializer<ProtoAccessoryType> {
        private static final Deserializer INSTANCE = new Deserializer();
        private static final String DEFAULT_EMPTY_SLOT = OhmegaCommon.rl("accessory_slot_normal").toString();

        private Deserializer() {}

        public static Deserializer getInstance() {
            return INSTANCE;
        }

        @Override
        public ProtoAccessoryType deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = GsonHelper.convertToJsonObject(element, "entry");
            int hoverTextColour;

            if (json.has("hoverTextColor")) {
                JsonElement hoverTextColourElement = json.get("hoverTextColor");

                if (GsonHelper.isNumberValue(hoverTextColourElement)) {
                    hoverTextColour = hoverTextColourElement.getAsInt();
                } else {
                    String string = hoverTextColourElement.getAsString();

                    if (string.startsWith("0x")) {
                        hoverTextColour = HexFormat.fromHexDigits(string, 2, string.length());
                    } else {
                        hoverTextColour = HexFormat.fromHexDigits(string);
                    }
                }
            } else {
                hoverTextColour = 0xffffff;
            }

            return new ProtoAccessoryType(
                    GsonHelper.getAsString(json, "emptySlotTexture", DEFAULT_EMPTY_SLOT),
                    Math.abs(GsonHelper.getAsInt(json, "priority", 0)),
                    hoverTextColour,
                    GsonHelper.getAsBoolean(json, "displayHoverText", true));
        }
    }
}
