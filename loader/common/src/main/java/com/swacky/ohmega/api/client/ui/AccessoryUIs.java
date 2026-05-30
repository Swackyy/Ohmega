package com.swacky.ohmega.api.client.ui;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.client.OhmegaClient;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A unified registration and data holder for accessory extension menus and screens, and some common related methods
 */
public final class AccessoryUIs {
    private static final Map<Identifier, Pair<AccessoryMenuExtension.Factory, AccessoryScreenExtension.Factory>> EXTENSIONS = new HashMap<>();

    /**
     * Use this to register an extension type, this will then be an available option to choose from in the client config,
     * allowing you to pick which extension to use.
     * @param id identifier corresponding to the type in the config
     * @param menuFactory factory for the menu extension, will be constructed later
     * @param screenFactory factory for the screen extension, will be constructed later
     */
    public static void registerExtension(Identifier id, AccessoryMenuExtension.Factory menuFactory, AccessoryScreenExtension.Factory screenFactory) {
        if (!EXTENSIONS.containsKey(id)) {
            EXTENSIONS.put(id, Pair.of(menuFactory, screenFactory));
        }
    }

    /**
     * Get a set of known extensions through their {@link Identifier} keys
     * @return the extensions map keyset
     */
    public static Set<Identifier> getExtensionKeys() {
        return EXTENSIONS.keySet();
    }

    public static boolean exists(Identifier id) {
        if (id != null) {
            return EXTENSIONS.containsKey(id);
        }

        return false;
    }

    /**
     * Retrieve the active menu extension factory by parsing the client config value to an {@link Identifier}
     * @return the currently in-use menu extension factory
     */
    public static AccessoryMenuExtension.Factory getActiveMenuFactory() {
        Identifier id = Identifier.tryParse(OhmegaConfig.Client.getData().accessoryExtensionId().getObject());

        if (id != null) {
            return EXTENSIONS.get(id).getLeft();
        }

        return EXTENSIONS.get(OhmegaClient.DEFAULT_EXTENSION_ID).getLeft();
    }

    /**
     * Retrieve the active menu extension factory by parsing the client config value to an {@link Identifier}
     * @return the currently in-use menu extension factory
     */
    public static AccessoryScreenExtension.Factory getActiveScreenFactory() {
        Identifier id = Identifier.tryParse(OhmegaConfig.Client.getData().accessoryExtensionId().getObject());

        if (id != null) {
            return EXTENSIONS.get(id).getRight();
        }

        return EXTENSIONS.get(OhmegaClient.DEFAULT_EXTENSION_ID).getRight();
    }
}
