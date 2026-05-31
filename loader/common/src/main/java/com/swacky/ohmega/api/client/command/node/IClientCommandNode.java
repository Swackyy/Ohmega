package com.swacky.ohmega.api.client.command.node;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.client.command.IClientCommandSource;
import com.swacky.ohmega.api.client.command.OhmegaClientCommandNodes;
import com.swacky.ohmega.api.common.command.CommandHelper;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Represents an extra node to add to the {@code /ohmegac} command.
 * Use {@link OhmegaClientCommandNodes#register(String, Factory)} to register your client command nodes
 */
public interface IClientCommandNode {
    // todo
    /**
     * Get the map of raw help elements to their argument types. Do not return strings from {@link CommandHelper.Builder#feedback()},
     * these are automatically wrapped. Instead, return a list of element strings like "get" or "set" mapped to arguments.
     * <p>
     * The boolean part of the argument value marks {@code true} for mandatory and {@code false} for optional
     * @return a map of raw elements to add to the built-in {@code /ohmegac help} command
     */
    default @NonNull Map<String, List<BooleanObjectPair<String>>> getHelpElements() {
        return Map.of();
    }

    interface Factory {
        <T extends SharedSuggestionProvider> @NonNull IClientCommandNode construct(@NonNull CommandBuildContext context, @NonNull LiteralArgumentBuilder<T> builder, IClientCommandSource.@NonNull Factory<T> sourceFactory);
    }
}
