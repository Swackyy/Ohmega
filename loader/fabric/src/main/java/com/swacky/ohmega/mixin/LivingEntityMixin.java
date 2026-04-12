package com.swacky.ohmega.mixin;

import com.swacky.ohmega.event.CommonCallbacks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity implements Attackable, WaypointTransmitter {
    private LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "dropAllDeathLoot",
            at = @At(
                    value = "TAIL"))
    private void dropAllDeathLoot(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
        ArrayList<ItemEntity> drops = new ArrayList<>();

        CommonCallbacks.onLivingDeath((LivingEntity) (Object) this, drops);

        for (ItemEntity entity : drops) {
            level().addFreshEntity(entity);
        }
    }

    @Inject(
            method = "getVisibilityPercent",
            at = @At(
                    value = "RETURN"),
            cancellable = true)
    private void getVisibilityPercent(Entity targetingEntity, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(cir.getReturnValue() * CommonCallbacks.getVisibilityPercentModifier((LivingEntity) (Object) this, targetingEntity));
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "TAIL"))
    private void tick(CallbackInfo ci) {
        CommonCallbacks.onLivingPostTick(((LivingEntity) (Object) this));
    }
}
