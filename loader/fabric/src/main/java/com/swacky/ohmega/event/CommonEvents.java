package com.swacky.ohmega.event;

import com.mojang.brigadier.CommandDispatcher;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.config.ModConfig;

public final class CommonEvents {
    private static boolean bootstrapped = false;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            ServerPlayerEvents.COPY_FROM.register(CommonEvents::onClonePlayer);
            ModConfigEvents.loading(Ohmega.MODID).register(CommonEvents::onConfigLoad);
            ModConfigEvents.reloading(Ohmega.MODID).register(CommonEvents::onConfigReload);
            ServerConfigurationConnectionEvents.CONFIGURE.register(CommonEvents::onConnectionConfigure);
            ServerEntityEvents.ENTITY_LOAD.register(CommonEvents::onEntityLoad);
            ServerEntityEvents.ENTITY_UNLOAD.register(CommonEvents::onEntityUnload);
            EntityTrackingEvents.START_TRACKING.register(CommonEvents::onLivingTrack);
            CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.OP_BLOCKS).register(CommonEvents::onModifyCreativeOpBlocksTab);
            ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(CommonEvents::onPlayerChangeDimension);
            CommandRegistrationCallback.EVENT.register(CommonEvents::onRegisterCommands);
            ServerLifecycleEvents.SERVER_STARTING.register(CommonEvents::onServerStarting);
            ItemEvents.USE.register(CommonEvents::onUseItem);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + CommonEvents.class + " multiple times");
        }
    }

    private static void onClonePlayer(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        CommonCallbacks.onClonePlayer(oldPlayer, newPlayer, alive);
    }

    private static void onConfigLoad(ModConfig config) {
        if (config.getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            CommonCallbacks.onServerConfigLoad();
        }
    }

    private static void onConfigReload(ModConfig config) {
        System.out.println("hello, config is: " + config.getFileName());
        if (config.getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            CommonCallbacks.onServerConfigReload();
        }
    }

    private static void onConnectionConfigure(ServerConfigurationPacketListenerImpl listener, MinecraftServer server) {
        ServerConfigurationNetworking.send(listener, new SyncTypesPacket(server.registryAccess()));
    }

    private static void onEntityLoad(Entity entity, ServerLevel level) {
        if (entity instanceof LivingEntity living) {
            OhmegaDataAttachments.getData(living).onAttach(living);
        }
    }

    private static void onEntityUnload(Entity entity, ServerLevel level) {
        if (entity instanceof LivingEntity living) {
            AccessoryData.DEFAULT_TRACKERS.remove(living);
        }
    }

    private static void onLivingTrack(Entity entity, ServerPlayer tracker) {
        if (entity instanceof LivingEntity tracked) {
            CommonCallbacks.onLivingTrack(tracker, tracked);
        }
    }

    private static void onModifyCreativeOpBlocksTab(FabricCreativeModeTabOutput output) {
        if (output.shouldShowOpRestrictedItems()) {
            output.accept(OhmegaItems.getAngelRing());
        }
    }

    private static void onPlayerChangeDimension(ServerPlayer player, ServerLevel from, ServerLevel to) {
        CommonCallbacks.onPlayerChangeDimension(player);
    }

    private static void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        CommonCallbacks.onRegisterCommands(dispatcher, context);
    }

    private static void onServerStarting(MinecraftServer server) {
        CommonCallbacks.onSetupAccessoryTypeManager();
    }

    private static InteractionResult onUseItem(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Accessory accessory = Accessories.get(stack.getItem());

        if (accessory != null && !accessory.preferVanillaUse(stack)) {
            InteractionResult candidate = AccessoryHelper.tryEquip(player, stack);

            if (candidate.consumesAction()) {
                return candidate;
            }
        }

        return null;
    }
}
