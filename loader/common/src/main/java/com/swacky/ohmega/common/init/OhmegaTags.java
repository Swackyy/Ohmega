package com.swacky.ohmega.common.init;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * <strong>Mostly for internal use</strong>
 * <p>
 * Dynamically creates tags matching each registered {@link AccessoryType}
 */
public final class OhmegaTags {
    private static ImmutableMap<AccessoryType, TagKey<Item>> TAG_MAP = ImmutableMap.of();

    public static void refresh() {
        ImmutableSet<AccessoryType> types = AccessoryTypeManager.getInstance().getTypes();
        ImmutableMap.Builder<AccessoryType, TagKey<Item>> builder = ImmutableMap.builderWithExpectedSize(types.size());

        for (AccessoryType type : types) {
            builder.put(type, TagKey.create(Registries.ITEM, type.getId()));
        }

        TAG_MAP = builder.build();
    }

    public static ImmutableMap<AccessoryType, TagKey<Item>> getTags() {
        return TAG_MAP;
    }

    /**
     * Do not use this in data generation, it will return null
     */
    public static TagKey<Item> get(AccessoryType type) {
        return TAG_MAP.get(type);
    }

    /**
     * Use this for data generation, it does not do any validation that the tag exists but it is the easiest way
     */
    public static TagKey<Item> get(ResourceLocation location) {
        return TagKey.create(Registries.ITEM, location);
    }
}
