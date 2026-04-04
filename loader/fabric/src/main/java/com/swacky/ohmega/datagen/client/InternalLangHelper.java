package com.swacky.ohmega.datagen.client;

import com.swacky.ohmega.common.Ohmega;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

final class InternalLangHelper {
    private final FabricLanguageProvider.TranslationBuilder builder;

    InternalLangHelper(FabricLanguageProvider.TranslationBuilder builder) {
        this.builder = builder;
    }

    void addDataPackDescription(String translation) {
        builder.add("dataPack." + Ohmega.MODID + ".description", translation);
    }

    void addConfigPort(String key, String translation) {
        builder.add(Ohmega.MODID + ".port.neoforge.configuration.uitext." + key, translation);
    }

    void addConfigPortTitle(String key, String translation) {
        addConfigPort("title." + key, translation);
    }

    void addConfigPortType(String key, String translation) {
        addConfigPort("type." + key, translation);
    }

    void addConfig(String key, String translation) {
        builder.add(Ohmega.MODID + ".configuration." + key, translation);
    }

    void addConfigTitle(String translation) {
        addConfig("title", translation);
    }

    void addConfigSection(String key, String translation, String titleTranslation) {
        String string = "section." + Ohmega.MODID + '.' + key;

        addConfig(string, translation);
        addConfig(string + ".title", titleTranslation);
    }

    void addConfigOption(String key, String optionName, String optionDescription) {
        addConfig(key, optionName);
        addConfig(key + ".tooltip", optionDescription);
    }

    void addConfigButton(String key, String translation) {
        addConfig(key + ".button", translation);
    }
}