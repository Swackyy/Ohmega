package com.swacky.ohmega.datagen.server;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class OhmegaTagsProvider extends ItemTagsProvider {
    public OhmegaTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, ExistingFileHelper helper) {
        super(output, provider, blockTags, OhmegaCommon.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(OhmegaTags.get(AccessoryType.UTILITY_ID)).add(OhmegaItems.ANGEL_RING.get());
    }
}
