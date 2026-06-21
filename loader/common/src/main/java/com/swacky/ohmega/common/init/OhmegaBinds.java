package com.swacky.ohmega.common.init;

import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// todo: register new keybinds on forge and neo
public final class OhmegaBinds {
    private static final Service INST = OhmegaClient.loadService(Service.class);

    public static void bootstrap() {}

    public static boolean isInstance(KeyMapping other) {
        return INST.isInstance(other);
    }

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Ohmega.id(Ohmega.MODID));

    public static final KeyMapping EDIT_MAGNETICS = key("edit_magnetics", GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyMapping EDIT_NUDGE_DOWN = key("edit_nudge_down", GLFW.GLFW_KEY_DOWN);
    public static final KeyMapping EDIT_NUDGE_LEFT = key("edit_nudge_left", GLFW.GLFW_KEY_LEFT);
    public static final KeyMapping EDIT_NUDGE_RIGHT = key("edit_nudge_right", GLFW.GLFW_KEY_RIGHT);
    public static final KeyMapping EDIT_NUDGE_UP = key("edit_nudge_up", GLFW.GLFW_KEY_UP);
    public static final KeyMapping EDIT_REDO = key("edit_redo", GLFW.GLFW_KEY_Y);
    public static final KeyMapping EDIT_SHOW_LINES = key("edit_show_lines", GLFW.GLFW_KEY_LEFT_SHIFT);
    public static final KeyMapping EDIT_UNDO = key("edit_undo", GLFW.GLFW_KEY_Z);
    public static final KeyMapping OPEN_ACCESSORY_INVENTORY = key("open_accessory_inventory", GLFW.GLFW_KEY_UNKNOWN);
    public static final KeyMapping OPEN_EDIT_UI = key("open_edit_ui", GLFW.GLFW_KEY_LEFT_BRACKET);

    private static Map<AccessoryType, List<KeyMapping>> SLOT_KEYS = Map.of();
    private static List<KeyMapping> ORDERED_SLOT_KEYS = List.of();

    private static KeyMapping key(String key, int defaultKey) {
        return new KeyMapping("key." + Ohmega.MODID + '.' + key, InputConstants.Type.KEYSYM, defaultKey, CATEGORY);
    }

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

        if (OhmegaConfig.Server.getData().disableAccessoryTypes().get()) {
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
