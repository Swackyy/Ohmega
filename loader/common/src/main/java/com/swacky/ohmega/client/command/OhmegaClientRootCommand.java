package com.swacky.ohmega.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.client.command.IClientCommandSource;
import com.swacky.ohmega.api.client.command.OhmegaClientCommandNodes;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;

public final class OhmegaClientRootCommand {
    public static <T extends SharedSuggestionProvider> void register(CommandDispatcher<T> dispatcher, CommandBuildContext context, IClientCommandSource.Factory<T> sourceFactory) {
        LiteralArgumentBuilder<T> builder = LiteralArgumentBuilder.literal(Ohmega.MODID + 'c');

        OhmegaClientCommandNodes.registerNodes(context, builder, sourceFactory);
        dispatcher.register(builder);
    }
}
