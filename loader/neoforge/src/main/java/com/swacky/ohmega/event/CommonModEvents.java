package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.OpenInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeCapPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryKbPacket;
import com.swacky.ohmega.network.S2C.SyncAccessorySlotsPacket;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = OhmegaCommon.MODID)
public class CommonModEvents {
    private static ImmutableMap<Item, AccessoryType> accessoryTypeOverrides = ImmutableMap.of();

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CommonModEvents.accessoryTypeOverrides = OhmegaHooks.accessoryOverrideTypesEvent();
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

    private static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(OhmegaCommon.rl("sync_accessory_types"));
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
        return CommonModEvents.accessoryTypeOverrides;
    }
}
