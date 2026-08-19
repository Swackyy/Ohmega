package com.swacky.ohmega.api.client.item;

import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.init.OhmegaDataComponents;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.Accessory;
import com.swacky.ohmega.api.config.OhmegaConfig;
import com.swacky.ohmega.api.datagen.client.OhmegaLangHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Contains some client-only utility methods for accessory items that don't really fit anywhere else within the API.
 * If you can think of a better place to put these methods, I'd be open to refactors
 */
public final class AccessoryHelper {
    /**
     * A utility method used to get a description for key-bound capable accessories
     * @param stack {@link ItemStack} instance of an accessory
     * @param bindKey the translatable key for use when a key-bind is applicable for this stack,
     * use '%s' for the bind key replacement in your translation
     * @param nonBindKey the translatable key for use when a key-bind is not applicable for this stack
     * @return example: "Press G to toggle flight", "Allows the wearer to fly"
     * @apiNote You should most likely use {@link #getBindTooltip(ItemStack)} as it is easier
     * and uses standardised key formats that work with {@link OhmegaLangHelper}
     */
    public static @NonNull MutableComponent getBindTooltip(@NonNull ItemStack stack, @NonNull String bindKey, @NonNull String nonBindKey) {
        Player player = Minecraft.getInstance().player;

        if (player != null) {
            int slot = OhmegaDataComponents.getSlotIndex(stack);
            AccessoryData data = OhmegaDataAttachments.getData(player);
            AccessoryType type;

            if (slot < 0 || slot >= data.size()) {
                type = null;
            } else {
                type = data.getEntry(slot).getType();
            }

            int typeIndex = 0;

            if (type != null) {
                for (int i = 0; i < slot; i++) {
                    if (data.getEntry(i).getType().equals(type)) {
                        typeIndex++;
                    }
                }
            }

            Accessory accessory = Accessories.get(stack.getItem());
            boolean flag = false;

            if (accessory != null) {
                for (AccessoryType keyboundType : OhmegaConfig.Server.getKeyboundSlotTypes()) {
                    if (data.getTypes().contains(keyboundType)) {
                        flag = true;
                        break;
                    }
                }
            }

            KeyMapping mapping;

            if (type == null) {
                mapping = null;
            } else {
                mapping = OhmegaBinds.getMapping(type, typeIndex);
            }

            if (slot < 0 || !flag || mapping == null) {
                return Component.translatable(nonBindKey).withStyle(ChatFormatting.GRAY);
            }

            return Component.translatable(bindKey, mapping.getTranslatedKeyMessage()).withStyle(ChatFormatting.GRAY);
        }

        return Component.translatable(nonBindKey).withStyle(ChatFormatting.GRAY);
    }

    /**
     * A shortcut method to {@link #getBindTooltip(ItemStack, String, String)} that uses standardised key formats
     * that work with {@link OhmegaLangHelper}
     * @param stack {@link ItemStack} instance of an accessory
     * @return example: "Press G to toggle flight", "Allows the wearer to fly"
     */
    public static @NonNull MutableComponent getBindTooltip(@NonNull ItemStack stack) {
        String id = stack.getItem().getDescriptionId();

        return getBindTooltip(stack, id + ".tooltip.keybind", id + ".tooltip");
    }

    /**
     * This is automatically applied internally
     * @param item accessory item
     * @return example: "Accessory Type: Utility"
     */
    public static @Nullable MutableComponent getTypeTooltip(@NonNull Item item) {
        AccessoryType type = Accessories.getType(Minecraft.getInstance().player, item);

        if (type.displayHoverText()) {
            return Component.translatable("accessory_type", type.getTranslation().getString()).withStyle(ChatFormatting.DARK_GRAY);
        }

        return null;
    }
}
