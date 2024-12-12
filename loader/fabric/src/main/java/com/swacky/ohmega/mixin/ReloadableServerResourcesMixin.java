package com.swacky.ohmega.mixin;

import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @Inject(method = "listeners", at = @At(value = "RETURN"), cancellable = true)
    public void listeners(CallbackInfoReturnable<List<PreparableReloadListener>> cir) {
        ArrayList<PreparableReloadListener> list = new ArrayList<>(cir.getReturnValue());
        list.add(AccessoryTypeManager.getInstance());
        cir.setReturnValue(list);
    }
}
