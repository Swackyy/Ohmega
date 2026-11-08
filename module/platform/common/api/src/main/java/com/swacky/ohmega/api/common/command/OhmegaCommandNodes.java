package com.swacky.ohmega.api.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.client.command.OhmegaClientCommandNodes;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Class for registering {@code /ohmega} command nodes, available on both client and server
 * @apiNote Custom commands should most likely only be used if you are creating an addon for the mod itself, or if you are me :p
 */
public final class OhmegaCommandNodes {
    private static final @NonNull Map<@Nullable String, ICommandNode.@NonNull CommonFactory> COMMON_NODES = new IdentityHashMap<>();
    private static final @NonNull Map<@Nullable String, ICommandNode.@NonNull ServerFactory> SERVER_NODES = new IdentityHashMap<>();

    /**
     * Register a command node (common)
     * @param key the root element key that immediately follows {@code /ohmega}
     * @param factory a function reference, usually to your {@link ICommandNode} constructor
     * @return {@code true} if registered successfully, {@code false} otherwise
     * @apiNote
     * <ul>
     *     <li>
     *         This only registers the common node on the {@code /ohmega} command so it can be run on the server.
     *         You should additionally call {@link OhmegaClientCommandNodes#register(String, ICommandNode.CommonFactory)}
     *         in a suitable location within your project with the same command
     *     </li>
     *     <li>
     *         Using a {@code null} key is not recommended and should only be done if you have good reason.
     *         If two commands are registered with the same key, including {@code null}, the last invocation will take priority
     *     </li>
     * </ul>
     */
    public static boolean registerCommon(@Nullable String key, ICommandNode.@NonNull CommonFactory factory) {
        if (!COMMON_NODES.containsKey(key)) {
            COMMON_NODES.put(key, factory);
            return true;
        }

        return false;
    }

    /**
     * Register a command node (server only)
     * @param key the root element key that immediately follows {@code /ohmega}
     * @param factory a function reference, usually to your {@link ICommandNode} constructor
     * @return {@code true} if registered successfully, {@code false} otherwise
     * @apiNote Using a {@code null} key is not recommended and should only be done if you have good reason.
     * If two commands are registered with the same key, including {@code null}, the last invocation will take priority
     */
    public static boolean registerServer(@Nullable String key, ICommandNode.@NonNull ServerFactory factory) {
        if (!SERVER_NODES.containsKey(key)) {
            SERVER_NODES.put(key, factory);
            return true;
        }

        return false;
    }

    /**
     * Used to register command nodes, called internally, do not call this
     * @param context build context supplied by command registration
     * @param builder the root {@code /ohmega} literal to build on
     */
    public static <T extends SharedSuggestionProvider> void registerCommonNodes(
            @NonNull CommandBuildContext context, @NonNull LiteralArgumentBuilder<T> builder, ICommonCommandSource.@NonNull Factory<T> sourceFactory) {
        CommandHelper<T> helper = new CommandHelper<>(sourceFactory);

        for (Map.Entry<@Nullable String, ICommandNode.@NonNull CommonFactory> entry : COMMON_NODES.entrySet()) {
            String key = entry.getKey();
            LiteralArgumentBuilder<T> node;

            if (key == null) {
                node = builder;
            } else {
                node = LiteralArgumentBuilder.literal(key);
            }

            entry.getValue().construct(context, node, helper);

            if (key != null) {
                builder.then(node);
            }
        }
    }

    /**
     * Used to register command nodes, called internally, do not call this
     * @param context build context supplied by command registration
     * @param builder the root {@code /ohmega} literal to build on
     */
    public static void registerServerNodes(@NonNull CommandBuildContext context, @NonNull LiteralArgumentBuilder<CommandSourceStack> builder) {
        for (Map.Entry<@Nullable String, ICommandNode.@NonNull ServerFactory> entry : SERVER_NODES.entrySet()) {
            String key = entry.getKey();
            LiteralArgumentBuilder<CommandSourceStack> node;

            if (key == null) {
                node = builder;
            } else {
                node = LiteralArgumentBuilder.literal(key);
            }

            entry.getValue().construct(context, node);

            if (key != null) {
                builder.then(node);
            }
        }
    }
}
