package com.swacky.ohmega.api.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.client.command.node.IClientCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Class for registering {@code /ohmegac} command nodes.
 * These are only available on the client
 */
public final class OhmegaClientCommandNodes {
    private static final Map<String, IClientCommandNode.Factory> NODES = new IdentityHashMap<>();

    /**
     * Register a client command node
     * @param key the root element key that immediately follows {@code /ohmegac}
     * @param factory a function reference, usually to your {@link IClientCommandNode} constructor
     * @return {@code true} if registered successfully, {@code false} otherwise
     */
    public static boolean register(String key, IClientCommandNode.Factory factory) {
        if (!NODES.containsKey(key)) {
            NODES.put(key, factory);
            return true;
        }

        return false;
    }

    /**
     * Used to register client command nodes, called internally, do not call this
     * @param context build context supplied by command registration
     * @param builder the root {@code /ohmegac} literal to build on
     */
    public static <T extends SharedSuggestionProvider> void registerNodes(CommandBuildContext context, LiteralArgumentBuilder<T> builder, IClientCommandSource.Factory<T> sourceFactory) {
        for (Map.Entry<String, IClientCommandNode.Factory> entry : NODES.entrySet()) {
            LiteralArgumentBuilder<T> node = LiteralArgumentBuilder.literal(entry.getKey());

            entry.getValue().construct(context, node, sourceFactory);
            builder.then(node);
        }
    }
}
