package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.AccessoryHelper;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
abstract class ServerPlayerGameModeMixin {
    @Redirect(
            method = "useItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult useItem(ItemStack stack, Level level, Player player, InteractionHand hand) {
        InteractionResult original = stack.use(level, player, hand);

        if (!original.consumesAction()) {
            InteractionResult candidate = AccessoryHelper.tryEquip(player, hand);

            if (candidate.consumesAction()) {
                return candidate;
            }
        }

        return original;
    }
}
