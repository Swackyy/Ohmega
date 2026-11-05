package com.swacky.ohmega.compat.util.mixinconfig;

import com.swacky.ohmega.api.util.OhmegaPlatform;
import org.jspecify.annotations.NonNull;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

// todo: implement some platform util thing (serviced) that allows checking if a mod is loaded bc this be very broken

/**
 * Implements default method bodies to reduce clutter and adds
 */
public class OhmegaMixinConfigPlugin implements IMixinConfigPlugin {
    private final @NonNull String requiredModId;

    public OhmegaMixinConfigPlugin(@NonNull String requiredModId) {
        this.requiredModId = requiredModId;
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return OhmegaPlatform.isModLoaded(requiredModId);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
