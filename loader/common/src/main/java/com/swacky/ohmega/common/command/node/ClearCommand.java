package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public final class ClearCommand implements ICommandNode {
    public static final String ELEMENT_ROOT = "clear";

    private static final String ARGUMENT_TARGETS = "targets";
    private static final String ARGUMENT_FILTER = "filter";
    private static final String ARGUMENT_MAX = "max";

    public static final String ROOT_EXCEPTION_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).exception("multiple");
    public static final String ROOT_EXCEPTION_SINGLE = CommandHelper.command(ELEMENT_ROOT).exception("single");

    public static final String ROOT_FEEDBACK_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).feedback("multiple");
    public static final String ROOT_FEEDBACK_SINGLE = CommandHelper.command(ELEMENT_ROOT).feedback("single");

    private static final DynamicCommandExceptionType EXCEPTION_MULTIPLE = new DynamicCommandExceptionType((count) -> Component.translatable(ROOT_EXCEPTION_MULTIPLE, count));
    private static final DynamicCommandExceptionType EXCEPTION_SINGLE = new DynamicCommandExceptionType(name -> Component.translatable(ROOT_EXCEPTION_SINGLE, name));

    public ClearCommand(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ClearCommand::clear)
                .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entities())
                        .executes(ClearCommand::clearWithTargets)
                        .then(Commands.argument(ARGUMENT_FILTER, ItemPredicateArgument.itemPredicate(context))
                                .executes(ClearCommand::clearWithTargetsFilter)
                                .then(Commands.argument(ARGUMENT_MAX, IntegerArgumentType.integer(0))
                                        .executes(ClearCommand::clearWithTargetsFilterMax))));
    }

    private static int doClear(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities, Predicate<ItemStack> filter, int max) throws CommandSyntaxException {
        List<LivingEntity> targets = CommandHelper.convertLiving(entities);
        int[] count = {0};

        for (LivingEntity target : targets) {
            count[0] += OhmegaDataAttachments.getData(target).clearMatchingItems(target, filter, max, EquipContext.COMMAND);
        }

        int size = targets.size();

        if (count[0] == 0) {
            if (size == 1) {
                throw EXCEPTION_SINGLE.create(targets.getFirst().getDisplayName());
            } else {
                throw EXCEPTION_MULTIPLE.create(size);
            }
        } else {
            CommandSourceStack source = context.getSource();

            if (size == 1) {
                source.sendSuccess(() -> Component.translatable(ROOT_FEEDBACK_SINGLE,
                        count[0],
                        targets.getFirst().getDisplayName()
                ), true);
            } else {
                source.sendSuccess(() -> Component.translatable(ROOT_FEEDBACK_MULTIPLE,
                        count[0],
                        size
                ), true);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int clear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return doClear(context, List.of(context.getSource().getPlayerOrException()), _ -> true, -1);
    }

    private static int clearWithTargets(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);

        return doClear(context, targets, _ -> true, -1);
    }

    private static int clearWithTargetsFilter(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        ItemPredicateArgument.Result predicate = ItemPredicateArgument.getItemPredicate(context, ARGUMENT_FILTER);

        return doClear(context, targets, predicate, -1);
    }

    private static int clearWithTargetsFilterMax(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        ItemPredicateArgument.Result predicate = ItemPredicateArgument.getItemPredicate(context, ARGUMENT_FILTER);
        int max = IntegerArgumentType.getInteger(context, ARGUMENT_MAX);

        return doClear(context, targets, predicate, max);
    }
}
