package com.swacky.ohmega.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.mixinduck.DisplayInfoExtension;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(DisplayInfo.class)
abstract class DisplayInfoMixin implements DisplayInfoExtension {
    @Unique
    private @Nullable Identifier ohmega$id;

    @Override
    public void ohmega$setAdvancementId(@NonNull Identifier id) {
        ohmega$id = id;
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @ModifyReturnValue(
            method = "getDescription",
            at = @At(
                    value = "RETURN"))
    private Component getDescription(Component original) {
        if (ohmega$id != null) {
            List<@NonNull ObjectIntImmutablePair<@NonNull AccessoryType>> list = AccessoryTypeManager.getAdvancementReward(ohmega$id);

            if (!list.isEmpty()) {
                MutableComponent formattedRewards = MutableComponent.create(PlainTextContents.EMPTY);

                for (ObjectIntImmutablePair<@NonNull AccessoryType> entry : list) {
                    formattedRewards.append(Component.literal("\n\u2022 " + entry.secondInt() + " '" + entry.left().getId() + '\''));
                }

                return original.copy()
                        .append(Component.literal("\n"))
                        .append(Component.translatable("advancement." + Ohmega.MODID + ".slot_rewards", formattedRewards)
                                .withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        return original;
    }
}
