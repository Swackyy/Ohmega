package com.swacky.ohmega.api.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.common.Ohmega;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

public final class OhmegaRootCommand {
    public static <T extends SharedSuggestionProvider> void registerCommon(
            CommandDispatcher<T> dispatcher, CommandBuildContext context, ICommonCommandSource.Factory<T> sourceFactory) {
        LiteralArgumentBuilder<T> builder = LiteralArgumentBuilder.literal(Ohmega.MODID);

        OhmegaCommandNodes.registerCommonNodes(context, builder, sourceFactory);
        dispatcher.register(builder);
    }

    public static void registerServer(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(Ohmega.MODID);

        OhmegaCommandNodes.registerServerNodes(context, builder);
        dispatcher.register(builder);
    }
}
