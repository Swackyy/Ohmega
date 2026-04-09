package com.swacky.ohmega.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.swacky.ohmega.api.AccessoryHelper;
import com.swacky.ohmega.api.EquipContext;
import com.swacky.ohmega.common.dataattachment.AccessoryData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public final class ItemCommand {
    public static final String ELEMENT_ROOT = "item";
    public static final String ELEMENT_GET = "get";
    public static final String ELEMENT_SET = "set";

    public static final String ARGUMENT_TARGET = "target";
    public static final String ARGUMENT_INDEX = "index";
    public static final String ARGUMENT_TARGETS = "targets";
    public static final String ARGUMENT_ITEM = "item";
    public static final String ARGUMENT_COUNT = "count";

    public static final String ROOT_FEEDBACK = MessageHelper.command(ELEMENT_ROOT).feedback();
    public static final String GET_FEEDBACK = MessageHelper.command(ELEMENT_ROOT).add(ELEMENT_GET).feedback();
    public static final String SET_FEEDBACK_MULTIPLE = MessageHelper.command(ELEMENT_ROOT).add(ELEMENT_SET).feedback("multiple");
    public static final String SET_FEEDBACK_SINGLE = MessageHelper.command(ELEMENT_ROOT).add(ELEMENT_SET).feedback("single");

    @SuppressWarnings("unchecked")
    private static final DynamicCommandExceptionType INDEX_EXCEPTION = new DynamicCommandExceptionType(obj -> {
        Pair<Integer, Integer> pair = (Pair<Integer, Integer>) obj;

        return Component.translatable(
                ROOT_FEEDBACK,
                pair.getLeft(),
                pair.getRight());
    });

    public static ArgumentBuilder<CommandSourceStack, ?> create(CommandBuildContext context) {
        return Commands.literal(ELEMENT_ROOT)
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal(ELEMENT_GET)
                        .then(Commands.argument(ARGUMENT_TARGET, EntityArgument.entity())
                                .then(Commands.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                        .executes(ItemCommand::get))))
                .then(Commands.literal(ELEMENT_SET)
                        .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entity())
                                .then(Commands.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                        .then(Commands.argument(ARGUMENT_ITEM, ItemArgument.item(context))
                                                .executes(ItemCommand::setWithTargetIndexItem)
                                                .then(Commands.argument(ARGUMENT_COUNT, IntegerArgumentType.integer(1))
                                                        .executes(ItemCommand::setWithTargetIndexItemCount))))));
    }

    public static int get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, ARGUMENT_TARGET);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);

        ItemStack stack;
        // todo
        if (target instanceof Player player) {
            AccessoryData data = AccessoryHelper.getData(player);

            if (index >= 0 && index < data.size()) {
                stack = data.getStackInSlot(index);
            } else {
                throw INDEX_EXCEPTION.create(Pair.of(index, data.size()));
            }
        } else return 0;

        context.getSource().sendSuccess(() -> Component.translatable(GET_FEEDBACK, target.getDisplayName(), stack.getCount(), stack.getDisplayName(), index), true);
        return Command.SINGLE_SUCCESS;
    }

    public static int doSet(CommandContext<CommandSourceStack> context, List<Entity> targets, int index, Holder<Item> item, int count) throws CommandSyntaxException {
        for (Entity target : targets) {
            // todo
            if (target instanceof Player player) {
                AccessoryData data = AccessoryHelper.getData(player);

                if (index >= 0 && index < data.size()) {
                    data.setStack(player, index, new ItemStack(item, count), EquipContext.GENERIC, true);
                } else {
                    throw INDEX_EXCEPTION.create(Pair.of(index, data.size()));
                }
            }
        }

        CommandSourceStack source = context.getSource();
        Component itemDisplayName = item.value().getDefaultInstance().getDisplayName();

        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable(SET_FEEDBACK_SINGLE, index, targets.getFirst().getDisplayName(), count, itemDisplayName), true);
        } else {
            source.sendSuccess(() -> Component.translatable(SET_FEEDBACK_MULTIPLE, index, targets.size(), count, itemDisplayName), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static int setWithTargetIndexItem(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<Entity> targets = List.copyOf(EntityArgument.getEntities(context, ARGUMENT_TARGETS));
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);
        Holder<Item> item = ItemArgument.getItem(context, ARGUMENT_ITEM).item();

        return doSet(context, targets, index, item, 1);
    }

    public static int setWithTargetIndexItemCount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<Entity> targets = List.copyOf(EntityArgument.getEntities(context, ARGUMENT_TARGETS));
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);
        Holder<Item> item = ItemArgument.getItem(context, ARGUMENT_ITEM).item();
        int count = IntegerArgumentType.getInteger(context, ARGUMENT_COUNT);

        return doSet(context, targets, index, item, count);
    }
}
