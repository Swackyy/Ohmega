package com.swacky.ohmega.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.net.URISyntaxException;

public class InfoCommand {
    public static final String ELEMENT_ROOT = "info";
    public static final String ELEMENT_DISCORD = "discord";
    public static final String ELEMENT_REPORT = "report";
    public static final String ELEMENT_WIKI = "wiki";

    public static final String DISCORD_FEEDBACK = MessageHelper.command(ELEMENT_ROOT).add(ELEMENT_DISCORD).feedback();
    public static final String REPORT_FEEDBACK = MessageHelper.command(ELEMENT_ROOT).add(ELEMENT_REPORT).feedback();
    public static final String WIKI_FEEDBACK = MessageHelper.command(ELEMENT_ROOT).add(ELEMENT_WIKI).feedback();

    public static ArgumentBuilder<CommandSourceStack, ?> create() {
        return Commands.literal(ELEMENT_ROOT)
                .then(Commands.literal(ELEMENT_DISCORD)
                        .executes(InfoCommand::discord))
                .then(Commands.literal(ELEMENT_REPORT)
                        .executes(InfoCommand::report))
                .then(Commands.literal(ELEMENT_WIKI)
                        .executes(InfoCommand::wiki));
    }

    private static int discord(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() ->
                Component.translatable(DISCORD_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(style -> {
                    try {
                        return style.withClickEvent(
                                new ClickEvent.OpenUrl(new URI("https://discord.gg/B9669WDmZk"))
                        );
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int report(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() ->
                Component.translatable(REPORT_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(style -> {
                    try {
                        return style.withClickEvent(
                                new ClickEvent.OpenUrl(new URI("https://github.com/Swackyy/Ohmega/issues"))
                        );
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int wiki(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() ->
                Component.translatable(WIKI_FEEDBACK).withStyle(ChatFormatting.UNDERLINE).withStyle(style -> {
                    try {
                        return style.withClickEvent(
                                new ClickEvent.OpenUrl(new URI("https://github.com/Swackyy/Ohmega/wiki"))
                        );
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }), false);
        return Command.SINGLE_SUCCESS;
    }
}
