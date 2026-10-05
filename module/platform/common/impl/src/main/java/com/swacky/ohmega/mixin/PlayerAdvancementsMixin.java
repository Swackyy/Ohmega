package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.item.AccessoryContext;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerAdvancements.class)
abstract class PlayerAdvancementsMixin {
    @Shadow
    private ServerPlayer player;

    @Inject(
            method = "award",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerAdvancements;markForVisibilityUpdate(Lnet/minecraft/advancements/AdvancementHolder;)V"))
    private void award(AdvancementHolder advancement, String criterion, CallbackInfoReturnable<Boolean> cir) {
        List<@NonNull ObjectIntImmutablePair<@NonNull AccessoryType>> list = AccessoryTypeManager.getAdvancementReward(advancement.id());

        if (!list.isEmpty()) {
            for (ObjectIntImmutablePair<@NonNull AccessoryType> entry : list) {
                OhmegaDataAttachments.getData(player).addSlots(player, entry.first(), entry.secondInt(), AccessoryContext.ADVANCEMENT);
            }
        }
    }

    @Inject(
            method = "revoke",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerAdvancements;markForVisibilityUpdate(Lnet/minecraft/advancements/AdvancementHolder;)V"))
    private void revoke(AdvancementHolder advancement, String criterion, CallbackInfoReturnable<Boolean> cir) {
        List<@NonNull ObjectIntImmutablePair<@NonNull AccessoryType>> list = AccessoryTypeManager.getAdvancementReward(advancement.id());

        if (!list.isEmpty()) {
            for (ObjectIntImmutablePair<@NonNull AccessoryType> entry : list) {
                OhmegaDataAttachments.getData(player).clearSlots(
                        player,
                        (type, context) -> type.equals(entry.first()) && context == AccessoryContext.ADVANCEMENT,
                        entry.secondInt(),
                        AccessoryContext.ADVANCEMENT);
            }
        }
    }
}
