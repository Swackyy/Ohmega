package com.swacky.ohmega.mixin.client;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.event.ClientCallbacks;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModConfig.class)
abstract class ModConfigMixin {
    @Shadow
    public abstract <T extends IConfigSpec<T>> IConfigSpec<T> getSpec();

    @Inject(method = "setConfigData", at = @At(value = "TAIL"))
    public void setConfigData(CommentedConfig data, CallbackInfo ci) {
        if (getSpec() == OhmegaConfigImpl.Server.getSpec() && data == null) {
            ClientCallbacks.onServerConfigUnload(Minecraft.getInstance().options::load);
        }
    }
}
