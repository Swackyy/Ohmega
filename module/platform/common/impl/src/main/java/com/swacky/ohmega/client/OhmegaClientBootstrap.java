package com.swacky.ohmega.client;

import com.swacky.ohmega.api.client.OhmegaClient;
import com.swacky.ohmega.api.client.command.OhmegaClientCommandNodes;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderStateData;
import com.swacky.ohmega.api.client.ui.AccessoryExtensions;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.api.config.OhmegaConfig;
import com.swacky.ohmega.client.command.node.ExtensionsCommand;
import com.swacky.ohmega.client.command.node.HelpCommand;
import com.swacky.ohmega.client.command.node.InfoCommand;
import com.swacky.ohmega.client.menu.DefaultMenuExtension;
import com.swacky.ohmega.client.screen.DefaultScreenExtension;
import net.minecraft.resources.Identifier;

public final class OhmegaClientBootstrap {
    public static final Identifier DEFAULT_EXTENSION_ID = Ohmega.id("default");

    private static boolean bootstrapped = false;
    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            OhmegaClient.bootstrap();

            // Bootstrap services
            AccessoryRenderStateData.bootstrap();
            OhmegaBinds.bootstrap();
            OhmegaConfig.Client.bootstrap();

            OhmegaClient.lock();

            // Register extension
            AccessoryExtensions.registerExtension(DEFAULT_EXTENSION_ID, DefaultMenuExtension::new, DefaultScreenExtension::new);

            // Register command nodes
            OhmegaClientCommandNodes.register(ExtensionsCommand.ELEMENT_ROOT, ExtensionsCommand::new);
            OhmegaClientCommandNodes.register(HelpCommand.ELEMENT_ROOT, HelpCommand::new);
            OhmegaClientCommandNodes.register(InfoCommand.ELEMENT_ROOT, InfoCommand::new);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + OhmegaClientBootstrap.class + " multiple times");
        }
    }
}
