package com.swacky.ohmega.mixin;

import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.event.CommonCallbacks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntity {
    private PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "TAIL"))
    public void tick(CallbackInfo ci) {
        CommonCallbacks.onPlayerPostTick(((Player) (Object) this));
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At(
                    value = "HEAD"))
    public void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        getAttachedOrCreate(OhmegaDataAttachments.ACCESSORY_HANDLER).onAttach((Player) (Object) this);
    }
}
