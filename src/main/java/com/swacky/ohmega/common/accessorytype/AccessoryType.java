package com.swacky.ohmega.common.accessorytype;

import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class AccessoryType {
    private final ResourceLocation id;
    private final ResourceLocation emptySlotLocation;
    private final int priority;
    private final int hoverTextColour;
    private final boolean displayHoverText;

    // Deferred to ensure they are non-null
    public static final Supplier<AccessoryType> GENERIC = () -> AccessoryTypeManager.getInstance().get(Ohmega.rl("generic"));
    public static final Supplier<AccessoryType> NORMAL = () -> AccessoryTypeManager.getInstance().get(Ohmega.rl("normal"));
    public static final Supplier<AccessoryType> UTILITY = () -> AccessoryTypeManager.getInstance().get(Ohmega.rl("utility"));
    public static final Supplier<AccessoryType> SPECIAL = () -> AccessoryTypeManager.getInstance().get(Ohmega.rl("special"));

    protected AccessoryType(String modid, String idPath, AccessoryTypeLow data) {
        this.id = ResourceLocation.fromNamespaceAndPath(modid, idPath);
        this.emptySlotLocation = data.emptySlotPath.indexOf(':') == -1 ? ResourceLocation.fromNamespaceAndPath(modid, data.emptySlotPath) : ResourceLocation.parse(data.emptySlotPath);
        this.priority = data.priority;
        this.hoverTextColour = data.hoverTextColour;
        this.displayHoverText = data.displayHoverText;
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
        return this == NORMAL;
    }

    public String getTranslationKey() {
        return "accessory_type." + this.getId().getNamespace() + "." + this.getId().getPath();
    }

    public MutableComponent getTranslation() {
        return Component.translatable(getTranslationKey()).withStyle(Style.EMPTY.withColor(this.getHoverTextColour()));
    }
}
