package com.swacky.ohmega.client;

import com.swacky.ohmega.api.IOhmegaEntrypoint;
import com.swacky.ohmega.api.client.OhmegaClient;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.api.network.OhmegaNetworking;
import com.swacky.ohmega.api.network.S2C.SyncDataPacket;
import com.swacky.ohmega.api.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.api.network.S2C.SyncKeybindUsePacket;
import com.swacky.ohmega.api.network.S2C.SyncSlotsPacket;
import com.swacky.ohmega.api.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.api.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.api.util.LogicalSide;
import com.swacky.ohmega.client.model.HaloModel;
import com.swacky.ohmega.client.renderer.HaloRenderer;
import com.swacky.ohmega.client.screen.widget.CrowdinButton;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.client.event.ClientEvents;
import com.swacky.ohmega.common.OhmegaMain;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

@SuppressWarnings("unused")
public final class OhmegaClientMain implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Bootstrap
        OhmegaClientBootstrap.bootstrap();
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
        ClientPlayNetworking.registerGlobalReceiver(SyncDataPacket.TYPE, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncData(packet));
        ClientPlayNetworking.registerGlobalReceiver(SyncHiddenPacket.TYPE, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncHidden(packet));
        ClientPlayNetworking.registerGlobalReceiver(SyncKeybindUsePacket.TYPE, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncKeybindUse(packet));
        ClientPlayNetworking.registerGlobalReceiver(SyncSlotsPacket.TYPE, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncSlots(packet));
        ClientPlayNetworking.registerGlobalReceiver(SyncStacksPacket.TYPE, (packet, _) ->
                OhmegaNetworking.S2C.handleSyncStacks(packet));
        ClientConfigurationNetworking.registerGlobalReceiver(SyncTypesPacket.TYPE, (packet, context) ->
                OhmegaNetworking.S2C.handleSyncTypes(packet, context.packetListener().receivedRegistries));

        // Registration
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.EDIT_MAGNETICS);
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.EDIT_NUDGE_DOWN);
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.EDIT_NUDGE_LEFT);
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.EDIT_NUDGE_RIGHT);
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.EDIT_NUDGE_UP);
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.EDIT_REDO);
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.EDIT_SHOW_LINES);
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.EDIT_UNDO);
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.OPEN_ACCESSORY_INVENTORY);
        KeyMappingHelper.registerKeyMapping(OhmegaBinds.OPEN_EDIT_UI);

        // Rendering
        AccessoryRenderers.registerLiving(OhmegaItems.getAngelRing(), HaloRenderer::new);
        ModelLayerRegistry.registerModelLayer(HaloModel.LOCATION, HaloModel::createDefinition);

        // Resource packs
        ResourceLoader.registerBuiltinPack(
                OhmegaClient.PACK_DARK_ID,
                FabricLoader.getInstance().getModContainer(Ohmega.MODID).orElseThrow(),
                PackActivationType.NORMAL);

        FabricLoader.getInstance().invokeEntrypoints("ohmega-client", IOhmegaEntrypoint.class, entrypoint -> Ohmega.invokeEntrypoint(LogicalSide.CLIENT, entrypoint));
        OhmegaMain.invokeEntrypointsUnsafe("ohmega-client-unsafe", IOhmegaEntrypoint.class, entrypoint -> Ohmega.invokeEntrypoint(LogicalSide.CLIENT, entrypoint));
    }
}
