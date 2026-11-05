package com.swacky.ohmega.compat.figura.client;

import com.swacky.ohmega.api.IOhmegaEntrypoint;
import com.swacky.ohmega.api.util.OhmegaPlatform;
import com.swacky.ohmega.compat.figura.client.event.OhmegaFiguraClientEvents;

public class OhmegaFiguraClientMain implements IOhmegaEntrypoint {
    @Override
    public void invoke() {
        if (OhmegaPlatform.isModLoaded("figura")) {
            OhmegaFiguraClientEvents.bootstrap();
        }
    }
}
