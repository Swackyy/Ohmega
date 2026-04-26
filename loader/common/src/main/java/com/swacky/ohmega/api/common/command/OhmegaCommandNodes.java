package com.swacky.ohmega.api.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.swacky.ohmega.api.common.command.node.OhmegaCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for registering extra {@code /ohmega} command nodes
 */
public final class OhmegaCommandNodes {
    private static final Map<String, OhmegaCommandNode.Factory> NODES = new HashMap<>();

    /**
     * Register a command node
     * @param key the root element key that immediately follows {@code /ohmega}
     * @param factory a function reference, usually to your {@link OhmegaCommandNode} constructor
     * @return {@code true} if registered successfully, {@code false} otherwise
     */
    public static boolean register(String key, OhmegaCommandNode.Factory factory) {
        if (!NODES.containsKey(key)) {
            NODES.put(key, factory);
            return true;
        }

        return false;
    }

    /**
     * Use to register command nodes, called internally, do not call this
     * @param context build context supplied by command registration
     * @param builder the root {@code /ohmega} literal argument to build on
     */
    public static void registerNodes(CommandBuildContext context, LiteralArgumentBuilder<CommandSourceStack> builder) {
        for (Map.Entry<String, OhmegaCommandNode.Factory> entry : NODES.entrySet()) {
            LiteralArgumentBuilder<CommandSourceStack> node = Commands.literal(entry.getKey());

            entry.getValue().construct(context, node);
            builder.then(node);
        }
    }
}
