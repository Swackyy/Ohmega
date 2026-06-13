package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import net.minecraft.server.WorldLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
    @Inject(
            method = "load",
            at = @At(
                    value = "HEAD"))
    private static void load(WorldLoader.InitConfig config, WorldLoader.WorldDataSupplier<?> worldDataSupplier, WorldLoader.ResultFactory<?, ?> resultFactory, Executor backgroundExecutor, Executor mainThreadExecutor, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        AccessoryTypeManager.lockEvents();
    }
}
