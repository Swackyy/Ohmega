package com.swacky.ohmega.client.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.client.ui.AccessoryExtensions;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.CommandUtils;
import com.swacky.ohmega.api.common.command.ICommonCommandSource;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ExtensionsCommand implements ICommandNode {
    public static final String ELEMENT_ROOT = "extensions";

    public static final String ROOT_FEEDBACK = CommandUtils.command(ELEMENT_ROOT).feedback();

    public <T extends SharedSuggestionProvider> ExtensionsCommand(
            CommandBuildContext context, LiteralArgumentBuilder<T> builder, CommandHelper<T> helper) {
        builder.executes(helper.executor(ExtensionsCommand::extensions));
    }

    private static int extensions(ICommonCommandSource source) {
        List<Component> components = new ArrayList<>();
        Set<Identifier> extensions = AccessoryExtensions.getExtensionKeys();

        for (Identifier id : extensions) {
            components.add(Component.literal(id.toString()).withStyle(ChatFormatting.GREEN));
        }

        source.sendSuccess(() -> Component.translatable(ROOT_FEEDBACK,
                extensions.size(),
                Component.literal("[").append(ComponentUtils.formatList(components, Component.literal(", "))).append("]")));
        return Command.SINGLE_SUCCESS;
    }
}
