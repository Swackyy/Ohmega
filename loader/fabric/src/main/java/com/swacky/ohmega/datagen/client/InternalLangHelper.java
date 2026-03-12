package com.swacky.ohmega.datagen.client;

import com.swacky.ohmega.common.OhmegaCommon;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

final class InternalLangHelper {
    private final FabricLanguageProvider.TranslationBuilder builder;

    InternalLangHelper(FabricLanguageProvider.TranslationBuilder builder) {
        this.builder = builder;
    }

    void addDataPackDescription(String translation) {
        builder.add("dataPack." + OhmegaCommon.MODID + ".description", translation);
    }

    void addConfig(String key, String translation) {
        builder.add(OhmegaCommon.MODID + ".configuration." + key, translation);
    }

    void addConfigSection(String key, String translation, String titleTranslation) {
        String string = "section." + OhmegaCommon.MODID + '.' + key;

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