package com.swacky.ohmega.common.core.init;

import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModBinds {
    public static final String CATEGORY = "key.category." + Ohmega.MODID + ".ohmega";

    public static final KeyMapping UTILITY_0 = register("utility_0", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping UTILITY_1 = register("utility_1", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping SPECIAL = register("special", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);

    private static KeyMapping register(String bind, KeyConflictContext context, InputConstants.Type inputType, int key, String category) {
        KeyMapping mapping = new KeyMapping("key." + Ohmega.MODID + "." + bind, context, inputType, key, category);
        ClientRegistry.registerKeyBinding(mapping);
        return mapping;
    }

    public static void register(){}
}
