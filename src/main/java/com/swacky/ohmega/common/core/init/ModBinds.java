package com.swacky.ohmega.common.core.init;

import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModBinds {
    public static final String CATEGORY = "key.category." + Ohmega.MODID + ".ohmega";

    public static final KeyMapping UTILITY_0 = new KeyMapping("key." + Ohmega.MODID + ".utility_0", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping UTILITY_1 = new KeyMapping("key." + Ohmega.MODID + ".utility_1", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping SPECIAL = new KeyMapping("key." + Ohmega.MODID + ".special", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);

    public static void register(){
        ClientRegistry.registerKeyBinding(UTILITY_0);
        ClientRegistry.registerKeyBinding(UTILITY_1);
        ClientRegistry.registerKeyBinding(SPECIAL);
    }
}
