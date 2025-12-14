package com.swacky.ohmega.common.accessorytype;

import com.swacky.ohmega.common.OhmegaCommon;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class AccessoryType {
    public static final StreamCodec<ByteBuf, AccessoryType> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            AccessoryType::getId,
            Identifier.STREAM_CODEC,
            AccessoryType::getEmptySlotLocation,
            ByteBufCodecs.INT,
            AccessoryType::getPriority,
            ByteBufCodecs.INT,
            AccessoryType::getHoverTextColour,
            ByteBufCodecs.BOOL,
            AccessoryType::displayHoverText,
            AccessoryType::new
    );

    private final Identifier id;
    private final Identifier emptySlotLocation;
    private final int priority;
    private final int hoverTextColour;
    private final boolean displayHoverText;

    // Deferred to ensure they are non-null
    public static final Supplier<AccessoryType> GENERIC = () -> AccessoryTypeManager.getInstance().get(OhmegaCommon.id("generic"));
    public static final Supplier<AccessoryType> NORMAL = () -> AccessoryTypeManager.getInstance().get(OhmegaCommon.id("normal"));
    public static final Supplier<AccessoryType> UTILITY = () -> AccessoryTypeManager.getInstance().get(OhmegaCommon.id("utility"));
    public static final Supplier<AccessoryType> SPECIAL = () -> AccessoryTypeManager.getInstance().get(OhmegaCommon.id("special"));

    protected AccessoryType(Identifier id, Identifier emptySlotLocation, int priority, int hoverTextColour, boolean displayHoverText) {
        this.id = id;
        this.emptySlotLocation = emptySlotLocation;
        this.priority = priority;
        this.hoverTextColour = hoverTextColour;
        this.displayHoverText = displayHoverText;
    }

    protected AccessoryType(String modid, String idPath, AccessoryTypeLow data) {
        this(
                Identifier.fromNamespaceAndPath(modid, idPath),
                data.emptySlotPath.indexOf(':') == -1 ? Identifier.fromNamespaceAndPath(modid, data.emptySlotPath) : Identifier.parse(data.emptySlotPath),
                data.priority,
                data.hoverTextColour,
                data.displayHoverText);
    }

    public Identifier getId() {
        return this.id;
    }

    public Identifier getEmptySlotLocation() {
        return this.emptySlotLocation;
    }

    public int getHoverTextColour() {
        return this.hoverTextColour;
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean displayHoverText() {
        return this.displayHoverText;
    }

    public boolean isDefault() {
        return this == NORMAL.get();
    }

    public String getTranslationKey() {
        return "accessory_type." + this.getId().getNamespace() + "." + this.getId().getPath();
    }

    public MutableComponent getTranslation() {
        return Component.translatable(getTranslationKey()).withStyle(Style.EMPTY.withColor(this.getHoverTextColour()));
    }
}
