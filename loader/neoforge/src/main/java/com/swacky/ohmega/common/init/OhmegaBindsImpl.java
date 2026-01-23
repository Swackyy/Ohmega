package com.swacky.ohmega.common.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

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
            super(name, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, key, OhmegaBinds.CATEGORY_KEY);
        }
    }
}
