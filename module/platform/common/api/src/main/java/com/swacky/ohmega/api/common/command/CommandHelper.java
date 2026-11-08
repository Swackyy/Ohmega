package com.swacky.ohmega.api.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.SharedSuggestionProvider;

/**
 * A helper class for <strong>common</strong> commands specifically, removing bulky generic specification on function calls and wrapping executors cleanly
 * @param <T> specified internally, type will depend on mod-loader
 */
public final class CommandHelper<T extends SharedSuggestionProvider> {
    private final ICommonCommandSource.Factory<T> sourceFactory;

    /**
     * Creates a helper instance, called internally
     * @param sourceFactory a factory method to converting loader and logical side specific command sources to a unified {@link ICommonCommandSource}
     */
    public CommandHelper(ICommonCommandSource.Factory<T> sourceFactory) {
        this.sourceFactory = sourceFactory;
    }

    /**
     * Wraps a command invoker accepting a {@link CommandContext}{@code <?>} and an {@link ICommonCommandSource} to a {@link Command}
     * @param executor function reference to wrap
     * @return wrapped {@link Command} instance
     */
    public Command<T> executor(ExecutorWithContext executor) {
        return context -> executor.execute(context, sourceFactory.construct(context.getSource()));
    }

    /**
     * Wraps a command invoker accepting an {@link ICommonCommandSource} to a {@link Command}
     * @param executor function reference to wrap
     * @return wrapped {@link Command} instance
     */
    public Command<T> executor(ExecutorNoContext executor) {
        return context -> executor.execute(sourceFactory.construct(context.getSource()));
    }

    /**
     * Creates a {@link LiteralArgumentBuilder} with the specified {@link T} generic type
     * @param literal string literal for the argument to be referenced by
     * @return generic-specified {@link LiteralArgumentBuilder}
     */
    public LiteralArgumentBuilder<T> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    /**
     * Creates a {@link RequiredArgumentBuilder} with the specified {@link T} generic type
     * @param name reference key for the argument
     * @param type argument type
     * @return generic-specified {@link RequiredArgumentBuilder}
     * @param <U> argument type, automatically inferred via the {@code type} param
     */
    public <U> RequiredArgumentBuilder<T, U> argument(String name, ArgumentType<U> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public interface ExecutorWithContext {
        int execute(CommandContext<?> context, ICommonCommandSource source) throws CommandSyntaxException;
    }

    public interface ExecutorNoContext {
        int execute(ICommonCommandSource source) throws CommandSyntaxException;
    }
}
