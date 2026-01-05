package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;

/**
 * <strong>Mostly for internal use</strong>
 * <p>
 * Dynamically creates tags matching each registered {@link AccessoryType}
 */
public final class OhmegaTags {
    private static final ArrayList<TagHolder> TAGS = new ArrayList<>();

    public static void refresh() {
        TAGS.clear();

        for (AccessoryType type : AccessoryTypeManager.getInstance().getTypes()) {
            TAGS.add(new TagHolder(type, register(type.getId())));
        }
    }

    private static TagKey<Item> register(ResourceLocation id) {
        return TagKey.create(Registries.ITEM, id);
    }

    public static ArrayList<TagHolder> getTags() {
        return TAGS;
    }

    public static TagHolder get(ResourceLocation id) {
        for (TagHolder holder : TAGS) {
            if (holder.getType().getId().equals(id)) {
                return holder;
            }
        }

        return null;
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
