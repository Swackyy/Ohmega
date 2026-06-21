package com.swacky.ohmega.api.client.ui;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A unified registration and data holder for accessory extension menus and screens, and some common related methods
 */
public final class AccessoryExtensions {
    private static final @NonNull Map<Identifier, Pair<AccessoryMenuExtension.Factory, AccessoryScreenExtension.Factory>> EXTENSIONS = new HashMap<>();

    /**
     * Use this to register an extension type, this will then be an available option to choose from in the client config,
     * allowing you to pick which extension to use.
     * @param id identifier corresponding to the type in the config
     * @param menuFactory factory for the menu extension, will be constructed later
     * @param screenFactory factory for the screen extension, will be constructed later
     */
    public static void registerExtension(@NonNull Identifier id, AccessoryMenuExtension.@NonNull Factory menuFactory, AccessoryScreenExtension.@NonNull Factory screenFactory) {
        if (!EXTENSIONS.containsKey(id)) {
            EXTENSIONS.put(id, Pair.of(menuFactory, screenFactory));
        }
    }

    /**
     * Get a set of known extensions through their {@link Identifier} keys
     * @return the extensions map keyset
     */
    public static @NonNull Set<Identifier> getExtensionKeys() {
        return EXTENSIONS.keySet();
    }

    /**
     * Check if an extension with the given {@link Identifier} exists.
     * Used for config value validation internally, but you may also use it
     * @param id identifier to check
     * @return {@code true} if an extension with the supplied {@code id} exists, {@code false} otherwise
     */
    public static boolean exists(@Nullable Identifier id) {
        if (id != null) {
            return EXTENSIONS.containsKey(id);
        }

        return false;
    }

    /**
     * Retrieve the active menu extension factory by parsing the client config value to an {@link Identifier}
     * @return the currently in-use menu extension factory
     */
    public static AccessoryMenuExtension.@NonNull Factory getActiveMenuFactory() {
        String rawId = OhmegaConfig.Client.getData().accessoryExtensionId().getObject();

        if (rawId != null) {
            Identifier id = Identifier.tryParse(rawId);

            if (id != null) {
                return EXTENSIONS.get(id).getLeft();
            }
        }

        return EXTENSIONS.get(OhmegaClient.DEFAULT_EXTENSION_ID).getLeft();
    }

    /**
     * Retrieve the active menu extension factory by parsing the client config value to an {@link Identifier}
     * @return the currently in-use menu extension factory
     */
    public static AccessoryScreenExtension.@NonNull Factory getActiveScreenFactory() {
        String rawId = OhmegaConfig.Client.getData().accessoryExtensionId().getObject();

        if (rawId != null) {
            Identifier id = Identifier.tryParse(rawId);

            if (id != null) {
                return EXTENSIONS.get(id).getRight();
            }
        }

        return EXTENSIONS.get(OhmegaClient.DEFAULT_EXTENSION_ID).getRight();
    }
}
