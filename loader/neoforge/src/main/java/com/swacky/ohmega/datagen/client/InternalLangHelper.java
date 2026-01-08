package com.swacky.ohmega.datagen.client;

import com.swacky.ohmega.common.OhmegaCommon;
import net.neoforged.neoforge.common.data.LanguageProvider;

final class InternalLangHelper {
    private final LanguageProvider provider;

    InternalLangHelper(LanguageProvider provider) {
        this.provider = provider;
    }

    void addDataPackDescription(String translation) {
        provider.add("dataPack." + OhmegaCommon.MODID + ".description", translation);
    }

    void addConfig(String key, String translation) {
        provider.add(OhmegaCommon.MODID + ".configuration." + key, translation);
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
