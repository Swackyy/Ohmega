package com.swacky.ohmega.api.common.init;

import com.mojang.blaze3d.platform.InputConstants;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.jspecify.annotations.NonNull;

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
            super(name, KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, key, OhmegaBinds.CATEGORY);
        }

        @Override
        public @NonNull Component getDisplayName() {
            String key = this.getName();
            int index = key.lastIndexOf('_');

            return Component.translatable(
                    "key." + Ohmega.MODID + ".accessory_type",
                    Component.translatable(key.substring(0, index).replace("key", "accessory_type")),
                    Integer.parseInt(key.substring(index + 1)) + 1);
        }
    }
}
