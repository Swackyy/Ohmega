package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.cap.AccessoryContainer;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.network.S2C.SyncAccessoriesPacket;
import com.swacky.ohmega.network.ModNetworking;
import com.swacky.ohmega.network.S2C.SyncActivePacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    // Syncs slots upon player joining
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof ServerPlayer svr) {
            syncSlots(svr, Collections.singletonList(svr));
            svr.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> ModNetworking.sendTo(new SyncActivePacket(svr.getId(), a.getActive()), svr));
        }
    }

    // Begins player tracking
    @SubscribeEvent
    public static void onPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer player) {
            syncSlots(player, Collections.singletonList(event.getPlayer()));
        }
    }


    // These use a player collection because of what is planned for the future of the mod and API
    // Sync all slots
    private static void syncSlots(ServerPlayer player, Collection<? extends Player> receivers) {
        player.getCapability(Ohmega.ACCESSORIES).ifPresent(accessories -> {
            for (byte i = 0; i < accessories.getSlots(); i++) {
                syncSlot(player, i, accessories.getStackInSlot(i), receivers);
            }
        });
    }

    // Sync provided slot
    public static void syncSlot(Player player, byte slot, ItemStack stack, Collection<? extends Player> receivers) {
        SyncAccessoriesPacket packet = new SyncAccessoriesPacket(player.getId(), slot, stack);
        for (Player receiver : receivers) {
            if (receiver instanceof ServerPlayer svr) {
                ModNetworking.sendTo(packet, svr);
            }
        }
    }

    // Attach accessory capability to the accessory item
    @SubscribeEvent
    public static void attachCapsItem(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof IAccessory) {
            event.addCapability(new ResourceLocation(Ohmega.MODID, "accessories_cap"), new ICapabilityProvider() {
                private final LazyOptional<IAccessory> opt = LazyOptional.of(() -> (IAccessory) stack.getItem());

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    return Ohmega.ACCESSORY_ITEM.orEmpty(cap, opt);
                }
            });
        }
    }

    // Attaches accessory container provider capability to the player
    @SubscribeEvent
    public static void attachCapsPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player player) {
            event.addCapability(new ResourceLocation(Ohmega.MODID, "container"), new AccessoryContainerProvider(player));
        }
    }

    // Provides the calling for the Accessories' ticking
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            var player = event.player;
            player.getCapability(Ohmega.ACCESSORIES).ifPresent(AccessoryContainer::tick);
        }
    }

    // Clones caps when changing dimensions
    @SubscribeEvent
    public static void onCloneCaps(PlayerEvent.Clone event) {
        try {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(Ohmega.ACCESSORIES).ifPresent(old -> {
                if((event.isWasDeath() && event.getOriginal().getServer() != null && event.getOriginal().getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) || !event.isWasDeath()) {
                    event.getEntity().getCapability(Ohmega.ACCESSORIES).ifPresent(newStore -> newStore.deserializeNBT(old.serializeNBT()));
                    event.getOriginal().invalidateCaps();
                }
            });
        } catch (Exception e) {
            Ohmega.LOGGER.warn("Player [{}]'s accessories could not be cloned.", event.getOriginal().getName());
        }
    }

    // For dropping accessories upon players' deaths.
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer svr && !svr.server.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            event.getEntity().getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                for (int i = 0; i < a.getSlots(); i++) {
                    if(a.getStackInSlot(i).getItem() instanceof IAccessory acc) {
                        a.getStackInSlot(i).getOrCreateTag().putInt("slot", -1);
                        AccessoryHelper.addActiveTag(a.getStackInSlot(i), false);
                        acc.onUnequip(svr, a.getStackInSlot(i));
                        svr.drop(a.getStackInSlot(i), false, false);
                    }
                }
            });
        }
    }

    // Inner class for providing a container to a player, utility class.
    private static class AccessoryContainerProvider implements INBTSerializable<CompoundTag>, ICapabilityProvider {
        private final AccessoryContainer inner;
        private final LazyOptional<AccessoryContainer> cap;

        public AccessoryContainerProvider(Player player) {
            this.inner = new AccessoryContainer(player);
            this.cap = LazyOptional.of(() -> this.inner);
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
            return Ohmega.ACCESSORIES.orEmpty(cap, this.cap);
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
