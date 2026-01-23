package com.swacky.ohmega.datagen;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.datagen.client.OhmegaEnUsProvider;
import com.swacky.ohmega.datagen.client.OhmegaModelProvider;
import com.swacky.ohmega.datagen.server.OhmegaAccessoryTypeProvider;
import com.swacky.ohmega.datagen.server.OhmegaTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = OhmegaCommon.MODID)
public final class OhmegaDataGeneration {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(true, new OhmegaModelProvider(output, helper));
        generator.addProvider(true, new OhmegaEnUsProvider(output));

        // Server
        generator.addProvider(true, new OhmegaAccessoryTypeProvider(output));

        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        BlockTagsProvider blockTags = new BlockTagsProvider(output, lookupProvider, OhmegaCommon.MODID, helper) {
            @Override
            protected void addTags(HolderLookup.@NonNull Provider provider) {}
        };

        generator.addProvider(true, blockTags);
        generator.addProvider(true, new OhmegaTagsProvider(output, lookupProvider,  blockTags.contentsGetter(), helper));
    }
}
