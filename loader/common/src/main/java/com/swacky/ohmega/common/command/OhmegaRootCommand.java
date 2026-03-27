package com.swacky.ohmega.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

// todo: some commands mostly designed to allow for any entity and not just players, but only players work at the moment.
// todo: make them work for any general entities when support is added
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
