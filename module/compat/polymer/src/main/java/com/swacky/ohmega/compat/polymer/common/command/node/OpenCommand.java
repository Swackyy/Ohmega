
package com.swacky.ohmega.compat.polymer.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import com.swacky.ohmega.compat.polymer.common.AccessorySGui;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public final class OpenCommand implements ICommandNode {
    public static final String ELEMENT_ROOT = "open";

    public OpenCommand(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.executes(OpenCommand::open);
    }

    private static int open(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        if (source.isPlayer()) {
            AccessorySGui.open(source.getPlayer());
        }

        return Command.SINGLE_SUCCESS;
    }
}
