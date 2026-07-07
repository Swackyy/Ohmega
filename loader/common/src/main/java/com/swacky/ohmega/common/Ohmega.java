package com.swacky.ohmega.common;

import com.swacky.ohmega.api.common.command.OhmegaCommandNodes;
import com.swacky.ohmega.api.common.init.OhmegaCriteriaTriggers;
import com.swacky.ohmega.api.common.init.OhmegaDataComponents;
import com.swacky.ohmega.api.common.init.OhmegaItems;
import com.swacky.ohmega.common.command.node.ClearCommand;
import com.swacky.ohmega.common.command.node.ItemCommand;
import com.swacky.ohmega.common.command.node.ItemsCommand;
import com.swacky.ohmega.common.command.node.SlotsCommand;
import com.swacky.ohmega.common.command.node.TypesCommand;
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
    public static final String MIXIN_UNIMPLEMENTED_EXCEPTION_MESSAGE = "This method was called without a defined functional method body. Implement it in your mixin class";

    private static boolean bootstrapped = false;
    private static int servicesCount = 0;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            // Bootstrap services
            OhmegaCriteriaTriggers.bootstrap();
            OhmegaDataComponents.bootstrap();
            OhmegaItems.bootstrap();
            OhmegaConfig.Server.bootstrap();
            OhmegaHooks.bootstrap();
            OhmegaNetworking.bootstrap();
            LOGGER.info("Successfully loaded {} common services", servicesCount);

            // Register command nodes
            OhmegaCommandNodes.register(ClearCommand.ELEMENT_ROOT, ClearCommand::new);
            OhmegaCommandNodes.register(ItemCommand.ELEMENT_ROOT, ItemCommand::new);
            OhmegaCommandNodes.register(ItemsCommand.ELEMENT_ROOT, ItemsCommand::new);
            OhmegaCommandNodes.register(SlotsCommand.ELEMENT_ROOT, SlotsCommand::new);
            OhmegaCommandNodes.register(TypesCommand.ELEMENT_ROOT, TypesCommand::new);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + Ohmega.class + " multiple times");
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
            throw new IllegalStateException("Common service loading called before Common bootstrapping");
        }
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
