package com.swacky.ohmega.common.init;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Dynamically creates tags matching each registered {@link AccessoryType}
 */
public final class OhmegaTags {
    private static Map<AccessoryType, TagKey<Item>> TAG_MAP = new IdentityHashMap<>();

    public static void refresh() {
        Collection<AccessoryType> types = AccessoryTypeManager.getTypes();
        Map<AccessoryType, TagKey<Item>> map = new IdentityHashMap<>(types.size());

        for (AccessoryType type : types) {
            map.put(type, TagKey.create(Registries.ITEM, type.getId()));
        }

        TAG_MAP = map;
    }

    public static Map<AccessoryType, TagKey<Item>> getTags() {
        return TAG_MAP;
    }

    /**
     * Do not use this in data generation, it will return null
     */
    public static TagKey<Item> get(AccessoryType type) {
        return TAG_MAP.get(type);
    }

    /**
     * Use this for data generation, it does not do any validation that the tag exists, but it is the easiest way
     */
    public static TagKey<Item> get(Identifier identifier) {
        return TagKey.create(Registries.ITEM, identifier);
    }
}
