package com.swacky.ohmega.datagen.client.lang;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

/**
 * Do not use this class whatsoever! (even though it is not included with the output JAR)
 */
@ApiStatus.Internal
public abstract class OhmegaLangProvider extends FabricLanguageProvider {
    protected static final String KEY_ACCESSORY_TYPE = "accessory_type";
    protected static final String KEY_ACCESSORY_TYPE_NONE = AccessoryType.NONE_ID.getPath();
    protected static final String KEY_ACCESSORY_TYPE_GENERIC = AccessoryType.GENERIC_ID.getPath();
    protected static final String KEY_ACCESSORY_TYPE_NORMAL = AccessoryType.NORMAL_ID.getPath();
    protected static final String KEY_ACCESSORY_TYPE_UTILITY = AccessoryType.UTILITY_ID.getPath();
    protected static final String KEY_ACCESSORY_TYPE_SPECIAL = AccessoryType.SPECIAL_ID.getPath();
    protected static final String KEY_BIND_ACCESSORY_TYPE = "key." + Ohmega.MODID + ".accessory_type";
    protected static final String KEY_BIND_CATEGORY = "key.category." + Ohmega.MODID + '.' + Ohmega.MODID;
    protected static final String KEY_BIND_OPEN_ACC_INV = "key." + Ohmega.MODID + ".open_acc_inv";
    protected static final String KEY_CONFIG_SECTION_CLIENT = "client.toml";
    protected static final String KEY_CONFIG_SECTION_SERVER = "server.toml";

    public OhmegaLangProvider(FabricPackOutput output, String locale, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, locale, lookup);
    }
}
