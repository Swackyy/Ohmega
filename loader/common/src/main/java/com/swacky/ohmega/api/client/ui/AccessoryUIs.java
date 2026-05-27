package com.swacky.ohmega.api.client.ui;

import com.swacky.ohmega.api.client.screen.AccessoryScreenExtension;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Holds known extension factories for both menus and screens, and common related methods
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

    /**
     * Retrieve the active menu extension factory by parsing the client config value to an {@link Identifier}
     * @return the currently in-use menu extension factory
     */
    public static AccessoryMenuExtension.@Nullable Factory getActiveMenuFactory() {
        Identifier id = Identifier.tryParse(OhmegaConfig.Client.getData().accessoryExtensionId().getObject());

        if (id != null) {
            return EXTENSIONS.get(id).getLeft();
        }

        return null;
    }

    /**
     * Retrieve the active menu extension factory by parsing the client config value to an {@link Identifier}
     * @return the currently in-use menu extension factory
     */
    public static AccessoryScreenExtension.@Nullable Factory getActiveScreenFactory() {
        Identifier id = Identifier.tryParse(OhmegaConfig.Client.getData().accessoryExtensionId().getObject());

        if (id != null) {
            return EXTENSIONS.get(id).getRight();
        }

        return null;
    }
}
