package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.command.OhmegaRootCommand;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.ReloadContainerPacket;
import com.swacky.ohmega.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryPacket;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.network.S2C.SyncUsePacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

@EventBusSubscriber(modid = Ohmega.MODID)
public final class CommonEvents {
    private static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(Ohmega.id("sync_accessory_types"));

    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();

        if (!event.isWasDeath() || CommonCallbacks.shouldKeepInventory(oldPlayer)) {
            Player newPlayer = event.getEntity();

            CommonCallbacks.onClonePlayer(oldPlayer, newPlayer);
        }
    }

    @SubscribeEvent
    public static void onLivingDropItems(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player player) {
            CommonCallbacks.onPlayerDeath(player, event.getDrops());
        }
    }

    @SubscribeEvent
    public static void onModifyLivingVisibility(LivingEvent.LivingVisibilityEvent event) {
        // todo
        if (event.getEntity() instanceof Player player) {
            event.modifyVisibility(CommonCallbacks.getVisibilityPercentModifier(player, event.getLookingEntity()));
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CommonCallbacks.onPlayerChangeDimension(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.getContainer(player).onAttach(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerPostTick(PlayerTickEvent.Post event) {
        CommonCallbacks.onPlayerPostTick(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer tracked && event.getEntity() instanceof ServerPlayer tracker) {
            CommonCallbacks.onPlayerTrack(tracker, tracked);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        OhmegaRootCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void onRegisterConfigTasks(RegisterConfigurationTasksEvent event) {
        event.register(new ICustomConfigurationTask() {
            @Override
            public void run(@NonNull Consumer<CustomPacketPayload> consumer) {
                consumer.accept(new SyncTypesPacket());
                event.getListener().finishCurrentTask(TYPE);
            }

            @Override
            public @NonNull Type type() {
                return TYPE;
            }
        });
    }

    @SubscribeEvent
    public static void onRegisterNetwork(RegisterPayloadHandlersEvent event) {
        event.registrar("1.0")
                .playToServer(
                        OpenAccessoryInventoryPacket.TYPE,
                        OpenAccessoryInventoryPacket.CODEC,
                        new MainThreadPayloadHandler<>((_, context) ->
                                OhmegaNetworking.C2S.handleOpenAccessoryInventory((ServerPlayer) context.player())))
                .playToServer(
                        ReloadContainerPacket.TYPE,
                        ReloadContainerPacket.CODEC,
                        new MainThreadPayloadHandler<>((_, context) ->
                                OhmegaNetworking.C2S.handleReloadContainer((ServerPlayer) context.player())))
                .playToServer(
                        SetHiddenPacket.TYPE,
                        SetHiddenPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, context) ->
                                OhmegaNetworking.C2S.handleSetHidden(packet, (ServerPlayer) context.player())))
                .playToServer(
                        UseAccessoryPacket.TYPE,
                        UseAccessoryPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, context) ->
                                OhmegaNetworking.C2S.handleUseAccessory(packet, (ServerPlayer) context.player())))
                .playToClient(
                        SyncHiddenPacket.TYPE,
                        SyncHiddenPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, _) ->
                                OhmegaNetworking.S2C.handleSyncHidden(packet)))
                .playToClient(
                        SyncStacksPacket.TYPE,
                        SyncStacksPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, _) ->
                                OhmegaNetworking.S2C.handleSyncStacks(packet)))
                .configurationToClient(
                        SyncTypesPacket.TYPE,
                        SyncTypesPacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, _) ->
                                OhmegaNetworking.S2C.handleSyncTypes(packet)))
                .playToClient(
                        SyncUsePacket.TYPE,
                        SyncUsePacket.CODEC,
                        new MainThreadPayloadHandler<>((packet, _) ->
                                OhmegaNetworking.S2C.handleSyncUse(packet)));
    }

    @SubscribeEvent
    public static void onRegisterServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(Ohmega.RELOAD_LISTENER_ID, AccessoryTypeManager.getInstance());
    }
}
