package com.swacky.ohmega.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.common.command.OhmegaCommandNodes;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class OhmegaRootCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(Ohmega.MODID);

        OhmegaCommandNodes.registerNodes(context, builder);
        dispatcher.register(builder);
    }
}
