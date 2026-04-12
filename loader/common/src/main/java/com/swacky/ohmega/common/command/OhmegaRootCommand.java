package com.swacky.ohmega.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class OhmegaRootCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
                Commands.literal(Ohmega.MODID)
                        .then(ClearCommand.create(context))
                        .then(InfoCommand.create())
                        .then(ItemCommand.create(context))
                        .then(ItemsCommand.create())
                        .then(TypeCommand.create()));
    }
}
