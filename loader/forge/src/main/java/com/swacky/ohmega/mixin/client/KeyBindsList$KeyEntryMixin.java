package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsList.KeyEntry.class)
abstract class KeyBindsList$KeyEntryMixin extends KeyBindsList.Entry {
    @Final
    @Mutable
    @Shadow
    private Component name;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "RETURN"))
    private void KeyMapping(KeyBindsList list, KeyMapping mapping, Component name, CallbackInfo ci) {
        if (OhmegaBinds.isInstance(mapping)) {
            String key = mapping.getName();
            int index = key.lastIndexOf('_');
            this.name = Component.translatable(
                    "key." + OhmegaCommon.MODID + ".accessory_type",
                    Component.translatable(key.substring(0, index).replace("key", "accessory_type")),
                    Integer.parseInt(key.substring(index + 1)) + 1);
        }
    }
}
