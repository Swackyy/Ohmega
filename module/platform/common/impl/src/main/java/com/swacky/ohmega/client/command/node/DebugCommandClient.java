package com.swacky.ohmega.client.command.node;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.ICommonCommandSource;
import com.swacky.ohmega.common.command.node.DebugCommandBase;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;

public final class DebugCommandClient extends DebugCommandBase {
    public <T extends SharedSuggestionProvider> DebugCommandClient(
            CommandBuildContext context, LiteralArgumentBuilder<T> builder, CommandHelper<T> helper) {
        builder
                .then(helper.literal(ELEMENT_DATA)
                        .executes(helper.executor(DebugCommandClient::data))
                        .then(helper.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                .executes(helper.executor(DebugCommandClient::dataWithIndex))));
    }

    private static int data(ICommonCommandSource source) throws CommandSyntaxException {
        return doData(source::sendSuccess, source.getPlayer(), -1, false);
    }

    private static int dataWithIndex(CommandContext<?> context, ICommonCommandSource source) throws CommandSyntaxException {
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);

        return doData(source::sendSuccess, source.getPlayer(), index, false);
    }
}
