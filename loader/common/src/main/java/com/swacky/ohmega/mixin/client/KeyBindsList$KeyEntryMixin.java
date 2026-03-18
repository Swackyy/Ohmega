package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaBinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsList.KeyEntry.class)
abstract class KeyBindsList$KeyEntryMixin extends KeyBindsList.Entry {
    @Inject(
            method = "<init>",
            at = @At(
                    value = "RETURN"))
    private void KeyMapping(KeyBindsList list, KeyMapping mapping, Component name, CallbackInfo ci) {
        if (OhmegaBinds.isInstance(mapping)) {
            String key = mapping.getName();
            int index = key.lastIndexOf('_');
            ((KeyBindsList.KeyEntry) (Object) this).name = new TranslatableComponent(
                    "key." + OhmegaCommon.MODID + ".accessory_type",
                    new TranslatableComponent(key.substring(0, index).replace("key", "accessory_type")),
                    Integer.parseInt(key.substring(index + 1)) + 1);
        }
    }
}
