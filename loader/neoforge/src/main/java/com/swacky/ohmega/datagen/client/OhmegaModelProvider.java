package com.swacky.ohmega.datagen.client;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class OhmegaModelProvider extends ItemModelProvider {
    public OhmegaModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, OhmegaCommon.MODID, helper);
    }

    @Override
    protected void registerModels() {
        basicItem(OhmegaItems.ANGEL_RING.get());
    }
}
