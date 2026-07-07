package com.swacky.ohmega.client;

import com.swacky.ohmega.api.client.command.OhmegaClientCommandNodes;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.api.client.ui.AccessoryExtensions;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.client.command.node.ExtensionsCommand;
import com.swacky.ohmega.client.command.node.HelpCommand;
import com.swacky.ohmega.client.command.node.InfoCommand;
import com.swacky.ohmega.client.menu.DefaultMenuExtension;
import com.swacky.ohmega.client.screen.DefaultScreenExtension;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;

public final class OhmegaClient {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier DEFAULT_EXTENSION_ID = Ohmega.id("default");
    public static final String LINK_CROWDIN = "https://crowdin.com/project/ohmega";
    public static final Identifier PACK_DARK_ID = Ohmega.id("dark");

    private static boolean bootstrapped = false;
    private static int servicesCount = 0;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            // Bootstrap services
            AccessoryRenderStateData.bootstrap();
            OhmegaBinds.bootstrap();
            OhmegaConfig.Client.bootstrap();
            LOGGER.info("Successfully loaded {} client services", servicesCount);

            // Register extension
            AccessoryExtensions.registerExtension(DEFAULT_EXTENSION_ID, DefaultMenuExtension::new, DefaultScreenExtension::new);

            // Register command nodes
            OhmegaClientCommandNodes.register(ExtensionsCommand.ELEMENT_ROOT, ExtensionsCommand::new);
            OhmegaClientCommandNodes.register(HelpCommand.ELEMENT_ROOT, HelpCommand::new);
            OhmegaClientCommandNodes.register(InfoCommand.ELEMENT_ROOT, InfoCommand::new);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + OhmegaClient.class + " multiple times");
        }
    }

    public static <T> T loadService(Class<T> clazz) {
        if (bootstrapped) {
            String name = clazz.getName();
            T service = ServiceLoader.load(clazz).findFirst().orElseThrow(() ->
                    new RuntimeException("Could not load service '" + name + "' as no implementation was found"));
            servicesCount++;

            LOGGER.debug("Loaded implementation '{}' for service '{}'", service.getClass().getName(), name);
            return service;
        } else {
            throw new IllegalStateException("Client service loading called either before Client bootstrapping or on the wrong distribution");
        }
    }
}
