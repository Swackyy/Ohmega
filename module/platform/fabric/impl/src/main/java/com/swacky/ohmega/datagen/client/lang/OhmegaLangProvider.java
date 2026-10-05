package com.swacky.ohmega.datagen.client.lang;

import com.swacky.ohmega.api.client.config.OhmegaClientConfig;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public abstract class OhmegaLangProvider extends FabricLanguageProvider {
    protected static final String KEY_ACCESSORY_TYPE = Ohmega.MODID + ".accessory_type";
    protected static final String KEY_ACCESSORY_TYPE_NONE = AccessoryType.NONE_ID.getPath();
    protected static final String KEY_ACCESSORY_TYPE_GENERIC = AccessoryType.GENERIC_ID.getPath();
    protected static final String KEY_ACCESSORY_TYPE_NORMAL = AccessoryType.NORMAL_ID.getPath();
    protected static final String KEY_ACCESSORY_TYPE_UTILITY = AccessoryType.UTILITY_ID.getPath();
    protected static final String KEY_ACCESSORY_TYPE_SPECIAL = AccessoryType.SPECIAL_ID.getPath();
    protected static final String KEY_ADVANCEMENT_SLOT_REWARDS = "advancement." + Ohmega.MODID + ".slot_rewards";
    protected static final String KEY_CONFIG_SECTION_CLIENT = "client.toml";
    protected static final String KEY_CONFIG_SECTION_SERVER = "server.toml";
    protected static final String KEY_ITEM_MODIFIERS_ACCESSORY_ACTIVE = Ohmega.MODID + ".item.modifiers.accessory_active";
    protected static final String KEY_BIND_ACCESSORY_TYPE = "key." + Ohmega.MODID + ".accessory_type";
    protected static final String KEY_TOAST_TRANSLATION_TITLE = "translation.title";
    protected static final String KEY_TOAST_TRANSLATION_MESSAGE = "translation.message";
    protected static final String KEY_NAME = Ohmega.MODID + ".name";

    public OhmegaLangProvider(FabricPackOutput output, String locale, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, locale, lookup);
    }

    protected static @NonNull String createPositionDescription(String template, String coordinate, String... args) {
        return OhmegaClientConfig.createPositionDescription(template, coordinate, args);
    }
}
