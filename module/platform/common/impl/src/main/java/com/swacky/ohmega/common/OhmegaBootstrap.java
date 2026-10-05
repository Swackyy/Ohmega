package com.swacky.ohmega.common;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.command.OhmegaCommandNodes;
import com.swacky.ohmega.api.common.config.OhmegaServerConfig;
import com.swacky.ohmega.api.common.event.OhmegaHooks;
import com.swacky.ohmega.api.common.init.OhmegaCriteriaTriggers;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.init.OhmegaDataComponents;
import com.swacky.ohmega.api.network.OhmegaNetworking;
import com.swacky.ohmega.common.command.node.ClearCommand;
import com.swacky.ohmega.common.command.node.DataCommand;
import com.swacky.ohmega.common.command.node.ItemCommand;
import com.swacky.ohmega.common.command.node.ItemsCommand;
import com.swacky.ohmega.common.command.node.SlotsCommand;
import com.swacky.ohmega.common.command.node.TypesCommand;
import com.swacky.ohmega.common.init.OhmegaItems;

public final class OhmegaBootstrap {
    public static final String MODID = "ohmega";

    private static boolean bootstrapped = false;

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;

            // Secondary bootstrap for checking non-linearity
            Ohmega.bootstrap();

            // Bootstrap services
            OhmegaCriteriaTriggers.bootstrap();
            OhmegaDataAttachments.bootstrap();
            OhmegaDataComponents.bootstrap();
            OhmegaItems.bootstrap();
            OhmegaHooks.bootstrap();
            OhmegaNetworking.bootstrap();
            OhmegaServerConfig.bootstrap();

            // Lock service loading
            Ohmega.lock();

            // Register command nodes
            OhmegaCommandNodes.register(ClearCommand.ELEMENT_ROOT, ClearCommand::new);
            OhmegaCommandNodes.register(DataCommand.ELEMENT_ROOT, DataCommand::new);
            OhmegaCommandNodes.register(ItemCommand.ELEMENT_ROOT, ItemCommand::new);
            OhmegaCommandNodes.register(ItemsCommand.ELEMENT_ROOT, ItemsCommand::new);
            OhmegaCommandNodes.register(SlotsCommand.ELEMENT_ROOT, SlotsCommand::new);
            OhmegaCommandNodes.register(TypesCommand.ELEMENT_ROOT, TypesCommand::new);
        } else {
            throw new IllegalStateException("Attempted to bootstrap " + OhmegaBootstrap.class + " multiple times");
        }
    }
}
