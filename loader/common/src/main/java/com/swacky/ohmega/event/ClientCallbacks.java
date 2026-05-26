package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IEntityRenderingExtension;
import com.swacky.ohmega.api.client.screen.IEntityRenderingScreen;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.util.LazySavedValue;
import com.swacky.ohmega.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.client.screen.EditUiScreen;
import com.swacky.ohmega.client.screen.widget.FlipEntityButton;
import com.swacky.ohmega.client.screen.widget.ToggleExtensionButton;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.item.Accessory;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.ReloadDataPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class ClientCallbacks {
    public static AccessoryRenderStateData createRenderStateData(LivingEntity entity) {
        AccessoryData data = AccessoryHelper.getData(entity);

        return new AccessoryRenderStateData(data.getStacks(), data.getHidden());
    }

    public static List<Rect2i> getJeiAvoidRects(AbstractContainerScreen<?> screen) {
        if (screen instanceof IAccessoryScreen accessoryScreen) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null && extension.isVisible()) {
                List<Rect2i> rects = null;

                if (screen.showsActiveEffects()) {
                    Minecraft minecraft = Minecraft.getInstance();
                    LocalPlayer player = minecraft.player;

                    if (player != null) {
                        Collection<MobEffectInstance> activeEffects = player.getActiveEffects();

                        if (!activeEffects.isEmpty()) {
                            int height;
                            rects = new ArrayList<>(3);

                            if (activeEffects.size() > 5) {
                                height = 132 / (activeEffects.size() - 1);
                            } else {
                                height = 33;
                            }

                            rects.add(new Rect2i(
                                    screen.leftPos + screen.imageWidth + extension.getExtraWidthRight() + 2,
                                    screen.topPos,
                                    32,
                                    height * activeEffects.size()));
                        }
                    }
                }

                if (rects == null) {
                    rects = new ArrayList<>(2);
                }

                OhmegaConfig.Client.Service.ButtonStyle buttonStyle = OhmegaConfig.Client.getData().buttonStyle().get();
                IntIntPair buttonPosition = accessoryScreen.getAccessoryExtensionToggleButtonPosition(buttonStyle);

                rects.add(new Rect2i(
                        screen.leftPos + buttonPosition.firstInt(),
                        screen.topPos + buttonPosition.secondInt(),
                        buttonStyle.width,
                        buttonStyle.height));
                rects.add(new Rect2i(
                        screen.leftPos + accessoryScreen.getAccessoryExtensionX().get(),
                        screen.topPos + accessoryScreen.getAccessoryExtensionY().get(),
                        extension.getWidth(),
                        extension.getHeight()));
                return rects;
            }
        }

        return List.of();
    }

    public static void onClientConfigReload() {
        OhmegaConfig.Client.getData().pull();

        if (!OhmegaConfig.Client.getData().compatibilityMode().get()) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (player != null && mc.screen instanceof IAccessoryScreen) {
                mc.execute(() -> mc.setScreen(null));

                AbstractContainerMenu menu = player.containerMenu;

                if (menu != mc.player.inventoryMenu) {
                    player.connection.send(new ServerboundContainerClosePacket(menu.containerId));
                }
            }
        }
    }

    public static boolean onKeyPressedInMenu(AbstractContainerScreen<?> screen, KeyEvent event) {
        if (OhmegaBinds.OPEN_ACC_INV.matches(event)) {
            if (screen instanceof IAccessoryScreen accessoryScreen) {
                AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

                if (extension != null) {
                    extension.setVisible(false);
                }
            }

            return true;
        }

        return false;
    }

    public static void onItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (Accessories.isBound(stack.getItem())) {
            Component component = AccessoryHelper.getTypeTooltip(stack.getItem());

            if (component != null) {
                tooltip.add(component);
            }
        }
    }

    public static void onJoinWorld(Minecraft mc) {
        LazySavedValue<Boolean> option = OhmegaConfig.Client.getData().showTranslationToast();

        if (option.get()) {
            mc.getToastManager().addToast(SystemToast.multiline(
                    mc,
                    new SystemToast.SystemToastId(7500),
                    Component.translatable("toast.ohmega.translation.title"),
                    Component.translatable("toast.ohmega.translation.message")
            ));

            option.set(false);
            option.serialise();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static void onKeyInput(KeyEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player != null) {
            Screen screen = mc.screen;

            if ((screen == null || !(screen.getFocused() instanceof EditBox)) && OhmegaBinds.OPEN_EDIT_UI.matches(event) && !(screen instanceof EditUiScreen)) {
                mc.setScreen(new EditUiScreen(screen, player));
            }

            if (screen == null) {
                while (OhmegaBinds.OPEN_ACC_INV.consumeClick() && (player.portalProcess == null || !player.portalProcess.isInsidePortalThisTick())) {
                    if (mc.gameMode != null && mc.gameMode.isServerControlledInventory()) {
                        player.sendOpenInventory();
                    } else {
                        if (player.hasInfiniteMaterials()) {
                            screen = new CreativeModeInventoryScreen(player, player.connection.enabledFeatures(), mc.options.operatorItemsTab().get());
                        } else {
                            screen = new InventoryScreen(player);
                        }

                        mc.setScreen(screen);

                        AccessoryScreenExtension extension = ((IAccessoryScreen) screen).getAccessoryExtension();

                        if (extension != null) {
                            extension.setVisible(true);
                        }
                    }
                }

                List<KeyMapping> mappings = OhmegaBinds.getMappings();
                Set<AccessoryType> keyboundSlotTypes = AccessoryHelper.getKeyboundSlotTypes();
                ImmutableList<AccessoryType> slotTypes = AccessoryHelper.getSlotTypes();

                if (mappings.isEmpty() || keyboundSlotTypes.isEmpty() || slotTypes.isEmpty()) {
                    return;
                }

                AccessoryData data = AccessoryHelper.getData(player);

                // Never ever touch this again; wrote 2 months ago, I now consider it dark magic.
                for (int i = 0; i < OhmegaBinds.size(); i++) {
                    KeyMapping mapping = mappings.get(i);
                    int j = 0;

                    if (mapping.consumeClick()) {
                        for (int k = 1; true; j++) {
                            if (keyboundSlotTypes.contains(slotTypes.get(j)) && k++ > i) {
                                break;
                            }
                        }

                        ItemStack stack = data.getStackInSlot(j);
                        Accessory accessory = Accessories.get(stack.getItem());

                        if (accessory != null) {
                            // Client handling
                            accessory.onKeybindUse(player, stack);

                            // Server handling
                            OhmegaNetworking.C2S.send(new UseAccessoryPacket(j));
                        }
                    }
                }
            }
        }
    }

    // Post an event here
    public static void onPostScreenInit(Screen screen, Consumer<AbstractWidget> consumer) {
        if (screen instanceof IAccessoryScreen accessoryScreen) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null) {
                OhmegaConfig.Client.Service.ButtonStyle style = OhmegaConfig.Client.getData().buttonStyle().get();
                AbstractContainerScreen<?> containerScreen = extension.getScreen();

                if (style != OhmegaConfig.Client.Service.ButtonStyle.HIDDEN) {
                    consumer.accept(new ToggleExtensionButton(containerScreen, extension, style));
                }

                if (screen instanceof IEntityRenderingScreen entityRenderingScreen && extension instanceof IEntityRenderingExtension entityRenderingExtension) {
                    IntIntPair pair = entityRenderingScreen.getFlipEntityButtonPosition();

                    consumer.accept(new FlipEntityButton(
                            containerScreen,
                            entityRenderingExtension,
                            pair.firstInt(),
                            pair.secondInt()));
                }

                extension.initExtension(new AccessoryScreenExtension.WidgetAdder(consumer, extension.getOverlayWidgets()));

                for (AbstractWidget widget : extension.getOverlayWidgets()) {
                    screen.children.add(widget);
                    screen.narratables.add(widget);
                }
            }
        }
    }

    public static void onServerConfigReload(Runnable loadFunction) {
        AccessoryTypeManager.runDeferredAwaitingConfigLoad();

        if (OhmegaConfig.Client.isLoaded()) {
            if (AccessoryTypeManager.getTypes().isEmpty()) {
                AccessoryTypeManager.deferApply(() -> ClientCallbacks.reloadRegisteredKeybinds(loadFunction));
            } else {
                reloadRegisteredKeybinds(loadFunction);
            }

            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (player != null) {
                if (mc.screen instanceof IAccessoryScreen) {
                    mc.execute(() -> mc.setScreen(null));

                    AbstractContainerMenu menu = player.containerMenu;

                    if (menu != mc.player.inventoryMenu) {
                        player.connection.send(new ServerboundContainerClosePacket(menu.containerId));
                    }
                }

                AccessoryHelper.getData(player).reload(player);
                OhmegaNetworking.C2S.send(ReloadDataPacket.INSTANCE);
            }
        }
    }

    public static void onServerConfigUnload(Runnable loadFunction) {
        AccessoryTypeManager.clear();

        Options options = Minecraft.getInstance().options;
        options.keyMappings = Arrays.stream(options.keyMappings).filter(v -> !OhmegaBinds.isInstance(v)).toList().toArray(new KeyMapping[0]);

        loadFunction.run();
    }

    public static boolean preventRender(LivingEntityRenderState state) {
        AccessoryRenderStateData data = AccessoryRenderStateData.getData(state);

        if (data != null) {
            for (ItemStack stack : data.stacks()) {
                // todo: optimise this by caching it somehow
                if (AccessoryRenderers.isNoRender(Accessories.get(stack.getItem()), state.entityType)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void reloadRegisteredKeybinds(Runnable loadFunction) {
        ArrayList<KeyMapping> list = new ArrayList<>();

        OhmegaBinds.reloadSlotKeys();

        for (List<KeyMapping> immutableList : OhmegaBinds.getSlotKeys().values()) {
            list.addAll(immutableList);
        }

        Options options = Minecraft.getInstance().options;
        options.keyMappings = ArrayUtils.addAll(Arrays.stream(options.keyMappings).filter(v -> !OhmegaBinds.isInstance(v)).toList().toArray(new KeyMapping[0]), list.toArray(new KeyMapping[0]));

        loadFunction.run();
    }
}
