package com.swacky.ohmega.common;

import com.swacky.ohmega.api.common.command.OhmegaCommandNodes;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.common.command.node.ClearCommand;
import com.swacky.ohmega.client.command.node.ExtensionsCommand;
import com.swacky.ohmega.client.command.node.HelpCommand;
import com.swacky.ohmega.client.command.node.InfoCommand;
import com.swacky.ohmega.common.command.node.ItemCommand;
import com.swacky.ohmega.common.command.node.ItemsCommand;
import com.swacky.ohmega.common.command.node.TypesCommand;
import com.swacky.ohmega.common.init.OhmegaDataComponents;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.event.OhmegaHooks;
import com.swacky.ohmega.network.OhmegaNetworking;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;

public final class Ohmega {
    public static final String MODID = "ohmega";
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier INTERFACE_ID = id("default");
    public static final String MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE = "This method was called without a defined functional method body. Implement it in your mixin class";

    private static boolean bootstrapped = false;
    private static int NUM_SERVICES = 0;

    public static <T> T loadService(Class<T> clazz) {
        String name = clazz.getName();
        T service = ServiceLoader.load(clazz).findFirst().orElseThrow(() ->
                new RuntimeException("Could not load service '" + name + "' as no implementation was found"));
        NUM_SERVICES++;

        LOGGER.debug("Loaded implementation '{}' for service '{}'", service.getClass().getName(), name);
        return service;
    }

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            // Bootstrap services
            AccessoryHelper.bootstrap();
            OhmegaDataComponents.bootstrap();
            OhmegaItems.bootstrap();
            OhmegaConfig.Server.bootstrap();
            OhmegaHooks.bootstrap();
            OhmegaNetworking.bootstrap();
            LOGGER.info("Successfully loaded {} services", NUM_SERVICES);

            // Register command nodes
            OhmegaCommandNodes.register(ClearCommand.ELEMENT_ROOT, ClearCommand::new);
            OhmegaCommandNodes.register(ExtensionsCommand.ELEMENT_ROOT, ExtensionsCommand::new);
            OhmegaCommandNodes.register(HelpCommand.ELEMENT_ROOT, HelpCommand::new);
            OhmegaCommandNodes.register(InfoCommand.ELEMENT_ROOT, InfoCommand::new);
            OhmegaCommandNodes.register(ItemCommand.ELEMENT_ROOT, ItemCommand::new);
            OhmegaCommandNodes.register(ItemsCommand.ELEMENT_ROOT, ItemsCommand::new);
            OhmegaCommandNodes.register(TypesCommand.ELEMENT_ROOT, TypesCommand::new);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + Ohmega.class + " multiple times");
        }
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
