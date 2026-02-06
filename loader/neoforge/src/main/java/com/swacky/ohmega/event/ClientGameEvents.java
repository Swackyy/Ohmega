package com.swacky.ohmega.event;

import com.swacky.ohmega.common.OhmegaCommon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = OhmegaCommon.MODID, value = Dist.CLIENT)
public final class ClientGameEvents {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ClientCallbacks.onItemTooltip(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        ClientCallbacks.onKeyInput();
    }

    @SubscribeEvent
    public static void onPostScreenInit(ScreenEvent.Init.Post event) {
        ClientCallbacks.onPostScreenInit(event.getScreen(), event::addListener);
    }
}
