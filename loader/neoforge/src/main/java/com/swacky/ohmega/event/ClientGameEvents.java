package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.client.screen.AccessoryInventoryButton;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryKbPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = OhmegaCommon.MODID, value = Dist.CLIENT)
public class ClientGameEvents {
    @SubscribeEvent
    public static void addToScreens(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen && OhmegaConfig.CONFIG_CLIENT.buttonStyle.get() != OhmegaConfig.ButtonStyle.HIDDEN) {
            final Minecraft mc = event.getScreen().getMinecraft();
            if (mc.player != null && !mc.player.isCreative() && !mc.player.isSpectator()) {
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
                    PacketDistributor.sendToServer(new OpenAccessoryInventoryPacket());
                } else {
                    mc.setScreen(new InventoryScreen(mc.player));
                }
            }

            ImmutableList<KeyMapping> mappings = OhmegaBinds.Generated.getMappings();
            ImmutableList<String> slotTypes = AccessoryHelper.getSlotTypesStr();
            if (mappings.isEmpty() || slotTypes.isEmpty()) {
                return;
            }

            // Never ever touch this again; wrote 2 months ago, I now consider it dark magic.
            for (int i = 0; i < OhmegaBinds.Generated.size(); i++) {
                int j = 0;
                KeyMapping mapping = mappings.get(i);
                if (mapping.consumeClick()) {
                    // Client handling
                    if (mc.player != null) {
                        AccessoryContainer a = mc.player.getData(OhmegaDataAttachments.ACCESSORY_HANDLER.get());
                        int k = 0;
                        for (; true; j++) {
                            if (AccessoryHelper.getKeyboundSlotTypesStr().contains(slotTypes.get(j))) {
                                k++;
                                if (k > i) {
                                    break;
                                }
                            }
                        }

                        ItemStack stack = a.getStackInSlot(j);

                        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                        if (acc != null && !OhmegaHooks.accessoryUseEvent(mc.player, stack).isCanceled()) {
                            acc.onUse(mc.player, stack);
                        }
                    }

                    // Server handling
                    PacketDistributor.sendToServer(new UseAccessoryKbPacket(j));
                }
            }
        }
    }
}
