package com.swacky.ohmega.api.client.screen;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;

/**
 * Implemented by {@link Screen}s to allow them to express screen-in-screen behaviour
 */
public interface IEmbeddingScreen {
    /**
     * Get the screen embedded within the class implementing this.
     * Can be implemented recursively if needed
     * @return the lowest depth embedded screen
     */
    @Nullable Screen getEmbeddedScreen();

    /**
     * Determines whether {@link Gui#setScreen(Screen)} will be allowed to proceed.
     * Useful in blocking unwanted screen changes when calling {@link Screen#init()}, however this is not always needed
     * @return {@code true} to allow screen setting, {@code false} to prohibit it
     */
    boolean shouldAllowSetScreen();
}
