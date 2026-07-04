package com.swacky.ohmega.mixin;

import com.mojang.authlib.GameProfile;
import com.swacky.ohmega.common.menu.AccessorySlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin extends Player {
    @Mutable
    @Shadow
    @Final
    private ContainerSynchronizer containerSynchronizer;

    public ServerPlayerMixin(Level level, GameProfile profile) {
        super(level, profile);
    }

    @ModifyVariable(method = "<init>", at = @At(value = "RETURN"), argsOnly = true)
    private MinecraftServer init(MinecraftServer server) {
        containerSynchronizer = new ContainerSynchroniserWrapper(containerSynchronizer, inventoryMenu);
        return server;
    }

    private record ContainerSynchroniserWrapper(ContainerSynchronizer wrapped, InventoryMenu menu) implements ContainerSynchronizer {
        @Override
        public void sendInitialData(AbstractContainerMenu container, List<ItemStack> slotItems, ItemStack carried, int[] dataSlots) {
            for (int i = slotItems.size() - 1; i >= 0; i--) {
                if (menu.getSlot(i) instanceof AccessorySlot) {
                    slotItems.remove(i);
                }
            }

            wrapped.sendInitialData(container, slotItems, carried, dataSlots);
        }

        @Override
        public void sendSlotChange(AbstractContainerMenu container, int slotIndex, ItemStack itemStack) {
            wrapped.sendSlotChange(container, slotIndex, itemStack);
        }

        @Override
        public void sendCarriedChange(AbstractContainerMenu container, ItemStack itemStack) {
            wrapped.sendCarriedChange(container, itemStack);
        }

        @Override
        public void sendDataChange(AbstractContainerMenu container, int id, int value) {
            wrapped.sendDataChange(container, id, value);
        }

        @Override
        public RemoteSlot createSlot() {
            return wrapped.createSlot();
        }
    }
}
