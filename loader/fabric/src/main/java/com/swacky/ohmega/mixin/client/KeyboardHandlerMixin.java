package com.swacky.ohmega.mixin.client;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.inv.AccessoryContainer;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.UseAccessoryKbPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At(value = "TAIL"))
    public void keyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null) {
            while (OhmegaBinds.OPEN_ACC_INV.consumeClick() && mc.player != null) {
                if (mc.gameMode != null && mc.gameMode.isServerControlledInventory()) {
                    mc.player.sendOpenInventory();
                } else if (!mc.player.isCreative() && !mc.player.isSpectator()) {
                    ClientPlayNetworking.send(new OpenAccessoryInventoryPacket());
                } else {
                    mc.setScreen(new InventoryScreen(mc.player));
                }
            }

            ImmutableList<KeyMapping> mappings = OhmegaBinds.Generated.getMappings();
            ImmutableList<String> slotTypes = AccessoryHelper.getSlotTypesStr();
            if (mappings.isEmpty() || slotTypes.isEmpty()) {
                return;
            }

            // Never ever touch this again; wrote 2 months ago, I now consider it dark magic.
            AccessoryContainer a = AccessoryHelper.getContainer(mc.player);
            for (int i = 0; i < OhmegaBinds.Generated.size(); i++) {
                int j = 0;
                KeyMapping mapping = mappings.get(i);
                if (mapping.consumeClick()) {
                    // Client handling
                    if (mc.player != null) {
                        for (int k = 0; true; j++) {
                            if (AccessoryHelper.getKeyboundSlotTypesStr().contains(slotTypes.get(j)) && ++k > i) {
                                break;
                            }
                        }

                        ItemStack stack = a.getStackInSlot(j);

                        IAccessory acc = AccessoryHelper.getBoundAccessory(stack.getItem());
                        if (acc != null && !OhmegaHooks.accessoryUseEvent(mc.player, stack)) {
                            acc.onUse(mc.player, stack);
                        }
                    }

                    // Server handling
                    ClientPlayNetworking.send(new UseAccessoryKbPacket(j));
                }
            }
        }
    }
}
