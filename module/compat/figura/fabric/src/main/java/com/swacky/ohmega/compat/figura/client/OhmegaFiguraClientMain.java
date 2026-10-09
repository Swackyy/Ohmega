package com.swacky.ohmega.compat.figura.client;

import com.swacky.ohmega.api.IOhmegaEntrypoint;
import com.swacky.ohmega.compat.figura.client.event.OhmegaFiguraClientEvents;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public class OhmegaFiguraClientMain implements IOhmegaEntrypoint {
    @Override
    public void invoke(@NonNull Function<String, Boolean> modLoaded) {
        if (modLoaded.apply("figura")) {
            OhmegaFiguraClientEvents.bootstrap();
        }
    }
}
