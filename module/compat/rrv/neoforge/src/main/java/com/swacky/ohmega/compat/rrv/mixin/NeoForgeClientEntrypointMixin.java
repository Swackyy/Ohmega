package com.swacky.ohmega.compat.rrv.mixin;

import cc.cassian.rrv.neoforge.NeoForgeClientEntrypoint;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.swacky.ohmega.api.client.screen.AccessoryScreens;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NeoForgeClientEntrypoint.class)
abstract class NeoForgeClientEntrypointMixin {
    @Definition(id = "getScreen", method = "Lnet/neoforged/neoforge/client/event/ScreenEvent$Render$Background;getScreen()Lnet/minecraft/client/gui/screens/Screen;")
    @Expression("?.getScreen() instanceof ?")
    @WrapOperation(
            method = "extractBackground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/event/ScreenEvent$Render$Background;getScreen()Lnet/minecraft/client/gui/screens/Screen;"))
    private static Screen onInitializeClient(ScreenEvent.Render.Background instance, Operation<Screen> handle) {
        return AccessoryScreens.getEffectiveScreen(handle.call(instance));
    }
}
