package com.swacky.ohmega.client;

import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.client.model.HaloModel;
import com.swacky.ohmega.client.renderer.HaloRenderer;
import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.client.screen.button.CrowdinButton;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.event.ClientEvents;
import com.swacky.ohmega.network.OhmegaNetworking;
import com.swacky.ohmega.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.network.S2C.SyncUsePacket;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

@SuppressWarnings("unused")
public final class OhmegaClientMain implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Bootstrap
        OhmegaClient.bootstrap();
        ClientEvents.bootstrap();

        // Config
        ConfigRegistry.INSTANCE.register(Ohmega.MODID, ModConfig.Type.CLIENT, OhmegaConfigImpl.Client.getSpec());
        ConfigScreenFactoryRegistry.INSTANCE.register(Ohmega.MODID, (modId, parentScreen) -> {
            ConfigurationScreen configScreen = new ConfigurationScreen(modId, parentScreen);

            configScreen.addRenderableWidget(new CrowdinButton(configScreen));
            return configScreen;
        });

        // Networking
        // Receive
        ClientPlayNetworking.registerGlobalReceiver(SyncHiddenPacket.TYPE, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncHidden(packet));
        ClientPlayNetworking.registerGlobalReceiver(SyncStacksPacket.TYPE, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncStacks(packet));
        ClientConfigurationNetworking.registerGlobalReceiver(SyncTypesPacket.TYPE, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncTypes(packet));
        ClientPlayNetworking.registerGlobalReceiver(SyncUsePacket.TYPE, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncUse(packet));

        // Registration
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.OPEN_ACC_INV);
        MenuScreens.register(OhmegaMenus.getAccessoryMenu(), AccessoryInventoryScreen::new);

        // Rendering
        AccessoryRenderers.register(OhmegaItems.getAngelRing(), HaloRenderer::new);
        ModelLayerRegistry.registerModelLayer(HaloModel.LOCATION, HaloModel::createDefinition);
    }
}
