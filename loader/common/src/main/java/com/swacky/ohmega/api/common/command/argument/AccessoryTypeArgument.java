package com.swacky.ohmega.api.common.command.argument;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.command.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Simple command argument for {@link AccessoryType}s
 */
public class AccessoryTypeArgument implements ArgumentType<AccessoryType> {
    public static final @NonNull String KEY = "accessory_type";
    public static final @NonNull BooleanArgumentSerialiser<AccessoryTypeArgument> SERIALISER = new BooleanArgumentSerialiser<>(
            AccessoryTypeArgument::new, inst -> inst.referenceableOnly);
    private static final @NonNull List<String> EXAMPLES = List.of(
            AccessoryType.NORMAL_ID.getPath(),
            AccessoryType.UTILITY_ID.toString(),
            AccessoryType.SPECIAL_ID.toString());

    public static final @NonNull String EXCEPTION_UNKNOWN_TYPE_KEY = CommandHelper.exception("unknown_accessory_type");
    public static final @NonNull String EXCEPTION_UNSPECIFIABLE_TYPE_KEY = CommandHelper.exception("unspecifiable_accessory_type");

    /**
     * General use type not found exception.
     * May be used elsewhere. hence the {@code public} specifier
     */
    public static final @NonNull DynamicCommandExceptionType EXCEPTION_UNKNOWN_TYPE = new DynamicCommandExceptionType(id ->
            Component.translatable(EXCEPTION_UNKNOWN_TYPE_KEY, id.toString()));
    /**
     * General use unspecifiable type (see {@link AccessoryType#allowReference()}) exception
     * May be used elsewhere. hence the {@code public} specifier
     */
    public static final @NonNull DynamicCommandExceptionType EXCEPTION_UNSPECIFIABLE_TYPE = new DynamicCommandExceptionType(id ->
            Component.translatable(EXCEPTION_UNSPECIFIABLE_TYPE_KEY, id.toString()));

    private final boolean referenceableOnly;

    /**
     * Internal constructor, use {@link #any()} and {@link #referenceable()}
     * @param referenceableOnly controls whether we should allow parsing and validation for types marked with {@link AccessoryType#allowReference()}
     */
    private AccessoryTypeArgument(boolean referenceableOnly) {
        this.referenceableOnly = referenceableOnly;
    }

    /**
     * Creates an argument for any {@link AccessoryType}s
     * @return a non-referenceable-asserting argument instance
     */
    public static AccessoryTypeArgument any() {
        return new AccessoryTypeArgument(false);
    }

    /**
     * Creates an argument for referenceable {@link AccessoryType}s
     * @return a referenceable-asserting argument instance
     */
    public static AccessoryTypeArgument referenceable() {
        return new AccessoryTypeArgument(true);
    }

    /**
     * Retrieves the {@link AccessoryType} from the command via named lookup
     * @param context provided with {@link ArgumentBuilder#executes(Command)}
     * @param name key for the type in the command
     * @return the type present in the command, {@link NonNull} as it will simply throw if an instance with the given {@code name} is not found
     */
    public static @NonNull AccessoryType getType(@NonNull CommandContext<CommandSourceStack> context, @NonNull String name) {
        return context.getArgument(name, AccessoryType.class);
    }

    @Override
    public @NonNull AccessoryType parse(@NonNull StringReader reader) throws CommandSyntaxException {
        Identifier id = Identifier.read(reader);

        if (AccessoryTypeManager.exists(id)) {
            AccessoryType type = AccessoryTypeManager.get(id);

            if (!referenceableOnly || type.allowReference()) {
                return type;
            }

            throw EXCEPTION_UNSPECIFIABLE_TYPE.create(id);
        } else {
            throw EXCEPTION_UNKNOWN_TYPE.create(id);
        }
    }

    @Override
    public <S> @NonNull CompletableFuture<Suggestions> listSuggestions(@NonNull CommandContext<S> context, @NonNull SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(AccessoryTypeManager.getTypeIdentifiers(referenceableOnly).stream().map(Identifier::toString), builder);
    }

    @Override
    public @NonNull Collection<String> getExamples() {
        return EXAMPLES;
    }
}
