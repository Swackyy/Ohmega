package com.swacky.ohmega.common.item;

import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.AccessoryType;
import com.swacky.ohmega.api.IAccessory;
import com.swacky.ohmega.common.core.Ohmega;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// This is simply a test and example accessory. Its code may be used as a reference to create your own. You may also add a recipe to this if desired for survival use.
public class AngelRing extends Item implements IAccessory {
    public AngelRing() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(AccessoryHelper.getBindTooltip(new TranslatableComponent("item." + Ohmega.MODID + ".flyring.tooltip.keybind"), stack, new TranslatableComponent("item." + Ohmega.MODID + ".flyring.tooltip")));
        tooltip.add(AccessoryHelper.getTypeTooltip(this));
    }

    @Override
    public @NotNull AccessoryType getType() {
        return AccessoryType.UTILITY;
    }

    @Override
    public void onEquip(Player player, ItemStack stack) {
        if(player instanceof ServerPlayer svr) {
            AccessoryHelper.activate(svr, stack);
        }
    }

    @Override
    public void onUnequip(Player player, ItemStack stack) {
        if(!(player.isCreative() || player.isSpectator())) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            if(player instanceof ServerPlayer svr) {
                AccessoryHelper.deactivate(svr, stack);
            }
        }
    }

    @Override
    public void tick(Player player, ItemStack stack) {
        if(player.isCreative() || player.isSpectator()) {
            player.getAbilities().mayfly = true;
            if(player instanceof ServerPlayer svr) {
                AccessoryHelper.deactivate(svr, stack);
            }
        } else if(AccessoryHelper.isActive(player, stack)) {
            player.getAbilities().mayfly = true;
        } else {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
        }
    }

    @Override
    public void onUse(Player player, ItemStack stack) {
        if(player instanceof ServerPlayer svr) {
            AccessoryHelper.toggle(svr, stack);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return AccessoryHelper.tryEquip(player, hand);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return AccessoryHelper.isTagActive(stack);
    }

    @Nullable
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GOLD;
    }
}
