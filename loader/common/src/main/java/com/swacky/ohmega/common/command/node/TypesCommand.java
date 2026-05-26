package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.node.OhmegaCommandNode;
import com.swacky.ohmega.api.common.item.datacomponent.AccessoryModifiers;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TypesCommand extends OhmegaCommandNode {
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
                        .then(Commands.argument(ARGUMENT_TYPE, new AccessoryTypeArgument())
                                .executes(TypesCommand::query)));
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        Collection<AccessoryType> types = AccessoryTypeManager.getTypes();
        List<Component> components = new ArrayList<>(types.size());

        for (AccessoryType type : types) {
            components.add(Component.literal('[' + type.getId().toString() + ']').withStyle(ChatFormatting.GREEN).withStyle(style ->
                    style.withHoverEvent(new HoverEvent.ShowText(type.getTranslation().withColor(type.getHoverTextColour())))));
        }

        context.getSource().sendSuccess(() -> Component.translatable(LIST_FEEDBACK, types.size(),
                ComponentUtils.formatList(components, Component.literal(", "))), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int query(CommandContext<CommandSourceStack> context) {
        AccessoryType type = AccessoryTypeArgument.getType(context, ARGUMENT_TYPE);
        List<Component> components = new ArrayList<>(6);
        int colour = type.getHoverTextColour();

        components.add(Component
                .literal("\n[" + AccessoryType.ATTRIBUTE_MODIFIERS_KEY + ':').withStyle(ChatFormatting.GREEN)
                .append(Component.literal(AccessoryModifiers.CODEC.encodeStart(
                        NbtOps.INSTANCE,
                        type.getAttributeModifiers()).resultOrPartial().orElseGet(CompoundTag::new).toString()
                ).withStyle(ChatFormatting.WHITE))
                .append(Component.literal("]").withStyle(ChatFormatting.GREEN)));
        components.add(Component
                .literal('[' + AccessoryType.DISPLAY_HOVER_TEXT_KEY + ':').withStyle(ChatFormatting.GREEN)
                .append(Component.literal(String.valueOf(type.displayHoverText())).withStyle(ChatFormatting.WHITE))
                .append(Component.literal("]").withStyle(ChatFormatting.GREEN)));
        components.add(Component
                .literal('[' + AccessoryType.EMPTY_SLOT_TEXTURE_KEY + ':').withStyle(ChatFormatting.GREEN)
                .append(Component.translatable(CommandHelper.CONTEXT_HOVER).withStyle(ChatFormatting.WHITE).withStyle(style ->
                        style.withHoverEvent(new HoverEvent.ShowText(Component.literal(type.getEmptySlotLocation().toString())))))
                .append(Component.literal("]").withStyle(ChatFormatting.GREEN)));
        components.add(Component
                .literal('[' + AccessoryType.HOVER_TEXT_COLOUR_KEY + ':').withStyle(ChatFormatting.GREEN)
                .append(Component.literal("0x" + Integer.toHexString(colour)).withColor(colour))
                .append(Component.literal("]").withStyle(ChatFormatting.GREEN)));
        components.add(Component
                .literal('[' + AccessoryType.NO_FALLBACK_KEY + ':').withStyle(ChatFormatting.GREEN)
                .append(Component.literal(String.valueOf(type.isNoFallback())).withStyle(ChatFormatting.WHITE))
                .append(Component.literal("]").withStyle(ChatFormatting.GREEN)));
        components.add(Component
                .literal('[' + AccessoryType.PRIORITY_KEY + ':').withStyle(ChatFormatting.GREEN)
                .append(Component.literal(String.valueOf(type.getPriority())).withStyle(ChatFormatting.WHITE))
                .append(Component.literal("]").withStyle(ChatFormatting.GREEN)));
        context.getSource().sendSuccess(() -> Component.translatable(QUERY_FEEDBACK, type.getId().toString(),
                ComponentUtils.formatList(components, Component.literal("\n"))), false);
        return Command.SINGLE_SUCCESS;
    }
}
