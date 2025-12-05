package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.client.screen.AccessoryInventoryButton;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeCapPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryKbPacket;
import com.swacky.ohmega.network.ModNetworking;
import com.swacky.ohmega.common.OhmegaCommon;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.BOTH, modid = OhmegaCommon.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void addToScreens(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen && OhmegaConfig.CONFIG_CLIENT.buttonStyle.get() != OhmegaConfig.ButtonStyle.HIDDEN) {
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
                    btn.visible = !scr.recipeBookComponent.isVisible();
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
                    ModNetworking.sendToServer(OpenAccessoryInventoryPacket.INSTANCE);
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
            for (int[] i = {0}; i[0] < OhmegaBinds.Generated.size(); i[0]++) {
                int[] j = {0};
                KeyMapping mapping = mappings.get(i[0]);

                if (mapping.consumeClick()) {
                    // Client handling
                    if (mc.player != null) {
                        AccessoryHelper.getContainer(mc.player).ifPresent(a -> {
                            for (int k = 0; true; j[0]++) {
                                if (AccessoryHelper.getKeyboundSlotTypesStr().contains(slotTypes.get(j[0])) && ++k > i[0]) {
                                    break;
                                }
                            }


                            ItemStack stack = a.getStackInSlot(j[0]);

                            IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                            if (acc != null && !OhmegaHooks.accessoryUseEvent(mc.player, stack)) {
                                acc.onUse(mc.player, stack);
                            }
                        });
                    }

                    // Server handling
                    ModNetworking.sendToServer(new UseAccessoryKbPacket(j[0]));
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerKbs(RegisterKeyMappingsEvent event) {
        event.register(OhmegaBinds.OPEN_ACC_INV);
    }

    @SuppressWarnings("RedundantCast")
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(OhmegaMenus.ACCESSORY_INVENTORY.get(), (MenuScreens.ScreenConstructor<AccessoryInventoryMenu, AccessoryInventoryScreen>) AccessoryInventoryScreen::new));
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == OhmegaConfig.SPEC_SERVER) {
            ArrayList<KeyMapping> list = new ArrayList<>();
            for (ImmutableList<KeyMapping> immutableList : OhmegaBinds.Generated.getSlotKeys().values()) {
                list.addAll(immutableList);
            }

            Minecraft mc = Minecraft.getInstance();
            mc.options.keyMappings = ArrayUtils.addAll(Arrays.stream(mc.options.keyMappings).filter(v -> !(v instanceof OhmegaBinds.OhmegaKeyMapping)).toList().toArray(new KeyMapping[0]), list.toArray(new KeyMapping[0]));
            mc.options.load(true);
        }
    }

    @SubscribeEvent
    public static void onConfigUnload(ModConfigEvent.Unloading event) {
        if (event.getConfig().getSpec() == OhmegaConfig.SPEC_SERVER) {
            Minecraft mc = Minecraft.getInstance();
            mc.options.keyMappings = Arrays.stream(mc.options.keyMappings).filter(v -> !(v instanceof OhmegaBinds.OhmegaKeyMapping)).toList().toArray(new KeyMapping[0]);
            mc.options.load(true);
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (OhmegaConfig.SPEC_SERVER.isLoaded()) {
            Minecraft mc = Minecraft.getInstance();
            if (event.getConfig().getSpec() == OhmegaConfig.SPEC_CLIENT && !OhmegaConfig.CONFIG_CLIENT.compatibilityMode.get()) {
                if (mc.player != null && mc.player.containerMenu instanceof AccessoryInventoryMenu) {
                    ModNetworking.sendToServer(OpenAccessoryInventoryPacket.INSTANCE);
                }
            } else if (event.getConfig().getSpec() == OhmegaConfig.SPEC_SERVER) {
                ArrayList<KeyMapping> list = new ArrayList<>();
                for (ImmutableList<KeyMapping> immutableList : OhmegaBinds.Generated.getSlotKeys().values()) {
                    list.addAll(immutableList);
                }

                mc.options.keyMappings = ArrayUtils.addAll(Arrays.stream(mc.options.keyMappings).filter(v -> !(v instanceof OhmegaBinds.OhmegaKeyMapping)).toList().toArray(new KeyMapping[0]), list.toArray(new KeyMapping[0]));
                mc.options.load(true);

                if (mc.player != null) {
                    AccessoryHelper.getContainer(mc.player).ifPresent(AccessoryContainer::reloadCfg);
                    ModNetworking.sendToServer(ResizeCapPacket.INSTANCE);

                    if (!OhmegaConfig.CONFIG_CLIENT.compatibilityMode.get() && mc.player.containerMenu instanceof AccessoryInventoryMenu) {
                        mc.screen = null;
                        mc.player.connection.send(new ServerboundContainerClosePacket(mc.player.containerMenu.containerId));
                        ModNetworking.sendToServer(OpenAccessoryInventoryPacket.INSTANCE);
                    }
                }
            }
        }
    }
}
