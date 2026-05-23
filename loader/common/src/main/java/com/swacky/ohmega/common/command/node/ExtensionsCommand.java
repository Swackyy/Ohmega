package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.node.OhmegaCommandNode;
import com.swacky.ohmega.api.common.menu.AccessoryMenus;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ExtensionsCommand extends OhmegaCommandNode {
    public static final String ELEMENT_ROOT = "extensions";

    public static final String ROOT_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).feedback();

    public ExtensionsCommand(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.executes(ExtensionsCommand::extensions);
    }

    private static int extensions(CommandContext<CommandSourceStack> context) {
        List<Component> components = new ArrayList<>();
        Set<Identifier> extensions = AccessoryMenus.getExtensionKeys();

        for (Identifier id : extensions) {
            components.add(Component.literal('[' + id.toString() + ']').withStyle(ChatFormatting.GREEN));
        }

        context.getSource().sendSuccess(() -> Component.translatable(ROOT_FEEDBACK, extensions.size(),
                ComponentUtils.formatList(components, Component.literal(", "))), false);
        return Command.SINGLE_SUCCESS;
    }
}
