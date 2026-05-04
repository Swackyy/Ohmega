package com.swacky.ohmega.common.init;

import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class OhmegaBinds {
    private static final Service INST = OhmegaClient.loadService(Service.class);

    public static void bootstrap() {}

    public static boolean isInstance(KeyMapping other) {
        return INST.isInstance(other);
    }

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Ohmega.id(Ohmega.MODID));

    // todo: make this also close the extension when pressed while it's open
    public static final KeyMapping OPEN_ACC_INV = new KeyMapping("key." + Ohmega.MODID + ".open_acc_inv", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY);

    private static Map<AccessoryType, List<KeyMapping>> SLOT_KEYS = Map.of();
    private static List<KeyMapping> ORDERED_SLOT_KEYS = List.of();

    private static void addMapping(AccessoryType type, int index, int key) {
        Identifier id = type.getId();
        KeyMapping mapping = INST.createMapping("key." + id.getNamespace() + "." + id.getPath() + "_" + index, key);

        SLOT_KEYS.computeIfAbsent(type, _ -> new ArrayList<>()).add(mapping);
        ORDERED_SLOT_KEYS.add(mapping);
    }

    public static void reloadSlotKeys() {
        Set<AccessoryType> keyBoundSlotTypes = AccessoryHelper.getKeyboundSlotTypes();
        int keyboundSize = keyBoundSlotTypes.size();
        int slotsSize = AccessoryHelper.getSlotTypes().size();
        Map<AccessoryType, Integer> typeCountMap = new IdentityHashMap<>(keyboundSize);
        SLOT_KEYS = new IdentityHashMap<>(keyboundSize);
        ORDERED_SLOT_KEYS = new ArrayList<>(slotsSize);

        if (OhmegaConfig.Server.disableAccessoryTypes()) {
            for (int i = 0; i < slotsSize; i++) {
                addMapping(AccessoryType.GENERIC.get(), i, GLFW.GLFW_KEY_UNKNOWN);
            }
        } else {
            for (AccessoryType type : AccessoryHelper.getSlotTypes()) {
                if (keyBoundSlotTypes.contains(type)) {
                    int index = typeCountMap.getOrDefault(type, 0);
                    // Default bindings in ternary:
                    // Utility 1: G
                    // Utility 2: V
                    // Special 1: B
                    int key =
                            type == AccessoryType.UTILITY.get() ?
                                    index == 0 ? GLFW.GLFW_KEY_G :
                                    index == 1 ? GLFW.GLFW_KEY_V :
                                    GLFW.GLFW_KEY_UNKNOWN :
                                    type == AccessoryType.SPECIAL.get() &&
                                            index == 0 ? GLFW.GLFW_KEY_B :
                                    GLFW.GLFW_KEY_UNKNOWN;

                    addMapping(type, index, key);
                    typeCountMap.put(type, index + 1);
                }
            }
        }
    }

    public static Map<AccessoryType, List<KeyMapping>> getSlotKeys() {
        return SLOT_KEYS;
    }

    public static KeyMapping getMapping(AccessoryType type, int index) {
        List<KeyMapping> list = SLOT_KEYS.get(type);

        if (list != null) {
            return list.get(index);
        }

        return null;
    }

    public static List<KeyMapping> getMappings() {
        return ORDERED_SLOT_KEYS;
    }

    public static int size() {
        return ORDERED_SLOT_KEYS.size();
    }

    public interface Service {
        KeyMapping createMapping(String name, int key);

        boolean isInstance(KeyMapping other);
    }
}
