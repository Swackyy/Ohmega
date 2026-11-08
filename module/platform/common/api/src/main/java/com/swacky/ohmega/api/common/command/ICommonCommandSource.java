package com.swacky.ohmega.api.common.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Loader and side agnostic command source to use in common commands' {@link CommandContext} parameters
 */
public interface ICommonCommandSource {
    /**
     * Retrieve the player who executed the command, or {@code null} if it was not executed by a player (server only)
     * @return the (nullable) {@link Player} caller
     */
    @Nullable Player getPlayer();

    /**
     * Sends a success message to the command source
     * @param messageSupplier a supplier for the success feedback message {@link Component}
     */
    void sendSuccess(@NonNull Supplier<@NonNull Component> messageSupplier);

    /**
     * Sends a failure message to the command source
     * @param message failure feedback message {@link Component}
     */
    void sendFailure(Component message);

    /**
     * Retrieve the player who executed the command, or {@code throw} if the caller was {@code null} (server only)
     * @return the (non-null) {@link Player} caller
     */
    default @NonNull Player getPlayerOrException() {
        Player player = getPlayer();

        if (player != null) {
            return player;
        }

        throw new NullPointerException("Player is null");
    }

    interface Factory<T extends SharedSuggestionProvider> {
        @NonNull ICommonCommandSource construct(@NonNull CommandContext<T> context);
    }
}
