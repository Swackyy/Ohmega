package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.AccessoryUseEvent;
import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.OhmegaBinds;
import com.swacky.ohmega.client.screen.AccessoryInventoryButton;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryKbPacket;
import com.swacky.ohmega.network.ModNetworking;
import net.minecraft.client.KeyMapping;
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
    public static void addToScreens(ScreenEvent.Init.Post event) {
        if ((event.getScreen() instanceof AccessoryInventoryScreen || event.getScreen() instanceof InventoryScreen) && OhmegaConfig.CONFIG_CLIENT.buttonStyle.get() != OhmegaConfig.ButtonStyle.HIDDEN) {
            final Minecraft mc = event.getScreen().getMinecraft();
            if (mc != null && mc.player != null && !mc.player.isCreative() && !mc.player.isSpectator()) {
                event.addListener(new AccessoryInventoryButton(OhmegaConfig.CONFIG_CLIENT.buttonStyle.get(), (AbstractContainerScreen<?>) event.getScreen()));
            }
        }
    }

    @SubscribeEvent
    public static void hide(ScreenEvent.Render.Pre event) {
        if (event.getScreen() instanceof InventoryScreen scr) {
            for (GuiEventListener list : scr.children()) {
                if (list instanceof AccessoryInventoryButton btn) {
                    btn.visible = !scr.getRecipeBookComponent().isVisible();
                }
            }
        }
    }

    // Handles the accessory use key-bind packets
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null) {
            while (OhmegaBinds.OPEN_ACC_INV.consumeClick() && mc.player != null) {
                if (mc.gameMode != null && mc.gameMode.isServerControlledInventory()) {
                    mc.player.sendOpenInventory();
                } else if (!mc.player.isCreative() && !mc.player.isSpectator()) {
                    ModNetworking.sendToServer(new OpenAccessoryInventoryPacket());
                } else {
                    mc.setScreen(new InventoryScreen(mc.player));
                }
            }

            ImmutableList<KeyMapping> mappings = OhmegaBinds.Generated.getMappings();
            ImmutableList<String> slotTypes = AccessoryHelper.getSlotTypesStr();
            // Never ever touch this again; wrote 2 months ago, I now consider it dark magic.
            for (int[] i = {0}; i[0] < OhmegaBinds.Generated.size(); i[0]++) {
                int[] j = {0};
                KeyMapping mapping = mappings.get(i[0]);
                if (mapping.consumeClick()) {
                    // Client handling
                    if (mc.player != null) {
                        mc.player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                            int k = 0;
                            for (; true; j[0]++) {
                                if (AccessoryHelper.getKeyboundSlotTypesStr().contains(slotTypes.get(j[0]))) {
                                    k++;
                                    if (k > i[0]) {
                                        break;
                                    }
                                }
                            }

                            ItemStack stack = a.getStackInSlot(j[0]);

                            IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                            if (acc != null) {
                                AccessoryUseEvent event0 = OhmegaHooks.accessoryUseEvent(mc.player, stack);
                                if (!event0.isCanceled()) {
                                    acc.onUse(mc.player, stack);
                                }
                            }
                        });
                    }

                    // Server handling
                    ModNetworking.sendToServer(new UseAccessoryKbPacket(j[0]));
                }
            }
        }
    }
}
