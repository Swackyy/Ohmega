package com.swacky.ohmega.compat.rei.client;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public final class OhmegaReiClientMain implements REIClientPlugin {
    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        OhmegaRei.registerExclusionZones(zones);
    }
}
