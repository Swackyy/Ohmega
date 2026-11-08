package com.swacky.ohmega.client.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.CommandUtils;
import com.swacky.ohmega.api.common.command.ICommonCommandSource;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.net.URI;
import java.util.function.UnaryOperator;

public final class InfoCommand implements ICommandNode {
    public static final String ELEMENT_ROOT = "info";
    public static final String ELEMENT_CROWDIN = "crowdin";
    public static final String ELEMENT_DISCORD = "discord";
    public static final String ELEMENT_REPORT = "report";
    public static final String ELEMENT_WIKI = "wiki";

    public static final String CROWDIN_FEEDBACK = CommandUtils.command(ELEMENT_ROOT).add(ELEMENT_CROWDIN).feedback();
    public static final String DISCORD_FEEDBACK = CommandUtils.command(ELEMENT_ROOT).add(ELEMENT_DISCORD).feedback();
    public static final String REPORT_FEEDBACK = CommandUtils.command(ELEMENT_ROOT).add(ELEMENT_REPORT).feedback();
    public static final String WIKI_FEEDBACK = CommandUtils.command(ELEMENT_ROOT).add(ELEMENT_WIKI).feedback();

    public <T extends SharedSuggestionProvider> InfoCommand(CommandBuildContext context, LiteralArgumentBuilder<T> builder, CommandHelper<T> helper) {
        builder
                .then(helper.literal(ELEMENT_CROWDIN)
                        .executes(helper.executor(InfoCommand::crowdin)))
                .then(helper.literal(ELEMENT_DISCORD)
                        .executes(helper.executor(InfoCommand::discord)))
                .then(helper.literal(ELEMENT_REPORT)
                        .executes(helper.executor(InfoCommand::report)))
                .then(helper.literal(ELEMENT_WIKI)
                        .executes(helper.executor(InfoCommand::wiki)));
    }

    private static UnaryOperator<Style> createClickable(String url) {
        return style -> style.withClickEvent(new ClickEvent.OpenUrl(URI.create(url)));
    }

    private static int crowdin(ICommonCommandSource source) {
        source.sendSuccess(() -> Component.translatable(CROWDIN_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(
                createClickable("https://crowdin.com/project/ohmega")));
        return Command.SINGLE_SUCCESS;
    }

    private static int discord(ICommonCommandSource source) {
        source.sendSuccess(() -> Component.translatable(DISCORD_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(
                createClickable("https://discord.gg/B9669WDmZk")));
        return Command.SINGLE_SUCCESS;
    }

    private static int report(ICommonCommandSource source) {
        source.sendSuccess(() -> Component.translatable(REPORT_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(
                createClickable("https://github.com/Swackyy/Ohmega/issues")));
        return Command.SINGLE_SUCCESS;
    }

    private static int wiki(ICommonCommandSource source) {
        source.sendSuccess(() -> Component.translatable(WIKI_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(
                createClickable("https://github.com/Swackyy/Ohmega/wiki")));
        return Command.SINGLE_SUCCESS;
    }
}
