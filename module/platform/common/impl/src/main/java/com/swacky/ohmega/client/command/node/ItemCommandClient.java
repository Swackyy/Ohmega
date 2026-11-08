package com.swacky.ohmega.client.command.node;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.ICommonCommandSource;
import com.swacky.ohmega.common.command.node.ItemCommandBase;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.ItemArgument;

public class ItemCommandClient extends ItemCommandBase {
    public <T extends SharedSuggestionProvider> ItemCommandClient(
            CommandBuildContext context, LiteralArgumentBuilder<T> builder, CommandHelper<T> helper) {
        builder
                .then(helper.literal(ELEMENT_TYPE)
                        .then(helper.argument(ARGUMENT_ITEM, ItemArgument.item(context))
                                .executes(helper.executor(ItemCommandClient::type))));
    }

    private static int type(CommandContext<?> context, ICommonCommandSource source) {
        return type(context, source::sendSuccess);
    }
}
