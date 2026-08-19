package com.swacky.ohmega.client.event;

import com.mojang.brigadier.CommandDispatcher;
import com.swacky.ohmega.api.client.command.IClientCommandSource;
import com.swacky.ohmega.api.client.event.ClientCallbacks;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class ClientEvents {
    private static final Runnable LOAD_FUNCTION = () -> Minecraft.getInstance().options.load();

    private static boolean bootstrapped = false;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            ModConfigEvents.loading(Ohmega.MODID).register(ClientEvents::onConfigLoad);
            ModConfigEvents.reloading(Ohmega.MODID).register(ClientEvents::onConfigReload);
            ModConfigEvents.unloading(Ohmega.MODID).register(ClientEvents::onConfigUnload);
            ClientPlayConnectionEvents.DISCONNECT.register(ClientEvents::onDisconnect);
            ClientEntityEvents.ENTITY_LOAD.register(ClientEvents::onEntityLoad);
            ClientEntityEvents.ENTITY_UNLOAD.register(ClientEvents::onEntityUnload);
            ItemTooltipCallback.EVENT.register(ClientEvents::onItemTooltip);
            ClientPlayConnectionEvents.JOIN.register(ClientEvents::onJoinWorld);
            ScreenEvents.AFTER_INIT.register(ClientEvents::onPostScreenInit);
            ClientCommandRegistrationCallback.EVENT.register(ClientEvents::onRegisterCommands);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + ClientEvents.class + " multiple times");
        }
    }

    private static void onConfigLoad(ModConfig config) {
        if (config.getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            AccessoryTypeManager.runConfigLoadTasks();
        }
    }

    private static void onConfigReload(ModConfig config) {
        IConfigSpec spec = config.getSpec();

        if (spec == OhmegaConfigImpl.Client.getSpec()) {
            ClientCallbacks.onClientConfigReload();
        } else if (spec == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigReload(LOAD_FUNCTION);
        }
    }

    private static void onConfigUnload(ModConfig config) {
        if (config.getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigUnload(LOAD_FUNCTION);
        }
    }

    private static void onDisconnect(ClientPacketListener listener, Minecraft mc) {
        ClientCallbacks.onDisconnect(LOAD_FUNCTION);
    }

    private static void onEntityLoad(Entity entity, ClientLevel level) {
        if (entity instanceof LivingEntity living) {
            OhmegaDataAttachments.getData(living).onAttach(living);
        }
    }

    private static void onEntityUnload(Entity entity, ClientLevel level) {
        if (entity instanceof LivingEntity living) {
            AccessoryData.DEFAULT_TRACKERS.remove(living);
        }
    }

    private static void onItemTooltip(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> tooltip) {
        ClientCallbacks.onItemTooltip(stack, tooltip);
    }

    private static void onJoinWorld(ClientPacketListener listener, PacketSender sender, Minecraft mc) {
        ClientCallbacks.onJoinWorld(mc);
    }

    private static void onPostScreenInit(Minecraft mc, Screen screen, int width, int height) {
        ClientCallbacks.onPostScreenInit(screen, screen::addRenderableWidget);
    }

    private static void onRegisterCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        ClientCallbacks.onRegisterCommands(dispatcher, context, cmdContext -> {
            FabricClientCommandSource source = cmdContext.getSource();

            return new IClientCommandSource() {
                @Override
                public void sendSuccess(@NonNull Component message) {
                    source.sendFeedback(message);
                }

                @Override
                public void sendError(@NonNull Component message) {
                    source.sendError(message);
                }

                @Override
                public @NonNull LocalPlayer getPlayer() {
                    return source.getPlayer();
                }
            };
        });
    }
}
