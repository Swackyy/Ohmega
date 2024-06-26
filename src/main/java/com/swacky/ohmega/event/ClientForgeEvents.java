package com.swacky.ohmega.event;

import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.events.AccessoryUseEvent;
import com.swacky.ohmega.client.screen.AccessoryInventoryButton;
import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.ModBinds;
import com.swacky.ohmega.network.C2S.UseAccessoryKbPacket;
import com.swacky.ohmega.network.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Ohmega.MODID, value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void addToScreens(ScreenEvent.InitScreenEvent.Post event) {
        if(event.getScreen() instanceof AccessoryInventoryScreen || event.getScreen() instanceof InventoryScreen) {
            final Minecraft mc = event.getScreen().getMinecraft();
            if(mc.player != null && !mc.player.isCreative()) {
                event.addListener(new AccessoryInventoryButton((AbstractContainerScreen<?>) event.getScreen(), 0, 0, 132, 61, 19));
            }
        }
    }

    @SubscribeEvent
    public static void hide(ScreenEvent.DrawScreenEvent.Pre event) {
        if(event.getScreen() instanceof InventoryScreen scr) {
            for (GuiEventListener list : scr.children()) {
                if (list instanceof AccessoryInventoryButton btn) {
                    btn.visible = !scr.getRecipeBookComponent().isVisible();
                }
            }
        }
    }

    private static final boolean[] down = new boolean[3];

    // Handles the accessory use kb packets
    @SuppressWarnings("PointlessArithmeticExpression")
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.screen == null) {
            if(ModBinds.UTILITY_0.isDown() && !down[0]) {
                down[0] = true;

                // Client Handling
                if(mc.player != null) {
                    mc.player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                        ItemStack stack = a.getStackInSlot(0 + 3);
                        if(stack.getItem() instanceof IAccessory acc) {
                            AccessoryUseEvent event0 = OhmegaHooks.accessoryUseEvent(mc.player, stack);
                            if(!event0.isCanceled()) {
                                acc.onUse(mc.player, stack);
                            }
                        }
                    });
                }
                // Server Handling
                ModNetworking.sendToServer(new UseAccessoryKbPacket(0));
            } else if(!ModBinds.UTILITY_0.isDown()) {
                down[0] = false;
            }

            if(ModBinds.UTILITY_1.isDown() && !down[1]) {
                down[1] = true;

                // Client Handling
                if(mc.player != null) {
                    mc.player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                        ItemStack stack = a.getStackInSlot(1 + 3);
                        if(stack.getItem() instanceof IAccessory acc) {
                            AccessoryUseEvent event0 = OhmegaHooks.accessoryUseEvent(mc.player, stack);
                            if(!event0.isCanceled()) {
                                acc.onUse(mc.player, stack);
                            }
                        }
                    });
                }
                // Server Handling
                ModNetworking.sendToServer(new UseAccessoryKbPacket(1));
            } else if(!ModBinds.UTILITY_1.isDown()) {
                down[1] = false;
            }

            if(ModBinds.SPECIAL.isDown() && !down[2]) {
                down[2] = true;

                // Client Handling
                if(mc.player != null) {
                    mc.player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                        ItemStack stack = a.getStackInSlot(2 + 3);
                        if(stack.getItem() instanceof IAccessory acc) {
                            AccessoryUseEvent event0 = OhmegaHooks.accessoryUseEvent(mc.player, stack);
                            if(!event0.isCanceled()) {
                                acc.onUse(mc.player, stack);
                            }
                        }
                    });
                }
                // Server Handling
                ModNetworking.sendToServer(new UseAccessoryKbPacket(2));
            } else if(!ModBinds.SPECIAL.isDown()) {
                down[2] = false;
            }
        }
    }
}
