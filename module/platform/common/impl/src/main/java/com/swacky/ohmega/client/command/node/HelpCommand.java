package com.swacky.ohmega.client.command.node;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.client.command.IClientCommandSource;
import com.swacky.ohmega.api.client.command.node.IClientCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;

// todo: write this
public class HelpCommand implements IClientCommandNode {
    public static final String ELEMENT_ROOT = "help";

    public <T extends SharedSuggestionProvider> HelpCommand(CommandBuildContext context, LiteralArgumentBuilder<T> builder, IClientCommandSource.Factory<T> sourceFactory) {
        // todo: write this
    }
}
