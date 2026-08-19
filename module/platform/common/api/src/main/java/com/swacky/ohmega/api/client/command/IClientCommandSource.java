package com.swacky.ohmega.api.client.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

/**
 * Abstraction for the provided command source due to loader discrepancies
 */
public interface IClientCommandSource {
    /**
     * Sends a success message to the player
     * @param message the success message
     */
    void sendSuccess(@NonNull Component message);

    /**
     * Sends an error message to the player
     * @param message the error message
     */
    void sendError(@NonNull Component message);

    /**
     * Gets the player that used the command
     * @return the player
     */
    @NonNull LocalPlayer getPlayer();

    interface Factory<T extends SharedSuggestionProvider> {
        @NonNull IClientCommandSource construct(@NonNull CommandContext<T> context);
    }
}
