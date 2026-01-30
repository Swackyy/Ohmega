package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.AccessoryHelperImpl;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.network.OhmegaNetworkingImpl;
import com.swacky.ohmega.network.S2C.SyncAccessoryTypesPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.network.GatherLoginConfigurationTasksEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.config.SimpleConfigurationTask;
import org.jspecify.annotations.NonNull;

import java.util.Collections;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = OhmegaCommon.MODID)
public final class CommonForgeEvents {
    private static final ResourceLocation CAPABILITY_ID = OhmegaCommon.rl("accessory_data");
    private static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(OhmegaCommon.rl("sync_accessory_types").toString());

    @SubscribeEvent
    public static void onAttachEntityCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(CAPABILITY_ID, new AccessoryContainerProvider(player));
        }
    }

    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();

        if (!event.isWasDeath() || CommonCallbacks.shouldKeepInventory(oldPlayer)) {
            Player newPlayer = event.getEntity();

            oldPlayer.reviveCaps();
            CommonCallbacks.onClonePlayer(oldPlayer, newPlayer);
            oldPlayer.invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onLivingEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.getContainer(player).onDeath(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.syncAllSlots(player, Collections.singleton(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerPostTick(TickEvent.PlayerTickEvent.Post event) {
        Player player = event.player;

        if (AccessoryHelperImpl.isPlayerDataPresent(player)) {
            CommonCallbacks.onPlayerPostTick(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer tracked && event.getEntity() instanceof ServerPlayer tracker) {
            CommonCallbacks.onPlayerTrack(tracked, tracker);
        }
    }

    @SubscribeEvent
    public static void onRegisterConfigTasks(GatherLoginConfigurationTasksEvent event) {
        event.addTask(new SimpleConfigurationTask(TYPE,
                () -> OhmegaNetworkingImpl.S2C.send(event.getConnection(), new SyncAccessoryTypesPacket())));
    }

    @SubscribeEvent
    public static void onRegisterServerReloadListeners(AddReloadListenerEvent event) {
        event.addListener(AccessoryTypeManager.getInstance());
    }

    @SuppressWarnings("deprecation")
    private static class AccessoryContainerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private AccessoryContainer inner;
        private final LazyOptional<AccessoryContainer> cap;
        private final Player player;

        public AccessoryContainerProvider(Player player) {
            this.inner = new AccessoryContainer();
            this.cap = LazyOptional.of(() -> this.inner);
            this.player = player;
        }

        @NonNull
        @Override
        public <T> LazyOptional<T> getCapability(@NonNull Capability<T> cap, Direction side) {
            return Ohmega.ACCESSORIES.orEmpty(cap, this.cap);
        }

        @Override
        public CompoundTag serializeNBT() {
            return (CompoundTag) AccessoryContainer.CODEC.encodeStart(NbtOps.INSTANCE, inner).result().orElseGet(CompoundTag::new);
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            AccessoryContainer.CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial().ifPresent(data -> {
                inner = data;
                inner.onAttach(player);
            });
        }
    }
}
