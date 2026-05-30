package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ItemsCommand implements ICommandNode {
    public static final String ELEMENT_ROOT = "items";

    private static final String ARGUMENT_TARGET = "target";
    private static final String ARGUMENT_INCLUDE_AIR = "includeAir";

    public static final String ROOT_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).feedback();
    public static final String ROOT_FEEDBACK_EMPTY = CommandHelper.command(ELEMENT_ROOT).feedback("empty");

    public ItemsCommand(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ItemsCommand::print)
                .then(Commands.argument(ARGUMENT_TARGET, EntityArgument.entity())
                        .executes(ItemsCommand::printWithEntity)
                        .then(Commands.argument(ARGUMENT_INCLUDE_AIR, BoolArgumentType.bool())
                                .executes(ItemsCommand::printWithEntityIncludeAir)));
    }

    private static int doPrint(CommandContext<CommandSourceStack> context, Entity target, boolean includeAir) throws CommandSyntaxException {
        NonNullList<ItemStack> stacks;
        LivingEntity entity = CommandHelper.convertLiving(target);
        CommandSourceStack source = context.getSource();

        if (includeAir) {
            stacks = AccessoryHelper.getData(entity).getStacks();
        } else {
            stacks = AccessoryHelper.getStacksNoEmpty(entity);

            if (stacks.isEmpty()) {
                source.sendSuccess(() -> Component.translatable(ROOT_FEEDBACK_EMPTY, target.getDisplayName()), true);
                return Command.SINGLE_SUCCESS;
            }
        }

        List<Component> components = new ArrayList<>(stacks.size());

        for (ItemStack stack : stacks) {
            components.add(Component.literal(stack.count() + " ").append(stack.getDisplayName()));
        }

        source.sendSuccess(() -> Component.translatable(ROOT_FEEDBACK, target.getDisplayName(), ComponentUtils.formatList(components, Component.literal(", "))), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int print(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
