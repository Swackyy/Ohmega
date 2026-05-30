package com.swacky.ohmega.api.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Class for registering {@code /ohmega} command nodes
 */
public final class OhmegaCommandNodes {
    private static final Map<String, ICommandNode.Factory> NODES = new IdentityHashMap<>();

    /**
     * Register a command node
     * @param key the root element key that immediately follows {@code /ohmega}
     * @param factory a function reference, usually to your {@link ICommandNode} constructor
     * @return {@code true} if registered successfully, {@code false} otherwise
     */
    public static boolean register(String key, ICommandNode.Factory factory) {
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
    public static void registerNodes(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        for (Map.Entry<String, ICommandNode.Factory> entry : NODES.entrySet()) {
            LiteralArgumentBuilder<CommandSourceStack> node = Commands.literal(entry.getKey());

            entry.getValue().construct(context, node);
            builder.then(node);
        }
    }
}
