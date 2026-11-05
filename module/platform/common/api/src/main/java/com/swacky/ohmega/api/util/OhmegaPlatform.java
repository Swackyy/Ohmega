package com.swacky.ohmega.api.util;

import com.swacky.ohmega.api.common.Ohmega;
import org.jspecify.annotations.NonNull;

public final class OhmegaPlatform {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static boolean isModLoaded(@NonNull String modId) {
        return IMPL.isModLoaded(modId);
    }

    @LoaderService
    public interface Service {
        boolean isModLoaded(@NonNull String modId);
    }
}
