package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class DataCommand implements ICommandNode {
    public static final String ELEMENT_ROOT = "data";

    private static final String ARGUMENT_TARGET = "target";
    private static final String ARGUMENT_INDEX = "index";

    public static final String ROOT_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).feedback();
    public static final String ROOT_FEEDBACK_INDEX = CommandHelper.command(ELEMENT_ROOT).feedback("index");

    public DataCommand(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(DataCommand::data)
                .then(Commands.argument(ARGUMENT_TARGET, EntityArgument.entity())
                        .executes(DataCommand::dataWithEntity)
                        .then(Commands.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                .executes(DataCommand::dataWithEntityIndex)));
    }

    private static int doData(CommandContext<CommandSourceStack> context, Entity entity) throws CommandSyntaxException {
        LivingEntity target = CommandHelper.convertLiving(entity);

        AccessoryData.CODEC.encodeStart(NbtOps.INSTANCE, OhmegaDataAttachments.getData(target)).resultOrPartial().ifPresent(tag ->
                context.getSource().sendSuccess(() -> Component.translatable(ROOT_FEEDBACK,
                        target.getDisplayName(),
                        NbtUtils.toPrettyComponent(tag)
                ), true));
        return Command.SINGLE_SUCCESS;
    }

    private static int data(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return doData(context, context.getSource().getEntity());
    }

    private static int dataWithEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, ARGUMENT_TARGET);

        return doData(context, target);
    }

    private static int dataWithEntityIndex(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        LivingEntity target = CommandHelper.convertLiving(EntityArgument.getEntity(context, ARGUMENT_TARGET));
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);

        AccessoryDataEntry.CODEC.encodeStart(NbtOps.INSTANCE, OhmegaDataAttachments.getData(target).getEntry(index)).resultOrPartial().ifPresent(tag ->
                context.getSource().sendSuccess(() -> Component.translatable(ROOT_FEEDBACK_INDEX,
                        target.getDisplayName(),
                        index,
                        NbtUtils.toPrettyComponent(tag)
                ), true));
        return Command.SINGLE_SUCCESS;
    }
}
