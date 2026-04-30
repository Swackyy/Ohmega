package com.swacky.ohmega.api.datagen.client;

import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;

/**
 * Use this to easily add translations for your own accessory types in data generation
 */
public final class OhmegaLangHelper {
    private final BiConsumer<String, String> adder;
    private final String namespace;

    /**
     * @param adder a function reference, usually to (pseudo) {@code this.add("key", "translation")}
     * @param namespace usually your mod ID, but can be whatever desired
     */
    public OhmegaLangHelper(BiConsumer<String, String> adder, String namespace) {
        this.adder = adder;
        this.namespace = namespace;
    }

    /**
     * Adds translations for an accessory type
     * @param namespace specify an alternate namespace that does not match the {@link #namespace} field
     * @param typeKey accessory type key, e.g: "normal", "utility"
     * @param translation the translation for the accessory type, e.g: "Normal", "Utility"
     */
    public void addType(String namespace, String typeKey, String translation) {
        adder.accept("accessory_type." + namespace + '.' + typeKey, translation);
        adder.accept("tag.item." + namespace + '.' + typeKey, translation);
    }

    /**
     * Adds translations for an accessory type
     * @param typeKey accessory type key, e.g: "normal", "utility"
     * @param translation the translation for the accessory type, e.g: "Normal", "Utility"
     */
    public void addType(String typeKey, String translation) {
        addType(namespace, typeKey, translation);
    }

    /**
     * Adds a tooltip for an accessory item
     * @param item accessory item
     * @param translation translation shown when hovered, e.g: "Allows the wearer to fly"
     */
    public void addItemTooltip(Item item, String translation) {
        adder.accept(item.getDescriptionId() + ".tooltip", translation);
    }

    /**
     * Adds a tooltip for an accessory item
     * @param item accessory item
     * @param translation translation shown when hovered, allows for '%s' replacement by the key-bind, e.g: "Press %s to toggle flight"
     */
    public void addItemKeybindTooltip(Item item, String translation) {
        adder.accept(item.getDescriptionId() + ".tooltip.keybind", translation);
    }

    /**
     * Adds all translations for a keybound item
     * @param item accessory item
     * @param translation item name translation, e.g: "Angel Ring"
     * @param tooltipTranslation translation shown when hovered, e.g: "Allows the wearer to fly"
     * @param tooltipKeyboundTranslation translation shown when hovered, allows for '%s' replacement by the key-bind, e.g: "Press %s to toggle flight"
     */
    public void addKeyboundItem(Item item, String translation, String tooltipTranslation, String tooltipKeyboundTranslation) {
        adder.accept(item.getDescriptionId(), translation);
        addItemTooltip(item, tooltipTranslation);
        addItemKeybindTooltip(item, tooltipKeyboundTranslation);
    }
}
