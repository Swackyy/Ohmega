package com.swacky.ohmega.client.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.client.command.IClientCommandSource;
import com.swacky.ohmega.api.client.command.node.IClientCommandNode;
import com.swacky.ohmega.api.common.command.CommandHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.net.URISyntaxException;

public final class InfoCommand implements IClientCommandNode {
    public static final String ELEMENT_ROOT = "info";
    public static final String ELEMENT_CROWDIN = "crowdin";
    public static final String ELEMENT_DISCORD = "discord";
    public static final String ELEMENT_REPORT = "report";
    public static final String ELEMENT_WIKI = "wiki";

    public static final String CROWDIN_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_CROWDIN).feedback();
    public static final String DISCORD_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_DISCORD).feedback();
    public static final String REPORT_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_REPORT).feedback();
    public static final String WIKI_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_WIKI).feedback();

    public <T extends SharedSuggestionProvider> InfoCommand(CommandBuildContext context, LiteralArgumentBuilder<T> builder, IClientCommandSource.Factory<T> sourceFactory) {
        builder
                .then(LiteralArgumentBuilder.<T>literal(ELEMENT_CROWDIN)
                        .executes(cmdContext -> crowdin(sourceFactory.construct(cmdContext))))
                .then(LiteralArgumentBuilder.<T>literal(ELEMENT_DISCORD)
                        .executes(cmdContext -> discord(sourceFactory.construct(cmdContext))))
                .then(LiteralArgumentBuilder.<T>literal(ELEMENT_REPORT)
                        .executes(cmdContext -> report(sourceFactory.construct(cmdContext))))
                .then(LiteralArgumentBuilder.<T>literal(ELEMENT_WIKI)
                        .executes(cmdContext -> wiki(sourceFactory.construct(cmdContext))));
    }

    private static int crowdin(IClientCommandSource source) {
        source.sendSuccess(Component.translatable(CROWDIN_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(style -> {
            try {
                return style.withClickEvent(
                        new ClickEvent.OpenUrl(new URI("https://crowdin.com/project/ohmega"))
                );
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }));

        return Command.SINGLE_SUCCESS;
    }

    private static int discord(IClientCommandSource source) {
        source.sendSuccess(Component.translatable(DISCORD_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(style -> {
            try {
                return style.withClickEvent(
                        new ClickEvent.OpenUrl(new URI("https://discord.gg/B9669WDmZk"))
                );
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }));

        return Command.SINGLE_SUCCESS;
    }

    private static int report(IClientCommandSource source) {
        source.sendSuccess(Component.translatable(REPORT_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(style -> {
            try {
                return style.withClickEvent(
                        new ClickEvent.OpenUrl(new URI("https://github.com/Swackyy/Ohmega/issues"))
                );
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }));

        return Command.SINGLE_SUCCESS;
    }

    private static int wiki(IClientCommandSource source) {
        source.sendSuccess(Component.translatable(WIKI_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(style -> {
            try {
                return style.withClickEvent(
                        new ClickEvent.OpenUrl(new URI("https://github.com/Swackyy/Ohmega/wiki"))
                );
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }));

        return Command.SINGLE_SUCCESS;
    }
}
