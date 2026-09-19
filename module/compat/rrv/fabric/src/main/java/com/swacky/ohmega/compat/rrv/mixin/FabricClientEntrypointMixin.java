package com.swacky.ohmega.compat.rrv.mixin;

import cc.cassian.rrv.fabric.FabricClientEntrypoint;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FabricClientEntrypoint.class)
abstract class FabricClientEntrypointMixin {
    @ModifyVariable(
            method = "lambda$onInitializeClient$3",
            at = @At(
                    value = "LOAD",
                    ordinal = 0),
            argsOnly = true)
    private static Screen onInitializeClient(Screen screen) {
        return AccessoryScreens.getEffectiveScreen(screen);
    }
}
