package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.AccessoryEquipEvent;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeCapPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryKbPacket;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(modid = OhmegaCommon.MODID)
public class CommonEvents {
    private static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(OhmegaCommon.rl("sync_accessory_types"));

    private static ImmutableMap<Item, AccessoryType> accessoryTypeOverrides = ImmutableMap.of();

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            List<ServerPlayer> receivers = player.level().players();

            receivers.add(player);
            AccessoryHelper.syncAllSlots(player, receivers);
        }
    }

    @SubscribeEvent
    public static void onPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer player && event.getEntity() instanceof ServerPlayer player0) {
            AccessoryHelper.syncAllSlots(player, Collections.singletonList(player0));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        AccessoryHelper.getContainer(event.getEntity()).tick();
    }

    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        boolean flag = switch (OhmegaConfig.CONFIG_SERVER.keepAccessories.get()) { // Inverse
            case ON -> false;
            case OFF -> true;
            case DEFAULT -> {
                MinecraftServer server = oldPlayer.level().getServer();
                yield server == null || !server.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            }
        };

        if (event.isWasDeath() || flag) {
            AccessoryContainer oldA = AccessoryHelper.getContainer(oldPlayer);
            AccessoryContainer newA = AccessoryHelper.getContainer(newPlayer);

            for (int i = 0; i < newA.getSlots(); i++) {
                ItemStack stack = oldA.getStackInSlot(i);
                newA.setStackInSlot(i, stack);
                IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                if (acc != null) {
                    AccessoryHelper.changeModifiers(newPlayer, AccessoryHelper.getModifiers(stack).getPassive(), true);

                    if (!OhmegaHooks.accessoryEquipEvent(newPlayer, stack, AccessoryEquipEvent.Context.GENERIC)) {
                        acc.onEquip(newPlayer, stack);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            boolean flag = switch (OhmegaConfig.CONFIG_SERVER.keepAccessories.get()) { // Inverse
                case ON -> false;
                case OFF -> true;
                case DEFAULT -> {
                    MinecraftServer server = player.level().getServer();
                    yield server == null || !server.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
                }
            };

            if (flag) {
                AccessoryHelper.getContainer(player).invalidate();
            }
        }
    }

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        InteractionResult result = AccessoryHelper.tryEquip(event.getEntity(), event.getHand());
        if (result == InteractionResult.SUCCESS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void addResourceReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(OhmegaCommon.rl("accessory_types"), AccessoryTypeManager.getInstance());
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CommonEvents.accessoryTypeOverrides = OhmegaHooks.accessoryOverrideTypesEvent();
        });
    }

    @SubscribeEvent
    public static void registerNetwork(RegisterPayloadHandlersEvent event) {
        event.registrar("1.0")
                .playToServer(
                        OpenAccessoryInventoryPacket.TYPE,
                        OpenAccessoryInventoryPacket.CODEC,
                        new MainThreadPayloadHandler<>(OpenAccessoryInventoryPacket::handle))
                .playToServer(
                        OpenInventoryPacket.TYPE,
                        OpenInventoryPacket.CODEC,
                        new MainThreadPayloadHandler<>(OpenInventoryPacket::handle))
                .playToServer(
                        ResizeCapPacket.TYPE,
                        ResizeCapPacket.CODEC,
                        new MainThreadPayloadHandler<>(ResizeCapPacket::handle))
                .playToServer(
                        UseAccessoryKbPacket.TYPE,
                        UseAccessoryKbPacket.CODEC,
                        new MainThreadPayloadHandler<>(UseAccessoryKbPacket::handle))
                .playToClient(
                        SyncAccessorySlotsPacket.TYPE,
                        SyncAccessorySlotsPacket.CODEC,
                        new MainThreadPayloadHandler<>(SyncAccessorySlotsPacket::handle))
                .configurationToClient(
                        SyncAccessoryTypesPacket.TYPE,
                        SyncAccessoryTypesPacket.CODEC,
                        new MainThreadPayloadHandler<>(SyncAccessoryTypesPacket::handle));
    }

    @SubscribeEvent
    public static void addConfigTask(RegisterConfigurationTasksEvent event) {
        event.register(new ICustomConfigurationTask() {
            @Override
            public void run(@NotNull Consumer<CustomPacketPayload> sender) {
                sender.accept(new SyncAccessoryTypesPacket(AccessoryTypeManager.getInstance().getTypes()));
                event.getListener().finishCurrentTask(TYPE);
            }

            @Override
            public @NotNull Type type() {
                return TYPE;
            }
        });
    }

    public static ImmutableMap<Item, AccessoryType> getAccessoryTypeOverrides() {
        return CommonEvents.accessoryTypeOverrides;
    }
}
