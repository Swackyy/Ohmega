package com.swacky.ohmega.mixin;

import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;
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

    @Inject(
            method = "lambda$load$4",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/WorldLoader$ResultFactory;create(Lnet/minecraft/server/packs/resources/CloseableResourceManager;Lnet/minecraft/server/ReloadableServerResources;Lnet/minecraft/core/LayeredRegistryAccess;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static void lambda$load$4(WorldLoader.ResultFactory<?, ?> resultFactory, CloseableResourceManager resources, LayeredRegistryAccess<?> resourcesLoadContext, WorldLoader.DataLoadOutput<?> worldDataAndRegistries, ReloadableServerResources managers, CallbackInfoReturnable<Object> cir) {
    }
}
