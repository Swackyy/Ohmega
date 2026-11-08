package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.CommandUtils;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.common.command.ICommonCommandSource;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
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

    public static final String LIST_FEEDBACK = CommandUtils.command(ELEMENT_ROOT).add(ELEMENT_LIST).feedback();
    public static final String QUERY_FEEDBACK = CommandUtils.command(ELEMENT_ROOT).add(ELEMENT_QUERY).feedback();

    public <T extends SharedSuggestionProvider> TypesCommand(CommandBuildContext context, LiteralArgumentBuilder<T> builder, CommandHelper<T> helper) {
        builder
                .then(helper.literal(ELEMENT_LIST)
                        .executes(helper.executor(TypesCommand::list)))
                .then(helper.literal(ELEMENT_QUERY)
                        .then(helper.argument(ARGUMENT_TYPE, AccessoryTypeArgument.any())
                                .executes(helper.executor(TypesCommand::query))));
    }

    private static int list(ICommonCommandSource source) {
        Collection<AccessoryType> types = AccessoryTypeManager.getTypes();
        List<Component> components = new ArrayList<>(types.size());

        for (AccessoryType type : types) {
            components.add(Component.literal('[' + type.getId().toString() + ']').withStyle(ChatFormatting.GREEN).withStyle(style ->
                    style.withHoverEvent(new HoverEvent.ShowText(type.getTranslation().withColor(type.getHoverTextColour())))));
        }

        source.sendSuccess(() -> Component.translatable(LIST_FEEDBACK,
                types.size(),
                ComponentUtils.formatList(components, Component.literal(", "))));
        return Command.SINGLE_SUCCESS;
    }

    private static int query(CommandContext<?> context, ICommonCommandSource source) {
        AccessoryType type = AccessoryTypeArgument.getType(context, ARGUMENT_TYPE);

        AccessoryType.INITIALISER_CODEC.encodeStart(NbtOps.INSTANCE, type).resultOrPartial().ifPresent(tag ->
                source.sendSuccess(() -> Component.translatable(QUERY_FEEDBACK,
                        Component.literal(type.getId().toString()).withStyle(ChatFormatting.GREEN),
                        NbtUtils.toPrettyComponent(tag))));
        return Command.SINGLE_SUCCESS;
    }
}
