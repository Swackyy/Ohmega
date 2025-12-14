package com.swacky.ohmega.common.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class OhmegaBinds {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(OhmegaCommon.id(OhmegaCommon.MODID));

    public static final KeyMapping OPEN_ACC_INV = new KeyMapping("key." + OhmegaCommon.MODID + ".open_acc_inv", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);

    // Inner class to defer loading
    public static class Generated {
        private static ImmutableMap<AccessoryType, ImmutableList<KeyMapping>> slotKeys;
        private static ArrayList<KeyMapping> orderedSlotKeys;

        private static ImmutableMap<AccessoryType, ImmutableList<KeyMapping>> createSlotKeys() {
            Generated.orderedSlotKeys = new ArrayList<>();

            ImmutableList<AccessoryType> keyBoundSlotTypes = AccessoryHelper.getKeyboundSlotTypes();

            WeakHashMap<AccessoryType, ImmutableList.Builder<KeyMapping>> builder = new WeakHashMap<>(keyBoundSlotTypes.size());
            Map<AccessoryType, Integer> typeCountMap = new WeakHashMap<>();

            if (OhmegaConfig.CONFIG_SERVER.noAccessoryTypes.get()) {
                OhmegaTags.TagHolder holder = OhmegaTags.get(OhmegaCommon.id("generic"));
                if (holder != null) {
                    typeCountMap.put(holder.getType(), 0);
                }
            } else {
                for (OhmegaTags.TagHolder holder : OhmegaTags.getTags()) {
                    typeCountMap.put(holder.getType(), 0);
                }
            }

            for (AccessoryType slotType : AccessoryHelper.getSlotTypes()) {
                for (AccessoryType type : keyBoundSlotTypes) {
                    if (type == slotType) {
                        int count = typeCountMap.get(type);
                        // Default bindings in ternary:
                        // Utility 1: G
                        // Utility 2: V
                        // Special 1: B
                        int key = type == AccessoryType.UTILITY.get() ? count == 0 ? GLFW.GLFW_KEY_G : count == 1 ? GLFW.GLFW_KEY_V : GLFW.GLFW_KEY_UNKNOWN : type == AccessoryType.SPECIAL.get() && count == 0 ? GLFW.GLFW_KEY_B : GLFW.GLFW_KEY_UNKNOWN;
                        builder.computeIfAbsent(type, k -> new ImmutableList.Builder<>());
                        Identifier id = type.getId();
                        OhmegaKeyMapping mapping = new OhmegaKeyMapping("key." + id.getNamespace() + "." + id.getPath() + "_" + count, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, key, CATEGORY);
                        builder.get(type).add(mapping);
                        Generated.orderedSlotKeys.add(mapping);
                        typeCountMap.put(type, count + 1);
                    }
                }
            }
            ImmutableMap.Builder<AccessoryType, ImmutableList<KeyMapping>> map = ImmutableMap.builderWithExpectedSize(builder.size());
            for (AccessoryType key : builder.keySet()) {
                map.put(key, builder.get(key).build());
            }

            return map.build();
        }

        public static ImmutableMap<AccessoryType, ImmutableList<KeyMapping>> getSlotKeys() {
            return Generated.slotKeys = createSlotKeys();
        }

        public static KeyMapping getMapping(AccessoryType type, int index) {
            ImmutableList<KeyMapping> list = Generated.slotKeys.get(type);
            if (list != null) {
                return list.get(index);
            }
            return null;
        }

        public static ImmutableList<KeyMapping> getMappings() {
            return ImmutableList.copyOf(Generated.orderedSlotKeys);
        }

        public static int size() {
            int runningTotal = 0;
            if (!Generated.slotKeys.isEmpty()) {
                for (ImmutableList<KeyMapping> list : Generated.slotKeys.values()) {
                    runningTotal += list.size();
                }
            }
            return runningTotal;
        }
    }

    public static class OhmegaKeyMapping extends KeyMapping {
        public OhmegaKeyMapping(String description, IKeyConflictContext conflictContext, InputConstants.Type inputType, int code, Category category) {
            super(description, conflictContext, inputType, code, category);
        }

        @Override
        public @NotNull Component getDisplayName() {
            String key = this.getName();

            return Component.translatable(key.substring(0, key.indexOf('_')), Integer.parseInt(key.substring(key.indexOf('_') + 1)) + 1);
        }
    }
}
