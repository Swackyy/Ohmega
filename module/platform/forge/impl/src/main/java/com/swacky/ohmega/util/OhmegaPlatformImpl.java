package com.swacky.ohmega.util;

import com.swacky.ohmega.api.util.OhmegaPlatform;
import net.minecraftforge.fml.ModList;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class OhmegaPlatformImpl implements OhmegaPlatform.Service {
    @Override
    public boolean isModLoaded(@NonNull String modId) {
        return ModList.isLoaded(modId);
    }
}
