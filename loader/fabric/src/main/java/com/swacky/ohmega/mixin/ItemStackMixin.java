package com.swacky.ohmega.mixin;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements DataComponentHolder, ItemInstance {
    @Inject(
            method = "addAttributeTooltips",
            at = @At(
                    value = "RETURN"))
    private void addAttributeTooltips(Consumer<Component> consumer, TooltipDisplay display, @Nullable Player player, CallbackInfo ci) {
        DataComponentType<ItemAttributeModifiers> type = OhmegaDataComponents.getAccessoryActiveModifiers();

        if (display.shows(type)) {
            final boolean[] flag = {true};

            getOrDefault(type, ItemAttributeModifiers.EMPTY).forEach(EquipmentSlotGroup.ANY, (attribute, modifier, tooltip) -> {
                if (tooltip != ItemAttributeModifiers.Display.hidden()) {
                    if (flag[0]) {
                        flag[0] = false;

                        consumer.accept(CommonComponents.EMPTY);
                        consumer.accept(Component.translatable(Ohmega.MODID + ".item.modifiers.accessory_active").withStyle(ChatFormatting.GRAY));
                    }

                    tooltip.apply(consumer, player, attribute, modifier);
                }
            });
        }
    }
}
