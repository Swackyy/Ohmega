package com.swacky.ohmega.mixin;

import com.mojang.datafixers.util.Pair;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.dataattachment.AccessoryContainer;
import com.swacky.ohmega.event.CommonCallbacks;
import com.swacky.ohmega.extension.AttachmentHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntity implements AttachmentHolder {
    @Unique
    AccessoryContainer container;

    private PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public @NonNull AccessoryContainer ohmega$getContainer() {
        if (container == null) {
            ohmega$setContainer(new AccessoryContainer());
        }

        return container;
    }

    @Override
    public void ohmega$setContainer(AccessoryContainer container) {
        this.container = container;
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
        ohmega$setContainer(AccessoryContainer.CODEC.decode(NbtOps.INSTANCE, tag.get(OhmegaCommon.CONTAINER_TAG_KEY)).result().map(Pair::getFirst).orElse(null));
        ohmega$getContainer().onAttach((Player) (Object) this);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "RETURN"))
    public void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.put(OhmegaCommon.CONTAINER_TAG_KEY, AccessoryContainer.CODEC.encodeStart(NbtOps.INSTANCE, container).result().orElseGet(CompoundTag::new));
    }
}
