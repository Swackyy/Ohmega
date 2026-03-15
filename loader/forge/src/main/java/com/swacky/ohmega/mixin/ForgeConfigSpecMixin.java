package com.swacky.ohmega.mixin;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.event.ClientCallbacks;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.IConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeConfigSpec.class)
abstract class ForgeConfigSpecMixin extends UnmodifiableConfigWrapper<UnmodifiableConfig> implements IConfigSpec<ForgeConfigSpec> {
    protected ForgeConfigSpecMixin(UnmodifiableConfig config) {
        super(config);
    }

    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "acceptConfig",
            at = @At(
                    value = "TAIL"),
            remap = false)
    public void acceptConfig(CommentedConfig data, CallbackInfo ci) {
        if (data == null && (Object) this == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigUnload(() -> Minecraft.getInstance().options.load(true));
        }
    }
}
