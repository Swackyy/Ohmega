package com.swacky.ohmega.event;

import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.cap.AccessoryContainer;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.network.C2S.SyncAccessoriesPacket;
import com.swacky.ohmega.network.ModNetworking;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    @SubscribeEvent
    public static void playerJoin(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            syncSlots(player, Collections.singletonList(player));
        }
    }

    @SubscribeEvent
    public static void tracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer player) {
            syncSlots(player, Collections.singletonList(event.getPlayer()));
        }
    }


    private static void syncSlots(ServerPlayer player, Collection<? extends Player> receivers) {
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(accessories -> {
            for (byte i = 0; i < accessories.getSlots(); i++) {
                syncSlot(player, i, accessories.getStackInSlot(i), receivers);
            }
        });
    }

    public static void syncSlot(Player player, byte slot, ItemStack stack, Collection<? extends Player> receivers) {
        SyncAccessoriesPacket packet = new SyncAccessoriesPacket(player.getId(), slot, stack);
        for (Player receiver : receivers) {
            if (receiver instanceof ServerPlayer s)
                ModNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> s), packet);
        }
    }

    private static final ResourceLocation cap = new ResourceLocation(Ohmega.MODID, "accessories_cap");

    @SubscribeEvent
    public static void attachCapsItem(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof IAccessory) {
            event.addCapability(cap, new ICapabilityProvider() {
                private final LazyOptional<IAccessory> opt = LazyOptional.of(() -> (IAccessory) stack.getItem());

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    return Ohmega.ACCESSORY_ITEM.orEmpty(cap, opt);
                }
            });
        }
    }

    @SubscribeEvent
    public static void attachCapsPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player player) {
            event.addCapability(new ResourceLocation(Ohmega.MODID, "container"), new AccessoryContainerProvider(player));
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            var player = event.player;
            player.getCapability(Ohmega.ACCESSORIES).ifPresent(AccessoryContainer::tick);
        }
    }

    @SubscribeEvent
    public static void cloneCapabilitiesEvent(PlayerEvent.Clone event) {
        try {
            event.getOriginal().getCapability(Ohmega.ACCESSORIES).ifPresent(container -> {
                CompoundTag tag = container.serializeNBT();
                event.getOriginal().getCapability(Ohmega.ACCESSORIES).ifPresent(accessoryContainer -> accessoryContainer.deserializeNBT(tag));
            });
        } catch (Exception e) {
            System.out.println("Could not clone player [" + event.getOriginal().getName() + "]'s accessories.");
        }
    }

    private static class AccessoryContainerProvider implements INBTSerializable<CompoundTag>, ICapabilityProvider {
        private final AccessoryContainer inner;
        private final LazyOptional<AccessoryContainer> handlerCap;

        public AccessoryContainerProvider(Player player) {
            this.inner = new AccessoryContainer(player);
            this.handlerCap = LazyOptional.of(() -> this.inner);
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
            return Ohmega.ACCESSORIES.orEmpty(cap, this.handlerCap);
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.inner.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.inner.deserializeNBT(tag);
        }
    }
}
