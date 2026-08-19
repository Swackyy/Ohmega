package com.swacky.ohmega.compat.rrv.client;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import com.swacky.ohmega.compat.rrv.event.ClientEvents;

@SuppressWarnings("unused")
public final class OhmegaRrvClientMain implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        ClientEvents.bootstrap();
    }
}
