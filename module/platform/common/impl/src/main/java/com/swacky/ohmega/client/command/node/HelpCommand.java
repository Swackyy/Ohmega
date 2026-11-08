package com.swacky.ohmega.client.command.node;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;

// todo: write this
public final class HelpCommand implements ICommandNode {
    public static final String ELEMENT_ROOT = "help";

    public <T extends SharedSuggestionProvider> HelpCommand(CommandBuildContext context, LiteralArgumentBuilder<T> builder, CommandHelper<T> helper) {
        // todo: write this
    }
}
