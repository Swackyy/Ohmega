package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.common.init.OhmegaDataAttachmentsImpl;
import com.swacky.ohmega.event.CommonCallbacks;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity implements Attackable, WaypointTransmitter {
    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "TAIL"))
    public void tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (OhmegaDataAttachmentsImpl.isAccessoryDataPresent(entity)) {
            CommonCallbacks.onLivingPostTick(entity);
        }
    }
}
