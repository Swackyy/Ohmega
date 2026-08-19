package com.swacky.ohmega.api.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Class for registering {@code /ohmega} command nodes
 */
public final class OhmegaCommandNodes {
    private static final @NonNull Map<@Nullable String, ICommandNode.@NonNull Factory> NODES = new IdentityHashMap<>();

    /**
     * Register a command node
     * @param key the root element key that immediately follows {@code /ohmega}
     * @param factory a function reference, usually to your {@link ICommandNode} constructor
     * @return {@code true} if registered successfully, {@code false} otherwise
     * @apiNote Using a {@code null} key is not recommended and should only be done if you have good reason.
     * If two commands are registered with the same key, including {@code null}, the last invocation will take priority
     */
    public static boolean register(@Nullable String key, ICommandNode.@NonNull Factory factory) {
        if (!NODES.containsKey(key)) {
            NODES.put(key, factory);
            return true;
        }

        return false;
    }

    /**
     * Used to register command nodes, called internally, do not call this
     * @param context build context supplied by command registration
     * @param builder the root {@code /ohmega} literal to build on
     */
    public static void registerNodes(@NonNull CommandBuildContext context, @NonNull LiteralArgumentBuilder<CommandSourceStack> builder) {
        for (Map.Entry<String, ICommandNode.Factory> entry : NODES.entrySet()) {
            String key = entry.getKey();
            LiteralArgumentBuilder<CommandSourceStack> node;

            if (key == null) {
                node = builder;
            } else {
                node = Commands.literal(key);
            }

            entry.getValue().construct(context, node);

            if (key != null) {
                builder.then(node);
            }
        }
    }
}
