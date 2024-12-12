package com.swacky.ohmega.mixin.client;

import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaBinds;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyBindsList.class)
public class KeyBindsListMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent KeyMapping(String key) {
        // Very clunky way of doing this, but it works
        if (key.startsWith(OhmegaCommon.MODID, 4) && !key.equals(OhmegaBinds.OPEN_ACC_INV.getName())) {
            return Component.translatable(key.substring(0, key.indexOf('_')), Integer.parseInt(key.substring(key.indexOf('_') + 1)) + 1);
        }
        return Component.translatable(key);
    }
}
