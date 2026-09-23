package com.swacky.ohmega.client.init;

import com.swacky.ohmega.api.client.init.OhmegaBinds;
import net.minecraft.client.KeyMapping;

@SuppressWarnings("unused")
public final class OhmegaBindsImpl implements OhmegaBinds.Service {
    @Override
    public KeyMapping createMapping(String name, int key) {
        return new OhmegaKeyMapping(name, key);
    }

    @Override
    public boolean isInstance(KeyMapping other) {
        return other instanceof OhmegaKeyMapping;
    }

    private static class OhmegaKeyMapping extends KeyMapping {
        public OhmegaKeyMapping(String name, int key) {
            super(name, key, OhmegaBinds.CATEGORY_ACCESSORY_TYPES);
        }
    }
}
