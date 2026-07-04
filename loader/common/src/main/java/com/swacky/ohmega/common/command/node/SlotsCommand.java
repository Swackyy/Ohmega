package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypePredicateArgument;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public final class SlotsCommand implements ICommandNode {
    public static final String ELEMENT_ROOT = "slots";
    public static final String ELEMENT_ADD = "add";
    public static final String ELEMENT_CLEAR = "clear";
    public static final String ELEMENT_DEFAULT = "default";
    public static final String ELEMENT_GET = "get";
    public static final String ELEMENT_INHERIT = "inherit";
    public static final String ELEMENT_INSERT = "insert";
    public static final String ELEMENT_REMOVE = "remove";
    public static final String ELEMENT_SET = "set";
    public static final String ELEMENT_TRACKING = "tracking";
    public static final String ELEMENT_UNTRACK = "untrack";

    private static final String ARGUMENT_TARGETS = "targets";
    private static final String ARGUMENT_TYPE = "type";
    private static final String ARGUMENT_AMOUNT = "amount";
    private static final String ARGUMENT_FILTER = "filter";
    private static final String ARGUMENT_TARGET = "target";
    private static final String ARGUMENT_INDEX = "index";
    private static final String ARGUMENT_OTHER = "other";
    private static final String ARGUMENT_MIN = "min";
    private static final String ARGUMENT_MAX = "max";

    public static final String ADD_FEEDBACK_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_ADD).feedback("multiple");
    public static final String ADD_FEEDBACK_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_ADD).feedback("single");
    public static final String CLEAR_EXCEPTION_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_CLEAR).exception("multiple");
    public static final String CLEAR_EXCEPTION_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_CLEAR).exception("single");
    public static final String CLEAR_FEEDBACK_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_CLEAR).feedback("multiple");
    public static final String CLEAR_FEEDBACK_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_CLEAR).feedback("single");
    public static final String DEFAULT_FEEDBACK_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_DEFAULT).feedback("multiple");
    public static final String DEFAULT_FEEDBACK_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_DEFAULT).feedback("single");
    public static final String GET_FEEDBACK_RANGED = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_GET).feedback("ranged");
    public static final String GET_FEEDBACK = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_GET).feedback();
    public static final String INHERIT_FEEDBACK_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_INHERIT).feedback("multiple");
    public static final String INHERIT_FEEDBACK_RANGED_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_INHERIT).feedback("ranged_multiple");
    public static final String INHERIT_FEEDBACK_RANGED_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_INHERIT).feedback("ranged_single");
    public static final String INHERIT_FEEDBACK_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_INHERIT).feedback("single");
    public static final String INSERT_FEEDBACK_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_INSERT).feedback("multiple");
    public static final String INSERT_FEEDBACK_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_INSERT).feedback("single");
    public static final String REMOVE_FEEDBACK_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_REMOVE).feedback("multiple");
    public static final String REMOVE_FEEDBACK_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_REMOVE).feedback("single");
    public static final String ROOT_EXCEPTION_BOUNDS_ARGUMENT = CommandHelper.command(ELEMENT_ROOT).exception("bounds_argument");
    public static final String ROOT_EXCEPTION_BOUNDS_SLOTS = CommandHelper.command(ELEMENT_ROOT).exception("bounds_slots");
    public static final String SET_FEEDBACK_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_SET).feedback("multiple");
    public static final String SET_FEEDBACK_RANGED_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_SET).feedback("ranged_multiple");
    public static final String SET_FEEDBACK_RANGED_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_SET).feedback("ranged_single");
    public static final String SET_FEEDBACK_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_SET).feedback("single");
    public static final String TRACKING_FEEDBACK_DEFAULT = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_TRACKING).feedback("default");
    public static final String TRACKING_FEEDBACK_NONE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_TRACKING).feedback("none");
    public static final String UNTRACK_FEEDBACK_MULTIPLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_UNTRACK).feedback("multiple");
    public static final String UNTRACK_FEEDBACK_SINGLE = CommandHelper.command(ELEMENT_ROOT).add(ELEMENT_UNTRACK).feedback("single");

    public SlotsCommand(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal(ELEMENT_ADD)
                        .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entities())
                                .then(Commands.argument(ARGUMENT_TYPE, AccessoryTypeArgument.referenceable())
                                        .executes(SlotsCommand::add)
                                        .then(Commands.argument(ARGUMENT_AMOUNT, IntegerArgumentType.integer(0))
                                                .executes(SlotsCommand::addWithAmount)))))
                .then(Commands.literal(ELEMENT_CLEAR)
                        .executes(SlotsCommand::clear)
                        .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entities())
                                .executes(SlotsCommand::clearWithTargets)
                                .then(Commands.argument(ARGUMENT_FILTER, AccessoryTypePredicateArgument.any())
                                        .executes(SlotsCommand::clearWithTargetsFilter)
                                        .then(Commands.argument(ARGUMENT_MAX, IntegerArgumentType.integer(0))
                                                .executes(SlotsCommand::clearWithTargetsFilterMax)))))
                .then(Commands.literal(ELEMENT_DEFAULT)
                        .executes(SlotsCommand::_default)
                        .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entities())
                                .executes(SlotsCommand::defaultWithTargets)))
                .then(Commands.literal(ELEMENT_GET)
                        .executes(SlotsCommand::get)
                        .then(Commands.argument(ARGUMENT_TARGET, EntityArgument.entity())
                                .executes(SlotsCommand::getWithTarget)
                                .then(Commands.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                        .executes(SlotsCommand::getWithTargetIndex))))
                .then(Commands.literal(ELEMENT_INHERIT)
                        .then(Commands.argument(ARGUMENT_OTHER, EntityArgument.entity())
                                .executes(SlotsCommand::inherit)
                                .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entities())
                                        .executes(SlotsCommand::inheritWithTargets)
                                        .then(Commands.argument(ARGUMENT_MIN, IntegerArgumentType.integer(0))
                                                .executes(SlotsCommand::inheritWithTargetsMin)
                                                .then(Commands.argument(ARGUMENT_MAX, IntegerArgumentType.integer(0))
                                                        .executes(SlotsCommand::inheritWithTargetsMinMax))))))
                .then(Commands.literal(ELEMENT_INSERT)
                        .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entities())
                                .then(Commands.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                        .then(Commands.argument(ARGUMENT_TYPE, AccessoryTypeArgument.referenceable())
                                                .executes(SlotsCommand::insert)
                                                .then(Commands.argument(ARGUMENT_AMOUNT, IntegerArgumentType.integer(0))
                                                        .executes(SlotsCommand::insertWithAmount))))))
                .then(Commands.literal(ELEMENT_REMOVE)
                        .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entities())
                                .then(Commands.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                        .executes(SlotsCommand::remove)
                                        .then(Commands.argument(ARGUMENT_AMOUNT, IntegerArgumentType.integer(0))
                                                .executes(SlotsCommand::removeWithAmount)
                                                .then(Commands.argument(ARGUMENT_FILTER, AccessoryTypePredicateArgument.any())
                                                        .executes(SlotsCommand::removeWithAmountType))))))
                .then(Commands.literal(ELEMENT_SET)
                        .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entities())
                                .then(Commands.argument(ARGUMENT_INDEX, IntegerArgumentType.integer(0))
                                        .then(Commands.argument(ARGUMENT_TYPE, AccessoryTypeArgument.referenceable())
                                                .executes(SlotsCommand::set)
                                                .then(Commands.argument(ARGUMENT_MAX, IntegerArgumentType.integer(0))
                                                        .executes(SlotsCommand::setWithMax))))))
                .then(Commands.literal(ELEMENT_TRACKING)
                        .executes(SlotsCommand::tracking)
                        .then(Commands.argument(ARGUMENT_TARGET, EntityArgument.entity())
                                .executes(SlotsCommand::trackingWithTarget)))
                .then(Commands.literal(ELEMENT_UNTRACK)
                        .executes(SlotsCommand::untrack)
                        .then(Commands.argument(ARGUMENT_TARGETS, EntityArgument.entities())
                                .executes(SlotsCommand::untrackWithTargets)));
    }

    private static int doAdd(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities, AccessoryType type, int amount) throws CommandSyntaxException {
        List<LivingEntity> targets = CommandHelper.convertLiving(entities);

        for (LivingEntity target : targets) {
            OhmegaDataAttachments.getData(target).addSlots(target, type, amount);
        }

        int size = targets.size();
        CommandSourceStack source = context.getSource();

        if (size == 1) {
            source.sendSuccess(() -> Component.translatable(ADD_FEEDBACK_SINGLE,
                    amount,
                    Component.literal(type.toString()).withStyle(ChatFormatting.GREEN),
                    targets.getFirst().getDisplayName()
            ), true);
        } else {
            source.sendSuccess(() -> Component.translatable(ADD_FEEDBACK_MULTIPLE,
                    amount,
                    Component.literal(type.toString()).withStyle(ChatFormatting.GREEN),
                    size
            ), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int add(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        AccessoryType type = AccessoryTypeArgument.getType(context, ARGUMENT_TYPE);

        return doAdd(context, targets, type, 1);
    }

    private static int addWithAmount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        AccessoryType type = AccessoryTypeArgument.getType(context, ARGUMENT_TYPE);
        int amount = IntegerArgumentType.getInteger(context, ARGUMENT_AMOUNT);

        return doAdd(context, targets, type, amount);
    }

    private static int doClear(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities, Predicate<AccessoryType> filter, int max) throws CommandSyntaxException {
        List<LivingEntity> targets = CommandHelper.convertLiving(entities);
        int[] count = {0};

        for (LivingEntity target : targets) {
            AccessoryData data = OhmegaDataAttachments.getData(target);

            if (filter == null) {
                count[0] += data.clearSlots(target, max, EquipContext.COMMAND);
            } else {
                count[0] += data.clearSlots(target, filter, max, EquipContext.COMMAND);
            }
        }

        int size = targets.size();
        int count0 = count[0];
        CommandSourceStack source = context.getSource();

        if (size == 1) {
            Component name = targets.getFirst().getDisplayName();

            if (count0 == 0) {
                source.sendFailure(Component.translatable(CLEAR_EXCEPTION_SINGLE, name));
            } else {
                source.sendSuccess(() -> Component.translatable(CLEAR_FEEDBACK_SINGLE,
                        count0,
                        name
                ), true);
            }
        } else {
            if (count0 == 0) {
                source.sendFailure(Component.translatable(CLEAR_EXCEPTION_MULTIPLE, size));
            } else {
                source.sendSuccess(() -> Component.translatable(CLEAR_FEEDBACK_MULTIPLE,
                        count0,
                        size
                ), true);
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int clear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = context.getSource().getEntity();

        if (target != null) {
            return doClear(context, List.of(target), null, -1);
        }

        throw EntityArgument.NO_ENTITIES_FOUND.create();
    }

    private static int clearWithTargets(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);

        return doClear(context, targets, null, -1);
    }

    private static int clearWithTargetsFilter(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        Predicate<AccessoryType> filter = AccessoryTypePredicateArgument.getTypePredicate(context, ARGUMENT_FILTER);

        return doClear(context, targets, filter, -1);
    }

    private static int clearWithTargetsFilterMax(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        Predicate<AccessoryType> filter = AccessoryTypePredicateArgument.getTypePredicate(context, ARGUMENT_FILTER);
        int max = IntegerArgumentType.getInteger(context, ARGUMENT_MAX);

        return doClear(context, targets, filter, max);
    }

    private static int doDefault(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities) throws CommandSyntaxException {
        List<LivingEntity> targets = CommandHelper.convertLiving(entities);

        for (LivingEntity target : targets) {
            OhmegaDataAttachments.getData(target).defaultSlots(target, EquipContext.COMMAND);
        }

        int size = targets.size();
        CommandSourceStack source = context.getSource();

        if (size == 1) {
            source.sendSuccess(() -> Component.translatable(DEFAULT_FEEDBACK_SINGLE,
                    targets.getFirst().getDisplayName()
            ), true);
        } else {
            source.sendSuccess(() -> Component.translatable(DEFAULT_FEEDBACK_MULTIPLE,
                    size
            ), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int _default(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = context.getSource().getEntity();

        if (target != null) {
            return doDefault(context, List.of(target));
        }

        throw EntityArgument.NO_ENTITIES_FOUND.create();
    }

    private static int defaultWithTargets(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);

        return doDefault(context, targets);
    }

    private static int doGet(CommandContext<CommandSourceStack> context, Entity entity, int index) throws CommandSyntaxException {
        LivingEntity target = CommandHelper.convertLiving(entity);
        AccessoryData data = OhmegaDataAttachments.getData(target);
        int size = data.size();
        CommandSourceStack source = context.getSource();
        Component name = target.getDisplayName();

        if (index == -1) {
            List<Component> components = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                components.add(Component.literal(data.getEntry(i).getType().toString()).withStyle(ChatFormatting.GREEN));
            }

            source.sendSuccess(() -> Component.translatable(GET_FEEDBACK_RANGED,
                    name,
                    size,
                    Component.literal("[").append(ComponentUtils.formatList(components, Component.literal(", "))).append("]")
            ), true);
        } else {
            if (index < size) {
                source.sendSuccess(() -> Component.translatable(GET_FEEDBACK,
                        index,
                        name,
                        Component.literal(data.getEntry(index).getType().toString()).withStyle(ChatFormatting.GREEN)
                ), true);
            } else {
                source.sendFailure(Component.translatable(ROOT_EXCEPTION_BOUNDS_SLOTS, ARGUMENT_INDEX, index, name));
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = context.getSource().getEntity();

        if (target != null) {
            return doGet(context, target, -1);
        }

        throw EntityArgument.NO_ENTITIES_FOUND.create();
    }

    private static int getWithTarget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, ARGUMENT_TARGET);

        return doGet(context, target, -1);
    }

    private static int getWithTargetIndex(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, ARGUMENT_TARGET);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);

        return doGet(context, target, index);
    }

    private static int doInherit(CommandContext<CommandSourceStack> context, Entity other, Collection<? extends Entity> entities, int min, int max) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        if (min <= max || max == -1) {
            List<LivingEntity> targets = CommandHelper.convertLiving(entities);
            LivingEntity otherTarget = CommandHelper.convertLiving(other);
            int size = targets.size();

            if (size == 1) {
                LivingEntity target = targets.getFirst();
                AccessoryData data = OhmegaDataAttachments.getData(target);
                int dataSize = data.size();
                Component name = target.getDisplayName();

                if (max < dataSize) {
                    data.inheritSlots(target, otherTarget, min, max, EquipContext.COMMAND);

                    if (min == 0 && max == -1) {
                        source.sendSuccess(() -> Component.translatable(INHERIT_FEEDBACK_SINGLE,
                                otherTarget.getDisplayName(),
                                name
                        ), true);
                    } else {
                        source.sendSuccess(() -> Component.translatable(INHERIT_FEEDBACK_RANGED_SINGLE,
                                otherTarget.getDisplayName(),
                                name,
                                min,
                                max
                        ), true);
                    }
                } else {
                    source.sendFailure(Component.translatable(ROOT_EXCEPTION_BOUNDS_SLOTS, ARGUMENT_MAX, max, name));
                }
            } else {
                for (LivingEntity target : targets) {
                    AccessoryData data = OhmegaDataAttachments.getData(target);

                    data.inheritSlots(target, otherTarget, min, Math.min(max, data.size()), EquipContext.COMMAND);
                }

                if (min == 0 && max == -1) {
                    source.sendSuccess(() -> Component.translatable(INHERIT_FEEDBACK_MULTIPLE,
                            otherTarget.getDisplayName(),
                            size
                    ), true);
                } else {
                    source.sendSuccess(() -> Component.translatable(INHERIT_FEEDBACK_RANGED_MULTIPLE,
                            otherTarget.getDisplayName(),
                            size,
                            min,
                            max
                    ), true);
                }
            }
        } else {
            source.sendFailure(Component.translatable(ROOT_EXCEPTION_BOUNDS_ARGUMENT, ARGUMENT_MAX, max, ARGUMENT_MIN));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int inherit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity other = EntityArgument.getEntity(context, ARGUMENT_OTHER);
        Entity target = context.getSource().getEntity();

        if (target != null) {
            return doInherit(context, other, List.of(target), 0, -1);
        }

        throw EntityArgument.NO_ENTITIES_FOUND.create();
    }

    private static int inheritWithTargets(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity other = EntityArgument.getEntity(context, ARGUMENT_OTHER);
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);

        return doInherit(context, other, targets, 0, -1);
    }

    private static int inheritWithTargetsMin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity other = EntityArgument.getEntity(context, ARGUMENT_OTHER);
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int min = IntegerArgumentType.getInteger(context, ARGUMENT_MIN);

        return doInherit(context, other, targets, min, -1);
    }

    private static int inheritWithTargetsMinMax(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity other = EntityArgument.getEntity(context, ARGUMENT_OTHER);
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int min = IntegerArgumentType.getInteger(context, ARGUMENT_MIN);
        int max = IntegerArgumentType.getInteger(context, ARGUMENT_MAX);

        return doInherit(context, other, targets, min, max);
    }

    private static int doInsert(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities, int index, AccessoryType type, int amount) throws CommandSyntaxException {
        List<LivingEntity> targets = CommandHelper.convertLiving(entities);

        int size = targets.size();
        CommandSourceStack source = context.getSource();

        if (size == 1) {
            LivingEntity target = targets.getFirst();
            AccessoryData data = OhmegaDataAttachments.getData(target);
            int dataSize = data.size();
            Component name = target.getDisplayName();

            if (index < dataSize) {
                data.insertSlots(target, index, type, amount);

                source.sendSuccess(() -> Component.translatable(INSERT_FEEDBACK_SINGLE,
                        amount,
                        Component.literal(type.toString()).withStyle(ChatFormatting.GREEN),
                        index,
                        name
                ), true);
            } else {
                source.sendFailure(Component.translatable(ROOT_EXCEPTION_BOUNDS_SLOTS, ARGUMENT_INDEX, index, name));
            }
        } else {
            for (LivingEntity target : targets) {
                AccessoryData data = OhmegaDataAttachments.getData(target);

                data.insertSlots(target, Math.min(index, data.size()), type, amount);
            }

            source.sendSuccess(() -> Component.translatable(INSERT_FEEDBACK_MULTIPLE,
                    amount,
                    Component.literal(type.toString()).withStyle(ChatFormatting.GREEN),
                    index,
                    size
            ), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int insert(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);
        AccessoryType type = AccessoryTypeArgument.getType(context, ARGUMENT_TYPE);

        return doInsert(context, targets, index, type, 1);
    }

    private static int insertWithAmount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);
        AccessoryType type = AccessoryTypeArgument.getType(context, ARGUMENT_TYPE);
        int amount = IntegerArgumentType.getInteger(context, ARGUMENT_AMOUNT);

        return doInsert(context, targets, index, type, amount);
    }

    private static int doRemove(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities, int index, int amount, Predicate<AccessoryType> filter) throws CommandSyntaxException {
        List<LivingEntity> targets = CommandHelper.convertLiving(entities);

        int size = targets.size();
        CommandSourceStack source = context.getSource();

        if (size == 1) {
            LivingEntity target = targets.getFirst();
            AccessoryData data = OhmegaDataAttachments.getData(target);
            int dataSize = data.size();
            Component name = targets.getFirst().getDisplayName();

            if (index < dataSize) {
                int count;

                if (filter == null) {
                    count = data.removeSlots(target, index, amount, EquipContext.COMMAND);
                } else {
                    count = data.removeSlots(target, index, amount, filter, EquipContext.COMMAND);
                }

                source.sendSuccess(() -> Component.translatable(REMOVE_FEEDBACK_SINGLE,
                        count,
                        name
                ), true);
            } else {
                source.sendFailure(Component.translatable(ROOT_EXCEPTION_BOUNDS_SLOTS, ARGUMENT_INDEX, index, name));
            }
        } else {
            int[] count = {0};

            for (LivingEntity target : targets) {
                AccessoryData data = OhmegaDataAttachments.getData(target);
                int correctedIndex = Math.min(index, data.size());

                if (filter == null) {
                    count[0] += data.removeSlots(target, correctedIndex, amount, EquipContext.COMMAND);
                } else {
                    count[0] += data.removeSlots(target, correctedIndex, amount, filter, EquipContext.COMMAND);
                }
            }

            source.sendSuccess(() -> Component.translatable(REMOVE_FEEDBACK_MULTIPLE,
                    count[0],
                    size
            ), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);

        return doRemove(context, targets, index, 1, null);
    }

    private static int removeWithAmount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);
        int amount = IntegerArgumentType.getInteger(context, ARGUMENT_AMOUNT);

        return doRemove(context, targets, index, amount, null);
    }

    private static int removeWithAmountType(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);
        int amount = IntegerArgumentType.getInteger(context, ARGUMENT_AMOUNT);
        Predicate<AccessoryType> type = AccessoryTypePredicateArgument.getTypePredicate(context, ARGUMENT_FILTER);

        return doRemove(context, targets, index, amount, type);
    }

    private static int doSet(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities, int index, AccessoryType type, int max) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        if (index <= max) {
            List<LivingEntity> targets = CommandHelper.convertLiving(entities);
            int size = targets.size();

            if (size == 1) {
                LivingEntity target = targets.getFirst();
                AccessoryData data = OhmegaDataAttachments.getData(target);
                int dataSize = data.size();
                Component name = target.getDisplayName();

                if (index < dataSize) {
                    if (max < dataSize) {
                        data.setSlots(target, index, type, max, EquipContext.COMMAND);

                        if (index == max) {
                            source.sendSuccess(() -> Component.translatable(SET_FEEDBACK_SINGLE,
                                    index,
                                    name,
                                    Component.literal(type.toString()).withStyle(ChatFormatting.GREEN)
                            ), true);
                        } else {
                            source.sendSuccess(() -> Component.translatable(SET_FEEDBACK_RANGED_SINGLE,
                                    index,
                                    max,
                                    name,
                                    Component.literal(type.toString()).withStyle(ChatFormatting.GREEN)
                            ), true);
                        }
                    } else {
                        source.sendFailure(Component.translatable(ROOT_EXCEPTION_BOUNDS_SLOTS, ARGUMENT_MAX, max, name));
                    }
                } else {
                    source.sendFailure(Component.translatable(ROOT_EXCEPTION_BOUNDS_SLOTS, ARGUMENT_INDEX, index, name));
                }
            } else {
                for (LivingEntity target : targets) {
                    AccessoryData data = OhmegaDataAttachments.getData(target);
                    int dataSize = data.size();

                    data.setSlots(target, Math.min(index, dataSize), type, Math.min(max, dataSize), EquipContext.COMMAND);
                }

                if (index == max) {
                    source.sendSuccess(() -> Component.translatable(SET_FEEDBACK_MULTIPLE,
                            index,
                            size,
                            Component.literal(type.toString()).withStyle(ChatFormatting.GREEN)
                    ), true);
                } else {
                    source.sendSuccess(() -> Component.translatable(SET_FEEDBACK_RANGED_MULTIPLE,
                            index,
                            max,
                            size,
                            Component.literal(type.toString()).withStyle(ChatFormatting.GREEN)
                    ), true);
                }
            }
        } else {
            source.sendFailure(Component.translatable(ROOT_EXCEPTION_BOUNDS_ARGUMENT, ARGUMENT_MAX, max, ARGUMENT_INDEX));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);
        AccessoryType type = AccessoryTypeArgument.getType(context, ARGUMENT_TYPE);

        return doSet(context, targets, index, type, index);
    }

    private static int setWithMax(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);
        int index = IntegerArgumentType.getInteger(context, ARGUMENT_INDEX);
        AccessoryType type = AccessoryTypeArgument.getType(context, ARGUMENT_TYPE);
        int max = IntegerArgumentType.getInteger(context, ARGUMENT_MAX);

        return doSet(context, targets, index, type, max);
    }

    private static int doTracking(CommandContext<CommandSourceStack> context, Entity entity) throws CommandSyntaxException {
        LivingEntity target = CommandHelper.convertLiving(entity);
        AccessoryData data = OhmegaDataAttachments.getData(target);
        CommandSourceStack source = context.getSource();
        Component name = target.getName();

        if (data.isTrackingDefault()) {
            source.sendSuccess(() -> Component.translatable(TRACKING_FEEDBACK_DEFAULT,
                    name
            ), true);
        } else {
            source.sendSuccess(() -> Component.translatable(TRACKING_FEEDBACK_NONE,
                    name
            ), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int tracking(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = context.getSource().getEntity();

        if (target != null) {
            return doTracking(context, target);
        }

        throw EntityArgument.NO_ENTITIES_FOUND.create();
    }

    private static int trackingWithTarget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, ARGUMENT_TARGET);

        return doTracking(context, target);
    }

    private static int doUntrack(CommandContext<CommandSourceStack> context, Collection<? extends Entity> entities) throws CommandSyntaxException {
        List<LivingEntity> targets = CommandHelper.convertLiving(entities);

        for (LivingEntity target : targets) {
            OhmegaDataAttachments.getData(target).untrackDefault(target);
        }

        int size = targets.size();
        CommandSourceStack source = context.getSource();

        if (size == 1) {
            source.sendSuccess(() -> Component.translatable(UNTRACK_FEEDBACK_SINGLE,
                    targets.getFirst().getDisplayName()
            ), true);
        } else {
            source.sendSuccess(() -> Component.translatable(UNTRACK_FEEDBACK_MULTIPLE,
                    size
            ), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int untrack(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = context.getSource().getEntity();

        if (target != null) {
            return doUntrack(context, List.of(target));
        }

        throw EntityArgument.NO_ENTITIES_FOUND.create();
    }

    private static int untrackWithTargets(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, ARGUMENT_TARGETS);

        return doUntrack(context, targets);
    }
}
