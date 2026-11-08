package com.swacky.ohmega.api.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.common.command.CommandHelper;
import com.swacky.ohmega.api.common.command.ICommonCommandSource;
import com.swacky.ohmega.api.common.command.OhmegaCommandNodes;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Class for registering {@code /ohmegac} command nodes, available on the client
 * @apiNote Custom commands should most likely only be used if you are creating an addon for the mod itself, or if you are me :p
 */
public final class OhmegaClientCommandNodes {
    private static final @NonNull Map<@Nullable String, ICommandNode.@NonNull CommonFactory> NODES = new IdentityHashMap<>();

    /**
     * Register a client command node
     * @param key the root element key that immediately follows {@code /ohmegac}
     * @param factory a function reference, usually to your {@link ICommandNode} constructor
     * @return {@code true} if registered successfully, {@code false} otherwise
     * @apiNote If you are wishing to register a common command node,
     * you must call this and also {@link OhmegaCommandNodes#registerCommon(String, ICommandNode.CommonFactory)}
     * in a suitable location within your project with the same command
     */
    public static boolean register(@Nullable String key, ICommandNode.@NonNull CommonFactory factory) {
        if (!NODES.containsKey(key)) {
            NODES.put(key, factory);
            return true;
        }

        return false;
    }

    /**
     * Used to register client (and common, on client-side) command nodes, called internally, do not call this
     * @param context build context supplied by command registration
     * @param builder the root {@code /ohmegac} literal to build on
     */
    public static <T extends SharedSuggestionProvider> void registerNodes(
            @NonNull CommandBuildContext context, @NonNull LiteralArgumentBuilder<T> builder, ICommonCommandSource.@NonNull Factory<T> sourceFactory) {
        CommandHelper<T> helper = new CommandHelper<>(sourceFactory);

        for (Map.Entry<@Nullable String, ICommandNode.@NonNull CommonFactory> entry : NODES.entrySet()) {
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
}
