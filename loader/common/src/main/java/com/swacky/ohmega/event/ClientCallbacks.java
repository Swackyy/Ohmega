package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IEntityRenderingExtension;
import com.swacky.ohmega.api.client.screen.IEntityRenderingScreen;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.menu.IAccessoryMenu;
import com.swacky.ohmega.client.renderer.AccessoryRenderStateData;
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
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class ClientCallbacks {
    public static AccessoryRenderStateData createRenderStateData(LivingEntity entity) {
        AccessoryData data = AccessoryHelper.getData(entity);

        return new AccessoryRenderStateData(data.getStacks(), data.getHidden());
    }

    public static void onClientConfigReload() {
        if (!OhmegaConfig.Client.compatibilityMode()) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (player != null && player.containerMenu instanceof IAccessoryMenu) {
                mc.screen = new InventoryScreen(player);
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
        if (OhmegaConfig.Client.showTranslationToast()) {
            mc.getToastManager().addToast(SystemToast.multiline(
                    mc,
                    new SystemToast.SystemToastId(7500),
                    Component.translatable("toast.ohmega.translation.title"),
                    Component.translatable("toast.ohmega.translation.message")
            ));

            OhmegaConfig.Client.setShowTranslationToast(false);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static void onKeyInput() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen == null) {
            LocalPlayer player = mc.player;

            if (player != null) {
                while (OhmegaBinds.OPEN_ACC_INV.consumeClick() && (player.portalProcess == null || !player.portalProcess.isInsidePortalThisTick())) {
                    if (mc.gameMode != null && mc.gameMode.isServerControlledInventory()) {
                        player.sendOpenInventory();
                    } else {
                        Screen screen;

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
                AbstractContainerScreen<?> containerScreen = extension.getScreen();

                if (OhmegaConfig.Client.buttonStyle() != OhmegaConfig.Client.Service.ButtonStyle.HIDDEN ) {
                    consumer.accept(new ToggleExtensionButton(containerScreen, extension, OhmegaConfig.Client.buttonStyle()));
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
                AbstractContainerMenu menu = player.containerMenu;

                if (menu instanceof IAccessoryMenu) {
                    mc.execute(() -> mc.setScreen(null));

                    if (menu != mc.player.inventoryMenu) {
                        player.connection.send(new ServerboundContainerClosePacket(player.containerMenu.containerId));
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

    public static void preventRender(LivingEntityRenderState state, CallbackInfo ci) {
        AccessoryRenderStateData data = AccessoryRenderStateData.getData(state);

        if (data != null) {
            for (ItemStack stack : data.stacks()) {
                // todo: optimise this by caching it somehow
                if (AccessoryRenderers.isNoRender(Accessories.get(stack.getItem()), state.entityType)) {
                    ci.cancel();
                    return;
                }
            }
        }
    }

    public static void reloadRegisteredKeybinds(Runnable loadFunction) {
        ArrayList<KeyMapping> list = new ArrayList<>();

        for (List<KeyMapping> immutableList : OhmegaBinds.getSlotKeys().values()) {
            list.addAll(immutableList);
        }

        Options options = Minecraft.getInstance().options;
        options.keyMappings = ArrayUtils.addAll(Arrays.stream(options.keyMappings).filter(v -> !OhmegaBinds.isInstance(v)).toList().toArray(new KeyMapping[0]), list.toArray(new KeyMapping[0]));

        loadFunction.run();
    }
}
