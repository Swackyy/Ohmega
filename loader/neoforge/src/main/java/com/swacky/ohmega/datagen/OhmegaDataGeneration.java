package com.swacky.ohmega.datagen;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.datagen.client.OhmegaEnUsProvider;
import com.swacky.ohmega.datagen.client.OhmegaModelProvider;
import com.swacky.ohmega.datagen.server.OhmegaAccessoryTypeProvider;
import com.swacky.ohmega.datagen.server.OhmegaTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = OhmegaCommon.MODID)
public final class OhmegaDataGeneration {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(true, new OhmegaModelProvider(output));
        generator.addProvider(true, new OhmegaEnUsProvider(output));

        // Server
        generator.addProvider(true, new OhmegaAccessoryTypeProvider(output));
        generator.addProvider(true, new OhmegaTagsProvider(output, event.getLookupProvider()));
    }
}
