package com.swacky.ohmega.api.common.command.node;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.OhmegaCommandNodes;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Represents an extra node to add to the {@code /ohmega} command.
 * Use {@link OhmegaCommandNodes#register(String, Factory)} to register your command nodes
 */
public abstract class OhmegaCommandNode {
    // todo
    /**
     * Get the map of raw help elements to their argument types. Do not return strings from {@link CommandHelper.Builder#feedback()},
     * these are automatically wrapped, instead return a list of element strings like "get" or "set" mapped to arguments.
     * <p>
     * The boolean part of the argument value marks {@code true} for mandatory and {@code false} for optional
     * @return a map of raw elements to add to the built-in {@code /ohmega help} command
     */
    public /*abstract*/ @NonNull Map<String, List<BooleanObjectPair<String>>> getHelpElements()/*;*/{
        return Map.of();
    }

    public interface Factory {
        @NonNull OhmegaCommandNode construct(@NonNull CommandBuildContext context, @NonNull LiteralArgumentBuilder<CommandSourceStack> builder);
    }
}
