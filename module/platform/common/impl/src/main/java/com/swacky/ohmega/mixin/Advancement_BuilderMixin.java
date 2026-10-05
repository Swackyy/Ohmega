package com.swacky.ohmega.mixin;

import com.swacky.ohmega.mixinduck.DisplayInfoExtension;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Advancement.Builder.class)
abstract class Advancement_BuilderMixin {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    public Optional<DisplayInfo> display;

    @Inject(
            method = "build",
            at = @At(
                    value = "HEAD"))
    private void init(Identifier id, CallbackInfoReturnable<AdvancementHolder> cir) {
        display.ifPresent(info -> ((DisplayInfoExtension) info).ohmega$setAdvancementId(id));
    }
}
