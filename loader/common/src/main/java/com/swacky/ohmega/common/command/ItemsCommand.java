package com.swacky.ohmega.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.swacky.ohmega.api.AccessoryHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemsCommand {
    public static final String ELEMENT_ROOT = "items";

    private static final String ARGUMENT_TARGET = "target";
    private static final String ARGUMENT_INCLUDE_AIR = "includeAir";

    public static final String ROOT_FEEDBACK = MessageHelper.command(ELEMENT_ROOT).feedback();

    public static ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal(ELEMENT_ROOT)
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ItemsCommand::print)
                .then(Commands.argument(ARGUMENT_TARGET, EntityArgument.entity())
                        .executes(ItemsCommand::printWithEntity)
                        .then(Commands.argument(ARGUMENT_INCLUDE_AIR, BoolArgumentType.bool())
                                .executes(ItemsCommand::printWithEntityIncludeAir)));
    }

    private static int doPrint(CommandContext<CommandSourceStack> context, Entity target, boolean includeAir) {
        NonNullList<ItemStack> stacks;

        // todo:
        if (target instanceof Player player) {
            if (includeAir) {
                stacks = AccessoryHelper.getStacks(player);
            } else {
                stacks = AccessoryHelper.getStacksFiltered(player);
            }
        } else return 0;

        List<Component> components = new ArrayList<>(stacks.size());

        for (ItemStack stack : stacks) {
            components.add(Component.literal(stack.count() + " ").append(stack.getDisplayName()));
        }

        context.getSource().sendSuccess(() -> Component.translatable(ROOT_FEEDBACK, target.getDisplayName(), ComponentUtils.formatList(components, Component.literal(", "))), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int print(CommandContext<CommandSourceStack> context) {
        return doPrint(context, context.getSource().getEntity(), false);
    }

    private static int printWithEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, ARGUMENT_TARGET);

        return doPrint(context, target, false);
    }

    private static int printWithEntityIncludeAir(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, ARGUMENT_TARGET);
        boolean includeAir = BoolArgumentType.getBool(context, ARGUMENT_INCLUDE_AIR);

        return doPrint(context, target, includeAir);
    }
}
