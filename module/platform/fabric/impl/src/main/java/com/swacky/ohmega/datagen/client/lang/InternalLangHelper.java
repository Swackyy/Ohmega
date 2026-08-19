package com.swacky.ohmega.datagen.client.lang;

import com.swacky.ohmega.api.common.Ohmega;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;

public final class InternalLangHelper {
    private final FabricLanguageProvider.TranslationBuilder builder;

    public InternalLangHelper(FabricLanguageProvider.TranslationBuilder builder) {
        this.builder = builder;
    }

    public void add(KeyMapping.Category category, String translation) {
        builder.add(((TranslatableContents) category.label().getContents()).getKey(), translation);
    }

    public void add(KeyMapping mapping, String translation) {
        builder.add(mapping.getName(), translation);
    }

    public void addConfig(String key, String translation) {
        builder.add(Ohmega.MODID + ".configuration." + key, translation);
    }

    public void addConfigTitle(String translation) {
        addConfig("title", translation);
    }

    public void addConfigSection(String key, String translation, String titleTranslation) {
        String string = "section." + Ohmega.MODID + '.' + key;

        addConfig(string, translation);
        addConfig(string + ".title", titleTranslation);
    }

    public void addConfigOption(String key, String optionName, String optionDescription) {
        addConfig(key, optionName);
        addConfig(key + ".tooltip", optionDescription);
    }

    public void addConfigButton(String key, String translation) {
        addConfig(key + ".button", translation);
    }

    public void addConfigPort(String key, String translation) {
        builder.add(Ohmega.MODID + ".port.neoforge.configuration.uitext." + key, translation);
    }

    public void addConfigPortTitle(String key, String translation) {
        addConfigPort("title." + key, translation);
    }

    public void addConfigPortType(String key, String translation) {
        addConfigPort("type." + key, translation);
    }

    public void addDataPackDescription(String translation) {
        builder.add("dataPack." + Ohmega.MODID + ".description", translation);
    }

    public void addDataPackDescription(Identifier key, String translation) {
        builder.add("dataPack." + key.toDebugFileName() + ".description", translation);
    }

    public void addToast(String key, String translation) {
        builder.add("toast." + Ohmega.MODID + '.' + key, translation);
    }
}