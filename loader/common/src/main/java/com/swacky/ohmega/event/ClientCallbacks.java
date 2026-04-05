package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.client.screen.widget.AccessoryInventoryButton;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.menu.AccessoryInventoryMenu;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.ReloadDataPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class ClientCallbacks {
    // todo
    public static AccessoryRenderStateData createRenderStateData(Player player) {
        if (OhmegaConfig.Server.isLoaded() && OhmegaConfig.Server.allowHideAccessories()) {
            AccessoryData data = AccessoryHelper.getData(player);
            NonNullList<ItemStack> stacks = data.getStacks();
            NonNullList<ItemStack> stacksFiltered = NonNullList.createWithCapacity(stacks.size());

            for (int i = 0; i < stacks.size(); i++) {
                ItemStack stack = data.getStackInSlot(i);

                if (!stack.isEmpty() && !data.isHidden(i)) {
                    stacksFiltered.add(stack);
                }
            }

            return new AccessoryRenderStateData(stacksFiltered);
        }

        return new AccessoryRenderStateData(AccessoryHelper.getStacksNoEmpty(player));
    }

    public static void onClientConfigReload() {
        if (!OhmegaConfig.Client.compatibilityMode()) {
            LocalPlayer player = Minecraft.getInstance().player;

            if (player != null && player.containerMenu instanceof AccessoryInventoryMenu) {
                OhmegaNetworking.C2S.send(OpenAccessoryInventoryPacket.INSTANCE);
            }
        }
    }

    public static void onItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (AccessoryHelper.isAccessory(stack.getItem())) {
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

    public static void onKeyInput() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen == null && mc.player != null) {
            while (OhmegaBinds.OPEN_ACC_INV.consumeClick() && (mc.player.portalProcess == null || !mc.player.portalProcess.isInsidePortalThisTick())) {
                if (mc.gameMode != null && mc.gameMode.isServerControlledInventory()) {
                    mc.player.sendOpenInventory();
                } else if (!mc.player.isCreative() && !mc.player.isSpectator()) {
                    OhmegaNetworking.C2S.send(OpenAccessoryInventoryPacket.INSTANCE);
                } else {
                    mc.setScreen(new InventoryScreen(mc.player));
                }
            }

            ImmutableList<KeyMapping> mappings = OhmegaBinds.getMappings();
            ImmutableList<AccessoryType> keyboundSlotTypes = AccessoryHelper.getKeyboundSlotTypes();
            ImmutableList<AccessoryType> slotTypes = AccessoryHelper.getSlotTypes();

            if (mappings.isEmpty() || keyboundSlotTypes.isEmpty() || slotTypes.isEmpty()) {
                return;
            }

            AccessoryData data = AccessoryHelper.getData(mc.player);

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
                    IAccessory accessory = AccessoryHelper.getAccessory(stack.getItem());

                    if (accessory != null) {
                        // Client handling
                        if (!OhmegaHooks.accessoryUseEvent(mc.player, stack)) {
                            accessory.onKeybindUse(mc.player, stack);
                        }

                        // Server handling
                        OhmegaNetworking.C2S.send(new UseAccessoryPacket(j));
                    }
                }
            }
        }
    }

    public static void onPostScreenInit(Screen screen, Consumer<AbstractWidget> widgetConsumer) {
        if (screen instanceof InventoryScreen && OhmegaConfig.Client.buttonStyle() != OhmegaConfig.Client.Service.ButtonStyle.HIDDEN) {
            Minecraft mc = screen.minecraft;

            if (mc.player != null && !mc.player.isCreative() && !mc.player.isSpectator()) {
                widgetConsumer.accept(new AccessoryInventoryButton((AbstractContainerScreen<?>) screen, OhmegaConfig.Client.buttonStyle()));
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
            LocalPlayer player = Minecraft.getInstance().player;

            if (player != null) {
                AccessoryHelper.getData(player).reload(player);
                OhmegaNetworking.C2S.send(ReloadDataPacket.INSTANCE);

                if (!OhmegaConfig.Client.compatibilityMode() && player.containerMenu instanceof AccessoryInventoryMenu) {
                    mc.screen = null;

                    player.connection.send(new ServerboundContainerClosePacket(player.containerMenu.containerId));
                    OhmegaNetworking.C2S.send(OpenAccessoryInventoryPacket.INSTANCE);
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

    public static void preventRender(LivingEntityRenderState state, CallbackInfo ci) {
        AccessoryRenderStateData data = AccessoryRenderStateData.getData(state);

        if (data != null) {
            for (ItemStack stack : data.stacks()) {
                if (AccessoryRenderers.isNoRender(AccessoryHelper.getAccessory(stack.getItem()), state.entityType)) {
                    ci.cancel();
                    return;
                }
            }
        }
    }

    public static void reloadRegisteredKeybinds(Runnable loadFunction) {
        ArrayList<KeyMapping> list = new ArrayList<>();

        for (ImmutableList<KeyMapping> immutableList : OhmegaBinds.getSlotKeys().values()) {
            list.addAll(immutableList);
        }

        Options options = Minecraft.getInstance().options;
        options.keyMappings = ArrayUtils.addAll(Arrays.stream(options.keyMappings).filter(v -> !OhmegaBinds.isInstance(v)).toList().toArray(new KeyMapping[0]), list.toArray(new KeyMapping[0]));

        loadFunction.run();
    }
}
