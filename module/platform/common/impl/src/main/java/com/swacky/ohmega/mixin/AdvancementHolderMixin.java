package com.swacky.ohmega.mixin;

import com.swacky.ohmega.mixinduck.DisplayInfoExtension;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementHolder.class)
abstract class AdvancementHolderMixin {
    @Inject(
            method = "<init>",
            at = @At(
                    value = "RETURN"))
    private void init(Identifier id, Advancement value, CallbackInfo ci) {
        value.display().ifPresent(info -> ((DisplayInfoExtension) info).ohmega$setAdvancementId(id));
    }
}
