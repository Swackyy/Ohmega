package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.swacky.ohmega.api.common.command.CommandUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public final class DebugCommand extends DebugCommandBase {
    private static final String ARGUMENT_TARGET = "target";

    public DebugCommand(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .then(Commands.literal(ELEMENT_DATA)
                        .executes(DebugCommand::data)
                        .then(Commands.argument(ARGUMENT_TARGET, EntityArgument.entity())
                                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(DebugCommand::dataWithEntity)
                                .then(Commands.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                        .executes(DebugCommand::dataWithEntityIndex))));
    }

    private static int data(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        return doData(source::sendSuccess, source.getPlayer(), -1, false);
    }

    private static int dataWithEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity target = EntityArgument.getEntity(context, ARGUMENT_TARGET);

        return doData(source::sendSuccess, target, -1, !Objects.equals(source.getEntity(), target));
    }

    private static int dataWithEntityIndex(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        LivingEntity target = CommandUtils.convertLiving(EntityArgument.getEntity(context, ARGUMENT_TARGET));
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);

        return doData(source::sendSuccess, target, index, !Objects.equals(source.getEntity(), target));
    }
}
