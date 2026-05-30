package com.swacky.ohmega.client.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.client.command.IClientCommandSource;
import com.swacky.ohmega.api.client.command.node.IClientCommandNode;
import com.swacky.ohmega.api.client.ui.AccessoryUIs;
import com.swacky.ohmega.api.common.command.CommandHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ExtensionsCommand implements IClientCommandNode {
    public static final String ELEMENT_ROOT = "extensions";

    public static final String ROOT_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).feedback();

    public <T extends SharedSuggestionProvider> ExtensionsCommand(CommandBuildContext context, LiteralArgumentBuilder<T> builder, IClientCommandSource.Factory<T> sourceFactory) {
        builder.executes(cmdContext -> extensions(sourceFactory.construct(cmdContext)));
    }

    private static int extensions(IClientCommandSource source) {
        List<Component> components = new ArrayList<>();
        Set<Identifier> extensions = AccessoryUIs.getExtensionKeys();

        for (Identifier id : extensions) {
            components.add(Component.literal('[' + id.toString() + ']').withStyle(ChatFormatting.GREEN));
        }

        source.sendSuccess(Component.translatable(ROOT_FEEDBACK, extensions.size(),
                ComponentUtils.formatList(components, Component.literal(", "))));
        return Command.SINGLE_SUCCESS;
    }
}
