package com.swacky.ohmega.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.common.command.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class AccessoryTypeArgument implements ArgumentType<AccessoryType> {
    public static final String KEY = "accessory_type";

    public static final String EXCEPTION_UNKNOWN_TYPE_KEY = CommandHelper.exception("unknown_accessory_type");

    private static final DynamicCommandExceptionType EXCEPTION_UNKNOWN_TYPE = new DynamicCommandExceptionType(id -> Component.translatable(EXCEPTION_UNKNOWN_TYPE_KEY, id));
    private static final List<String> EXAMPLES = List.of("normal", "ohmega:utility");

    @Override
    public AccessoryType parse(StringReader reader) throws CommandSyntaxException {
        Identifier id = Identifier.read(reader);

        if (AccessoryTypeManager.exists(id)) {
            return AccessoryTypeManager.get(id);
        } else {
            throw EXCEPTION_UNKNOWN_TYPE.create(id);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(AccessoryTypeManager.getTypeIdentifiers().stream().map(Identifier::toString), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static AccessoryType getType(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, AccessoryType.class);
    }
}
