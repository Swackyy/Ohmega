package com.swacky.ohmega.common.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class OhmegaBinds {
    private static final Service INST = OhmegaClient.loadService(Service.class);

    public static void bootstrap() {}

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Ohmega.id(Ohmega.MODID));

    public static final KeyMapping OPEN_ACC_INV = new KeyMapping("key." + Ohmega.MODID + ".open_acc_inv", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);

    public static boolean isInstance(KeyMapping other) {
        return INST.isInstance(other);
    }

    // Inner class to defer loading
    public static class Generated {
        private static ImmutableMap<AccessoryType, ImmutableList<KeyMapping>> SLOT_KEYS = ImmutableMap.of();
        private static List<KeyMapping> ORDERED_SLOT_KEYS = List.of();

        private static ImmutableMap<AccessoryType, ImmutableList<KeyMapping>> createSlotKeys() {
            Generated.ORDERED_SLOT_KEYS = new ArrayList<>();

            ImmutableList<AccessoryType> keyBoundSlotTypes = AccessoryHelper.getKeyboundSlotTypes();
            WeakHashMap<AccessoryType, ImmutableList.Builder<KeyMapping>> builder = new WeakHashMap<>(keyBoundSlotTypes.size());
            Map<AccessoryType, Integer> typeCountMap = new WeakHashMap<>();

            if (OhmegaConfig.Server.disableAccessoryTypes()) {
                typeCountMap.put(AccessoryType.GENERIC.get(), 0);
            } else {
                for (AccessoryType type : AccessoryTypeManager.getTypes()) {
                    typeCountMap.put(type, 0);
                }
            }

            for (AccessoryType slotType : AccessoryHelper.getSlotTypes()) {
                for (AccessoryType keyboundType : keyBoundSlotTypes) {
                    if (keyboundType == slotType) {
                        int count = typeCountMap.get(keyboundType);
                        // Default bindings in ternary:
                        // Utility 1: G
                        // Utility 2: V
                        // Special 1: B
                        int key =
                                keyboundType == AccessoryType.UTILITY.get() ?
                                        count == 0 ? GLFW.GLFW_KEY_G :
                                        count == 1 ? GLFW.GLFW_KEY_V :
                                        GLFW.GLFW_KEY_UNKNOWN :
                                keyboundType == AccessoryType.SPECIAL.get() &&
                                        count == 0 ? GLFW.GLFW_KEY_B :
                                        GLFW.GLFW_KEY_UNKNOWN;

                        builder.computeIfAbsent(keyboundType, k -> new ImmutableList.Builder<>());

                        Identifier id = keyboundType.getId();
                        KeyMapping mapping = INST.createMapping("key." + id.getNamespace() + "." + id.getPath() + "_" + count, key);

                        builder.get(keyboundType).add(mapping);
                        Generated.ORDERED_SLOT_KEYS.add(mapping);
                        typeCountMap.put(keyboundType, count + 1);
                        break;
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
            return Generated.SLOT_KEYS = createSlotKeys();
        }

        public static KeyMapping getMapping(AccessoryType type, int index) {
            ImmutableList<KeyMapping> list = Generated.SLOT_KEYS.get(type);

            if (list != null) {
                return list.get(index);
            }

            return null;
        }

        public static ImmutableList<KeyMapping> getMappings() {
            return ImmutableList.copyOf(Generated.ORDERED_SLOT_KEYS);
        }

        public static int size() {
            int size = 0;

            for (ImmutableList<KeyMapping> list : Generated.SLOT_KEYS.values()) {
                size += list.size();
            }

            return size;
        }
    }

    public interface Service {
        KeyMapping createMapping(String name, int key);

        boolean isInstance(KeyMapping other);
    }
}
