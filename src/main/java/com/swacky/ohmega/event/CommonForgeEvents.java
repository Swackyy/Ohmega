package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.api.event.AccessoryEquipEvent;
import com.swacky.ohmega.api.event.AccessoryUnequipEvent;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Ohmega.MODID)
public class CommonForgeEvents {
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AccessoryHelper.syncAllSlots(player, Collections.singletonList(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer player && event.getEntity() instanceof ServerPlayer player0) {
            AccessoryHelper.syncAllSlots(player, Collections.singletonList(player0));
        }
    }

    @SubscribeEvent
    public static void attachCapsPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(Ohmega.rl("accessory_container"), new AccessoryContainerProvider(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            var player = event.player;
            player.getCapability(Ohmega.ACCESSORIES).ifPresent(AccessoryContainer::tick);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @SubscribeEvent
    public static void onCloneCaps(PlayerEvent.Clone event) {
        try {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(Ohmega.ACCESSORIES).ifPresent(old -> {
                if ((event.isWasDeath() && event.getOriginal().getServer() != null && event.getOriginal().getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) || !event.isWasDeath()) {
                    event.getEntity().getCapability(Ohmega.ACCESSORIES).ifPresent(newStore -> {
                        newStore.deserializeNBT(event.getOriginal().registryAccess(), old.serializeNBT(event.getOriginal().registryAccess()));
                        if (event.getOriginal().getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                        for (int i = 0; i < newStore.getSlots(); i++) {
                            ItemStack stack = newStore.getStackInSlot(i);
                            IAccessory acc =  AccessoryHelper.getBoundAccessory(stack.getItem());
                            if (acc != null) {
                                Player player = event.getEntity();
                                AccessoryHelper.changeModifiers(player, AccessoryHelper.getModifiers(stack).getPassive(), true);

                                AccessoryEquipEvent event0 = OhmegaHooks.accessoryEquipEvent(player, stack, AccessoryEquipEvent.Context.GENERIC);
                                if (!event0.isCanceled()) {
                                    acc.onEquip(player, stack);
                                }
                                AccessoryHelper.setSlot(stack, i);
                            }
                        }
                        }
                    });
                    event.getOriginal().invalidateCaps();
                }
            });
        } catch (Exception e) {
            Ohmega.LOGGER.warn("Player \"{}\"'s accessories could not be cloned.", event.getOriginal().getName());
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            OhmegaConfig.KeepAccessoriesBehaviour cfg = OhmegaConfig.CONFIG_SERVER.keepAccessories.get();
            boolean flag = switch (cfg) {
                case ON -> false;
                case OFF -> true;
                case DEFAULT -> player.getServer() == null || !player.getServer().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            };

            if (player.getServer() != null && flag) {
                player.getCapability(Ohmega.ACCESSORIES).ifPresent(a -> {
                    for (int i = 0; i < a.getSlots(); i++) {
                        ItemStack stack = a.getStackInSlot(i);
                        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                        if (acc != null) {
                            AccessoryUnequipEvent event0 = OhmegaHooks.accessoryUnequipEvent(player, stack);
                            if (!event0.isCanceled()) {
                                acc.onUnequip(player, stack);
                            }
                            AccessoryHelper.setSlot(stack, -1);
                            AccessoryHelper.setActive(player, stack, false);
                            player.drop(stack, false, false);
                        }
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        InteractionResult result = AccessoryHelper.tryEquip(event.getEntity(), event.getHand()).getResult();
        if (result == InteractionResult.SUCCESS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void addResourceReloadListeners(AddReloadListenerEvent event) {
        event.addListener(AccessoryTypeManager.getInstance());
    }

    @SuppressWarnings("deprecation")
    private static class AccessoryContainerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final AccessoryContainer inner;
        private final LazyOptional<AccessoryContainer> cap;
        private final Player player;

        public AccessoryContainerProvider(Player player) {
            this.inner = new AccessoryContainer(player);
            this.cap = LazyOptional.of(() -> this.inner);
            this.player = player;
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return Ohmega.ACCESSORIES.orEmpty(cap, this.cap);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
            return this.inner.serializeNBT(this.player.registryAccess());
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag tag) {
            this.inner.deserializeNBT(this.player.registryAccess(), tag);

            this.cap.ifPresent(a -> {
                for (int i = 0; i < a.getSlots(); i++) {
                    ItemStack stack = a.getStackInSlot(i);
                    if (AccessoryHelper.isActive(stack)) {
                        AccessoryHelper.changeModifiers(this.player, AccessoryHelper.getModifiers(stack).getActive(), true);
                    }
                }
            });
        }
    }
}
