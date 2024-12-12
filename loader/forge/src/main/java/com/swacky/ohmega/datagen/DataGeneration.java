package com.swacky.ohmega.datagen;

import com.swacky.ohmega.datagen.client.OhmegaItemModelProvider;
import com.swacky.ohmega.datagen.client.lang.OhmegaEnUsProvider;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = OhmegaCommon.MODID, bus = Bus.MOD)
public class DataGeneration {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new OhmegaItemModelProvider(output, helper));
        generator.addProvider(event.includeClient(), new OhmegaEnUsProvider(output));
    }
}
