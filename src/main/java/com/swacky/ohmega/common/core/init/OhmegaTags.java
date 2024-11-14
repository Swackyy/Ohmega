package com.swacky.ohmega.common.core.init;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;

/**
 * <strong>Mostly for internal use</strong>
 * Dynamically creates tags matching each registered {@link AccessoryType}
 */
public class OhmegaTags {
    private static final ArrayList<TagHolder> TAGS = new ArrayList<>();

    public static void register() {
        ImmutableList<AccessoryType> types = AccessoryTypeManager.getInstance().getTypes();
        ArrayList<TagHolder> builder = new ArrayList<>(types.size());

        for (AccessoryType type : types) {
            builder.add(new TagHolder(type, register(type.getId())));
        }

        TAGS.clear();
        TAGS.addAll(builder);
    }

    private static TagKey<Item> register(ResourceLocation location) {
        return TagKey.create(Registries.ITEM, location);
    }

    public static ArrayList<TagHolder> getTags() {
        return TAGS;
    }

    public static TagHolder get(ResourceLocation location) {
        for (TagHolder holder : TAGS) {
            if (holder.getType().getId().equals(location)) {
                return holder;
            }
        }
        return null;
    }

    public static boolean existsAt(ResourceLocation location) {
        return get(location) != null;
    }

    public static boolean existsAt(String str) {
        ResourceLocation rl = ResourceLocation.tryParse(str);
        if (rl != null) {
            return existsAt(rl);
        }
        return false;
    }

    public static class TagHolder {
        private final AccessoryType type;
        private final TagKey<Item> tag;

        private TagHolder(AccessoryType type, TagKey<Item> tag) {
            this.type = type;
            this.tag = tag;
        }

        public AccessoryType getType() {
            return this.type;
        }

        public TagKey<Item> getTag() {
            return this.tag;
        }
    }
}
