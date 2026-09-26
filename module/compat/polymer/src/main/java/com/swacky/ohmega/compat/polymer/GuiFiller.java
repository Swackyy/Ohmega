package com.swacky.ohmega.compat.polymer;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public final class GuiFiller {
    public static final GuiElement BACKGROUND = new GuiElementBuilder(Items.STAINED_GLASS_PANE.white()).setItemName(Component.empty()).build();
}
