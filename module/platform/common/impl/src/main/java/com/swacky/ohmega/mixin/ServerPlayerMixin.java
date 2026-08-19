package com.swacky.ohmega.mixin;

import com.mojang.authlib.GameProfile;
import com.swacky.ohmega.api.common.menu.AccessorySlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
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
        public void sendInitialData(@NonNull AbstractContainerMenu container, List<ItemStack> stacks, @NonNull ItemStack carried, int @NonNull [] data) {
            for (int i = stacks.size() - 1; i >= 0; i--) {
                if (menu.getSlot(i) instanceof AccessorySlot) {
                    stacks.remove(i);
                }
            }

            wrapped.sendInitialData(container, stacks, carried, data);
        }

        @Override
        public void sendSlotChange(@NonNull AbstractContainerMenu menu, int index, @NonNull ItemStack stack) {
            wrapped.sendSlotChange(menu, index, stack);
        }

        @Override
        public void sendCarriedChange(@NonNull AbstractContainerMenu menu, @NonNull ItemStack stack) {
            wrapped.sendCarriedChange(menu, stack);
        }

        @Override
        public void sendDataChange(@NonNull AbstractContainerMenu menu, int id, int value) {
            wrapped.sendDataChange(menu, id, value);
        }

        @Override
        public @NonNull RemoteSlot createSlot() {
            return wrapped.createSlot();
        }
    }
}
