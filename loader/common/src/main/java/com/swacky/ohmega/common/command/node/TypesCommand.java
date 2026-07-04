package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TypesCommand implements ICommandNode {
    public static final String ELEMENT_ROOT = "types";
    public static final String ELEMENT_LIST = "list";
    public static final String ELEMENT_QUERY = "query";

    private static final String ARGUMENT_TYPE = "type";

    public static final String LIST_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_LIST).feedback();
    public static final String QUERY_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_QUERY).feedback();

    public TypesCommand(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .then(Commands.literal(ELEMENT_LIST)
                        .executes(TypesCommand::list))
                .then(Commands.literal(ELEMENT_QUERY)
                        .then(Commands.argument(ARGUMENT_TYPE, AccessoryTypeArgument.any())
                                .executes(TypesCommand::query)));
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        Collection<AccessoryType> types = AccessoryTypeManager.getTypes();
        List<Component> components = new ArrayList<>(types.size());

        for (AccessoryType type : types) {
            components.add(Component.literal('[' + type.getId().toString() + ']').withStyle(ChatFormatting.GREEN).withStyle(style ->
                    style.withHoverEvent(new HoverEvent.ShowText(type.getTranslation().withColor(type.getHoverTextColour())))));
        }

        context.getSource().sendSuccess(() -> Component.translatable(LIST_FEEDBACK,
                types.size(),
                ComponentUtils.formatList(components, Component.literal(", "))
        ), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int query(CommandContext<CommandSourceStack> context) {
        AccessoryType type = AccessoryTypeArgument.getType(context, ARGUMENT_TYPE);

        context.getSource().sendSuccess(() -> Component.translatable(QUERY_FEEDBACK,
                Component.literal(type.getId().toString()).withStyle(ChatFormatting.GREEN),
                Component.literal(AccessoryType.INITIALISER_CODEC.encodeStart(JsonOps.INSTANCE, type).getOrThrow().toString()).withStyle(ChatFormatting.GREEN)
        ), false);
        return Command.SINGLE_SUCCESS;
    }
}
