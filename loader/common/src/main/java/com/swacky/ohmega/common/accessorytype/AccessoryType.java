package com.swacky.ohmega.common.accessorytype;

import com.swacky.ohmega.common.OhmegaCommon;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class AccessoryType {
    public static final StreamCodec<ByteBuf, AccessoryType> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            AccessoryType::getId,
            ResourceLocation.STREAM_CODEC,
            AccessoryType::getEmptySlotLocation,
            ByteBufCodecs.INT,
            AccessoryType::getPriority,
            ByteBufCodecs.INT,
            AccessoryType::getHoverTextColour,
            ByteBufCodecs.BOOL,
            AccessoryType::displayHoverText,
            AccessoryType::new
    );

    private final ResourceLocation id;
    private final ResourceLocation emptySlotLocation;
    private final int priority;
    private final int hoverTextColour;
    private final boolean displayHoverText;

    // Deferred to ensure they are non-null
    public static final Supplier<AccessoryType> GENERIC = () -> AccessoryTypeManager.getInstance().get(OhmegaCommon.rl("generic"));
    public static final Supplier<AccessoryType> NORMAL = () -> AccessoryTypeManager.getInstance().get(OhmegaCommon.rl("normal"));
    public static final Supplier<AccessoryType> UTILITY = () -> AccessoryTypeManager.getInstance().get(OhmegaCommon.rl("utility"));
    public static final Supplier<AccessoryType> SPECIAL = () -> AccessoryTypeManager.getInstance().get(OhmegaCommon.rl("special"));

    protected AccessoryType(ResourceLocation id, ResourceLocation emptySlotLocation, int priority, int hoverTextColour, boolean displayHoverText) {
        this.id = id;
        this.emptySlotLocation = emptySlotLocation;
        this.priority = priority;
        this.hoverTextColour = hoverTextColour;
        this.displayHoverText = displayHoverText;
    }

    protected AccessoryType(String modid, String idPath, AccessoryTypeLow data) {
        this(
                ResourceLocation.fromNamespaceAndPath(modid, idPath),
                data.emptySlotPath.indexOf(':') == -1 ? ResourceLocation.fromNamespaceAndPath(modid, data.emptySlotPath) : ResourceLocation.parse(data.emptySlotPath),
                data.priority,
                data.hoverTextColour,
                data.displayHoverText);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public ResourceLocation getEmptySlotLocation() {
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
