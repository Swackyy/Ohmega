package com.swacky.ohmega.common.init;

import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
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

    private static Map<AccessoryType, List<KeyMapping>> createSlotKeys() {
        ORDERED_SLOT_KEYS = new ArrayList<>();

        Set<AccessoryType> keyBoundSlotTypes = AccessoryHelper.getKeyboundSlotTypes();
        Map<AccessoryType, List<KeyMapping>> map = new IdentityHashMap<>(keyBoundSlotTypes.size());
        Map<AccessoryType, Integer> typeCountMap = new IdentityHashMap<>();

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

                    map.computeIfAbsent(keyboundType, _ -> new ArrayList<>());

                    Identifier id = keyboundType.getId();
                    KeyMapping mapping = INST.createMapping("key." + id.getNamespace() + "." + id.getPath() + "_" + count, key);

                    map.get(keyboundType).add(mapping);
                    ORDERED_SLOT_KEYS.add(mapping);
                    typeCountMap.put(keyboundType, count + 1);
                    break;
                }
            }
        }

        return map;
    }

    public static Map<AccessoryType, List<KeyMapping>> getSlotKeys() {
        return SLOT_KEYS = createSlotKeys();
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
        int size = 0;

        for (List<KeyMapping> list : SLOT_KEYS.values()) {
            size += list.size();
        }

        return size;
    }

    public interface Service {
        KeyMapping createMapping(String name, int key);

        boolean isInstance(KeyMapping other);
    }
}
