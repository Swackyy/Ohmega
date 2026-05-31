package com.swacky.ohmega.event;

import com.google.common.reflect.TypeToken;
import com.swacky.ohmega.api.client.command.IClientCommandSource;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.client.model.HaloModel;
import com.swacky.ohmega.client.renderer.AccessoryRenderStateDataImpl;
import com.swacky.ohmega.client.renderer.HaloRenderer;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jspecify.annotations.NonNull;

@EventBusSubscriber(modid = Ohmega.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    private static final Runnable LOAD_FUNCTION = () -> Minecraft.getInstance().options.load();

    @SubscribeEvent
    public static void onAddAttributeTooltips(AddAttributeTooltipsEvent event) {
        AttributeTooltipContext context = event.getContext();
        DataComponentType<ItemAttributeModifiers> type = OhmegaDataComponents.getAccessoryActiveModifiers();

        if (context.tooltipDisplay().shows(type)) {
            boolean[] flag = {true};

            event.getStack().getOrDefault(type, ItemAttributeModifiers.EMPTY).forEach(EquipmentSlotGroup.ANY, (attribute, modifier, tooltip) -> {
                if (tooltip != ItemAttributeModifiers.Display.hidden()) {
                    if (flag[0]) {
                        flag[0] = false;

                        event.addTooltipLines(
                                CommonComponents.EMPTY,
                                Component.translatable(Ohmega.MODID + ".item.modifiers.accessory_active").withStyle(ChatFormatting.GRAY));
                    }

                    tooltip.apply(event::addTooltipLines, context.player(), attribute, modifier);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> AccessoryRenderers.registerLiving(OhmegaItems.getAngelRing(), HaloRenderer::new));
    }

    @SubscribeEvent
    private static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            AccessoryTypeManager.runDeferredAwaitingConfigLoad();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        IConfigSpec spec = event.getConfig().getSpec();

        if (spec == OhmegaConfigImpl.Client.getSpec()) {
            ClientCallbacks.onClientConfigReload();
        } else if (spec == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigReload(LOAD_FUNCTION);
        }
    }

    @SubscribeEvent
    public static void onConfigUnload(ModConfigEvent.Unloading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigUnload(LOAD_FUNCTION);
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ClientCallbacks.onItemTooltip(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onKeybindRegistration(RegisterKeyMappingsEvent event) {
        event.register(OhmegaBinds.EDIT_MAGNETICS);
        event.register(OhmegaBinds.EDIT_NUDGE_DOWN);
        event.register(OhmegaBinds.EDIT_NUDGE_LEFT);
        event.register(OhmegaBinds.EDIT_NUDGE_RIGHT);
        event.register(OhmegaBinds.EDIT_NUDGE_UP);
        event.register(OhmegaBinds.EDIT_SHOW_LINES);
        event.register(OhmegaBinds.OPEN_ACCESSORY_INVENTORY);
        event.register(OhmegaBinds.OPEN_EDIT_UI);
        event.registerCategory(OhmegaBinds.CATEGORY);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        ClientCallbacks.onKeyInput(event.getKeyEvent());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientCallbacks.onJoinWorld(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onPostScreenInit(ScreenEvent.Init.Post event) {
        ClientCallbacks.onPostScreenInit(event.getScreen(), event::addListener);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        ClientCallbacks.onRegisterCommands(event.getDispatcher(), event.getBuildContext(), context -> {
            CommandSourceStack source = context.getSource();

            return new IClientCommandSource() {
                @Override
                public void sendSuccess(@NonNull Component message) {
                    source.sendSuccess(() -> message, false);
                }

                @Override
                public void sendError(@NonNull Component message) {
                    source.sendFailure(message);
                }

                @Override
                public @NonNull LocalPlayer getPlayer() {
                    return Minecraft.getInstance().player;
                }
            };
        });
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HaloModel.LOCATION, HaloModel::createDefinition);
    }

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(new TypeToken<LivingEntityRenderer<?, ?, ?>>() {}, (entity, state) ->
                state.setRenderData(AccessoryRenderStateDataImpl.KEY, ClientCallbacks.createRenderStateData(entity)));
    }
}
