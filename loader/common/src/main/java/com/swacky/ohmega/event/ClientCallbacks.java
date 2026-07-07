package com.swacky.ohmega.event;

import com.mojang.brigadier.CommandDispatcher;
import com.swacky.ohmega.api.client.command.IClientCommandSource;
import com.swacky.ohmega.api.client.item.AccessoryHelperClient;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import com.swacky.ohmega.api.client.screen.IAccessoryScreen;
import com.swacky.ohmega.api.client.screen.IEntityRenderingExtension;
import com.swacky.ohmega.api.client.screen.IEntityRenderingScreen;
import com.swacky.ohmega.api.client.screen.LazyPosition;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.util.BooleanLazySavedValue;
import com.swacky.ohmega.client.command.OhmegaClientRootCommand;
import com.swacky.ohmega.client.screen.EditUiScreen;
import com.swacky.ohmega.client.screen.widget.FlipEntityButton;
import com.swacky.ohmega.client.screen.widget.ToggleExtensionButton;
import com.swacky.ohmega.common.menu.AccessorySlot;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.KeybindUsePacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
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
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class ClientCallbacks {
    public static void applyEntityInInventoryTranslation(EntityRenderState state, Quaternionf rotation) {
        if (
                AccessoryScreens.getEffectiveScreen() instanceof IAccessoryScreen accessoryScreen &&
                accessoryScreen.getAccessoryExtension() instanceof IEntityRenderingExtension extension &&
                extension.isEntityFlipped() &&
                state instanceof LivingEntityRenderState livingState) {
            livingState.bodyRot = -livingState.bodyRot;
            livingState.yRot = -livingState.yRot;

            rotation.rotationX((float) Math.PI);
        }
    }

    public static AccessoryRenderStateData createRenderStateData(LivingEntity entity) {
        AccessoryData data = OhmegaDataAttachments.getData(entity);

        return new AccessoryRenderStateData(data.getEntries());
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

                OhmegaConfig.Client.Service.ButtonStyle buttonStyle = OhmegaConfig.Client.getData().toggleExtensionButtonStyle().getObject();

                if (buttonStyle != null && buttonStyle != OhmegaConfig.Client.Service.ButtonStyle.HIDDEN) {
                    LazyPosition buttonPosition = accessoryScreen.getAccessoryExtensionToggleButtonPosition(buttonStyle);

                    rects.add(new Rect2i(
                            screen.leftPos + buttonPosition.x().get(),
                            screen.topPos + buttonPosition.y().get(),
                            buttonStyle.width,
                            buttonStyle.height));
                }

                LazyPosition position = accessoryScreen.getAccessoryExtensionPosition();

                rects.add(new Rect2i(
                        screen.leftPos + position.x().get(),
                        screen.topPos + position.y().get(),
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
            Gui gui = mc.gui;

            if (player != null && gui.screen() instanceof IAccessoryScreen) {
                mc.execute(() -> gui.setScreen(null));

                AbstractContainerMenu menu = player.containerMenu;

                if (menu != mc.player.inventoryMenu) {
                    player.connection.send(new ServerboundContainerClosePacket(menu.containerId));
                }
            }
        }
    }

    public static void onDisconnect(Runnable loadFunction) {
        AccessoryTypeManager.clear();
        reloadRegisteredKeybinds(loadFunction);
    }

    public static boolean onKeyPressedInMenu(AbstractContainerScreen<?> screen, KeyEvent event) {
        if (OhmegaBinds.OPEN_ACCESSORY_INVENTORY.matches(event)) {
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
            Component component = AccessoryHelperClient.getTypeTooltip(stack.getItem());

            if (component != null) {
                tooltip.add(component);
            }
        }
    }

    public static void onJoinWorld(Minecraft mc) {
        CommonCallbacks.onSetupAccessoryTypeManager();

        BooleanLazySavedValue option = OhmegaConfig.Client.getData().showTranslationToast();

        if (option.get()) {
            mc.gui.toastManager().addToast(new SystemToast(
                    new SystemToast.SystemToastId(7500),
                    Component.translatable("toast.ohmega.translation.title"),
                    Component.translatable("toast.ohmega.translation.message")
            ));

            option.set(false);
            option.serialise(true);
        }
    }

    @SuppressWarnings({"DataFlowIssue", "ConditionCoveredByFurtherCondition"})
    public static void onKeyInput(KeyEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player != null) {
            Gui gui = mc.gui;
            Screen screen = gui.screen();

            if (screen == null) {
                if (OhmegaBinds.OPEN_EDIT_UI.consumeClick()) {
                    gui.setScreen(new EditUiScreen(null, player));
                }

                while (OhmegaBinds.OPEN_ACCESSORY_INVENTORY.consumeClick() && (player.portalProcess == null || !player.portalProcess.isInsidePortalThisTick())) {
                    if (mc.gameMode != null && mc.gameMode.isServerControlledInventory()) {
                        player.sendOpenInventory();
                    } else {
                        if (player.hasInfiniteMaterials()) {
                            screen = new CreativeModeInventoryScreen(player, player.connection.enabledFeatures(), mc.options.operatorItemsTab().get());
                        } else {
                            screen = new InventoryScreen(player);
                        }

                        gui.setScreen(screen);

                        AccessoryScreenExtension extension = ((IAccessoryScreen) screen).getAccessoryExtension();

                        if (extension != null) {
                            extension.setVisible(true);
                        }
                    }
                }

                List<KeyMapping> mappings = OhmegaBinds.getMappings();
                Set<AccessoryType> keyboundSlotTypes = OhmegaConfig.Server.getKeyboundSlotTypes();
                AccessoryData data = OhmegaDataAttachments.getData(player);

                if (mappings.isEmpty() || keyboundSlotTypes.isEmpty() || data.isEmpty()) {
                    return;
                }

                int size = OhmegaBinds.size();

                // Never ever touch this again; wrote 2 months ago, I now consider it dark magic.
                for (int i = 0; i < size; i++) {
                    KeyMapping mapping = mappings.get(i);
                    int j = 0;

                    if (mapping.consumeClick()) {
                        for (int k = 1; true; j++) {
                            if (keyboundSlotTypes.contains(data.getEntry(j).getType()) && k++ > i) {
                                break;
                            }
                        }

                        ItemStack stack = data.getEntry(j).getStack();
                        Accessory accessory = Accessories.get(stack.getItem());

                        if (accessory != null) {
                            boolean shouldNotifyServer = accessory.onKeybindUse(player, stack);

                            if (shouldNotifyServer) {
                                OhmegaNetworking.sendC2S(new KeybindUsePacket(j));
                            }
                        }
                    }
                }
            } else {
                if (!(screen instanceof EditUiScreen) && screen instanceof IAccessoryScreen && OhmegaBinds.OPEN_EDIT_UI.matches(event) && !(screen.getFocused() instanceof EditBox)) {
                    gui.setScreen(new EditUiScreen(screen, player));
                }
            }
        }
    }

    // Post an event here
    public static void onPostScreenInit(Screen screen, Consumer<AbstractWidget> consumer) {
        if (screen instanceof IAccessoryScreen accessoryScreen) {
            AccessoryScreenExtension extension = accessoryScreen.getAccessoryExtension();

            if (extension != null) {
                OhmegaConfig.Client.Service.ButtonStyle style = OhmegaConfig.Client.getData().toggleExtensionButtonStyle().getObject();
                AbstractContainerScreen<?> containerScreen = extension.getScreen();

                List<AccessorySlot> slots = extension.getMenuExtension().getSlots();

                if (slots != null && !slots.isEmpty() && style != OhmegaConfig.Client.Service.ButtonStyle.HIDDEN) {
                    consumer.accept(new ToggleExtensionButton(containerScreen, extension, style));
                }

                if (screen instanceof IEntityRenderingScreen entityRenderingScreen && extension instanceof IEntityRenderingExtension entityRenderingExtension) {
                    consumer.accept(new FlipEntityButton(containerScreen, entityRenderingScreen.getFlipEntityButtonPosition(), entityRenderingExtension));
                }

                AccessoryScreens.doExtensionInit(screen, extension, consumer);
            }
        }
    }

    public static <T extends SharedSuggestionProvider> void onRegisterCommands(CommandDispatcher<T> dispatcher, CommandBuildContext context, IClientCommandSource.Factory<T> sourceFactory) {
        OhmegaClientRootCommand.register(dispatcher, context, sourceFactory);
    }

    public static void onServerConfigReload(Runnable loadFunction) {
        AccessoryTypeManager.runConfigLoadTasks();

        if (OhmegaConfig.Client.isLoaded()) {
            if (AccessoryTypeManager.getTypes().isEmpty()) {
                AccessoryTypeManager.deferApply(() -> ClientCallbacks.reloadRegisteredKeybinds(loadFunction));
            } else {
                reloadRegisteredKeybinds(loadFunction);
            }

            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            if (player != null) {
                Gui gui = mc.gui;

                if (gui.screen() instanceof IAccessoryScreen) {
                    mc.execute(() -> gui.setScreen(null));

                    AbstractContainerMenu menu = player.containerMenu;

                    if (menu != mc.player.inventoryMenu) {
                        player.connection.send(new ServerboundContainerClosePacket(menu.containerId));
                    }
                }
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
            for (AccessoryDataEntry entry : data.entries()) {
                ItemStack stack = entry.getStack();
                // todo: optimise this by caching it somehow
                if (AccessoryRenderers.isNoRender(stack.getItem(), state.entityType)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void reloadRegisteredKeybinds(Runnable loadFunction) {
        ArrayList<KeyMapping> list = new ArrayList<>();

        OhmegaBinds.rebuildSlotKeys();

        for (List<KeyMapping> immutableList : OhmegaBinds.getSlotKeys().values()) {
            list.addAll(immutableList);
        }

        Options options = Minecraft.getInstance().options;
        options.keyMappings = ArrayUtils.addAll(Arrays.stream(options.keyMappings).filter(v -> !OhmegaBinds.isInstance(v)).toList().toArray(new KeyMapping[0]), list.toArray(new KeyMapping[0]));

        loadFunction.run();
    }
}
