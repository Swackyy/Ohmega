package com.swacky.ohmega.api.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.client.command.node.IClientCommandNode;
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
    private static final @NonNull Map<String, IClientCommandNode.Factory> NODES = new IdentityHashMap<>();

    /**
     * Register a client command node
     * @param key the root element key that immediately follows {@code /ohmegac}
     * @param factory a function reference, usually to your {@link IClientCommandNode} constructor
     * @return {@code true} if registered successfully, {@code false} otherwise
     */
    public static boolean register(@Nullable String key, IClientCommandNode.@NonNull Factory factory) {
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
    public static <T extends SharedSuggestionProvider> void registerNodes(
            @NonNull CommandBuildContext context, @NonNull LiteralArgumentBuilder<T> builder, IClientCommandSource.@NonNull Factory<T> sourceFactory) {
        for (Map.Entry<String, IClientCommandNode.Factory> entry : NODES.entrySet()) {
            String key = entry.getKey();
            LiteralArgumentBuilder<T> node;

            if (key == null) {
                node = builder;
            } else {
                node = LiteralArgumentBuilder.literal(key);
            }

            entry.getValue().construct(context, node, sourceFactory);

            if (key != null) {
                builder.then(node);
            }
        }
    }
}
