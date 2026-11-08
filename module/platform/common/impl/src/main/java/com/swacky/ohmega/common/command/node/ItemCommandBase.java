package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.swacky.ohmega.api.common.command.CommandUtils;
import com.swacky.ohmega.api.common.command.ICommonCommandSource;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import com.swacky.ohmega.api.common.item.Accessories;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public abstract class ItemCommandBase implements ICommandNode {
    public static final String ELEMENT_ROOT = "item";
    public static final String ELEMENT_GET = "get";
    public static final String ELEMENT_SET = "set";
    public static final String ELEMENT_TYPE = "type";

    protected static final String ARGUMENT_ITEM = "item";

    public static final String TYPE_FEEDBACK = CommandUtils.command(ELEMENT_ROOT).add(ELEMENT_TYPE).feedback();

    protected static int type(CommandContext<?> context, ICommonCommandSource.SuccessReference successReference) {
        Item item = ItemArgument.getItem(context, ARGUMENT_ITEM).item().value();

        successReference.send(() -> Component.translatable(TYPE_FEEDBACK,
                Component.literal(item.toString()).withStyle(ChatFormatting.GREEN),
                Component.literal(Accessories.getType(null, item).toString()).withStyle(ChatFormatting.GREEN)
        ), false);
        return Command.SINGLE_SUCCESS;
    }
}
