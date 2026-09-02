package com.swacky.ohmega.compat.rrv.mixin;

import cc.cassian.rrv.fabric.FabricClientEntrypoint;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FabricClientEntrypoint.class)
abstract class FabricClientEntrypointMixin {
    @Definition(id = "screen", local = @Local(type = Screen.class, argsOnly = true))
    @Expression("screen")
    @ModifyExpressionValue(
            method = "lambda$onInitializeClient$4",
            at = @At(
                    value = "MIXINEXTRAS:EXPRESSION",
                    ordinal = 0))
    private static Screen onInitializeClient(Screen screen) {
        return AccessoryScreens.getEffectiveScreen(screen);
    }
}
