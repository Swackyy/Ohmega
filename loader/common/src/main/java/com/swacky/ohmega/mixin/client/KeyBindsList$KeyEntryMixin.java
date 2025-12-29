package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.common.init.OhmegaBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Not applied on NeoForge as their extension allows custom display names
@SuppressWarnings("UnusedMixin")
@Mixin(KeyBindsList.KeyEntry.class)
abstract class KeyBindsList$KeyEntryMixin extends KeyBindsList.Entry {
    @Shadow
    private Component name;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void KeyMapping(KeyBindsList list, KeyMapping mapping, Component name, CallbackInfo ci) {
        if (OhmegaBinds.isInstance(mapping)) {
            String key = mapping.getName();
            int index = key.lastIndexOf('_');
            this.name = Component.translatable(key.substring(0, index), Integer.parseInt(key.substring(index + 1)) + 1);
        }
    }
}
