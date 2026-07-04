package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;

public final class ItemCommand implements ICommandNode {
    public static final String ELEMENT_ROOT = "item";
    public static final String ELEMENT_GET = "get";
    public static final String ELEMENT_SET = "set";
    public static final String ELEMENT_TYPE = "type";

    public static final String ARGUMENT_TARGET = "target";
    public static final String ARGUMENT_INDEX = "index";
    public static final String ARGUMENT_TARGETS = "targets";
    public static final String ARGUMENT_ITEM = "item";
    public static final String ARGUMENT_COUNT = "count";

    public static final String ARGUMENT_INDEX_EXCEPTION = CommandHelper.command(ARGUMENT_INDEX).exception();

    public static final String GET_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_GET).feedback();
    public static final String SET_FEEDBACK_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_SET).feedback("multiple");
    public static final String SET_FEEDBACK_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_SET).feedback("single");
    public static final String TYPE_GET_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_TYPE).add(ELEMENT_GET).feedback();

    @SuppressWarnings("unchecked")
    private static final DynamicCommandExceptionType INDEX_EXCEPTION = new DynamicCommandExceptionType(object -> {
        Pair<Integer, Integer> pair = (Pair<Integer, Integer>) object;

        return Component.translatable(
                ARGUMENT_INDEX_EXCEPTION,
                pair.getLeft(),
                pair.getRight());
    });

    public ItemCommand(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal(ELEMENT_GET)
                        .then(Commands.argument(ARGUMENT_TARGET, EntityArgument.entity())
                                .then(Commands.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                        .executes(ItemCommand::get))))
                .then(Commands.literal(ELEMENT_SET)
                        .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entities())
                                .then(Commands.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                        .then(Commands.argument(ARGUMENT_ITEM, ItemArgument.item(context))
                                                .executes(ItemCommand::set)
                                                .then(Commands.argument(ARGUMENT_COUNT, IntegerArgumentType.integer(1))
                                                        .executes(ItemCommand::setWithCount))))))
                .then(Commands.literal(ELEMENT_TYPE)
                        .then(Commands.literal(ELEMENT_GET)
                                .then(Commands.argument(ARGUMENT_ITEM, ItemArgument.item(context))
                                        .executes(ItemCommand::type))));
    }

    private static int get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, ARGUMENT_TARGET);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);

        ItemStack stack;
        AccessoryData data = OhmegaDataAttachments.getData(CommandHelper.convertLiving(target));

        if (index >= 0 && index < data.size()) {
            stack = data.getEntry(index).getStack();
        } else {
            throw INDEX_EXCEPTION.create(Pair.of(index, data.size()));
        }

        context.getSource().sendSuccess(() -> Component.translatable(GET_FEEDBACK,
                target.getDisplayName(),
                stack.getCount(),
                stack.getDisplayName(),
                index
        ), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int doSet(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities, int index, Holder<Item> item, int count) throws CommandSyntaxException {
        List<LivingEntity> targets = CommandHelper.convertLiving(entities);

        for (LivingEntity target : targets) {
            AccessoryData data = OhmegaDataAttachments.getData(target);

            if (index >= 0 && index < data.size()) {
                data.getEntry(index).setStack(target, new ItemStack(item, count), index, EquipContext.COMMAND, true, true);
            } else {
                throw INDEX_EXCEPTION.create(Pair.of(index, data.size()));
            }
        }

        int size = targets.size();
        CommandSourceStack source = context.getSource();
        Component itemDisplayName = item.value().getDefaultInstance().getDisplayName();

        if (size == 1) {
            source.sendSuccess(() -> Component.translatable(SET_FEEDBACK_SINGLE,
                    index,
                    targets.getFirst().getDisplayName(),
                    count,
                    itemDisplayName
            ), true);
        } else {
            source.sendSuccess(() -> Component.translatable(SET_FEEDBACK_MULTIPLE,
                    index,
                    size,
                    count,
                    itemDisplayName
            ), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);
        Holder<Item> item = ItemArgument.getItem(context, ARGUMENT_ITEM).item();

        return doSet(context, targets, index, item, 1);
    }

    private static int setWithCount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);
        Holder<Item> item = ItemArgument.getItem(context, ARGUMENT_ITEM).item();
        int count = IntegerArgumentType.getInteger(context, ARGUMENT_COUNT);

        return doSet(context, targets, index, item, count);
    }

    private static int type(CommandContext<CommandSourceStack> context) {
        Item item = ItemArgument.getItem(context, ARGUMENT_ITEM).item().value();

        context.getSource().sendSuccess(() -> Component.translatable(TYPE_GET_FEEDBACK,
                Component.literal(item.toString()).withStyle(ChatFormatting.GREEN),
                Component.literal(Accessories.getType(null, item).toString()).withStyle(ChatFormatting.GREEN)
        ), true);
        return Command.SINGLE_SUCCESS;
    }
}
