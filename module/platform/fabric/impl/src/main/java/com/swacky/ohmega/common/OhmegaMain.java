package com.swacky.ohmega.common;

import com.swacky.ohmega.api.IOhmegaEntrypoint;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypePredicateArgument;
import com.swacky.ohmega.common.event.CommonEvents;
import com.swacky.ohmega.common.init.OhmegaDataAttachmentsImpl;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import com.swacky.ohmega.api.network.C2S.KeybindUsePacket;
import com.swacky.ohmega.api.network.C2S.SetExtensionVisiblePacket;
import com.swacky.ohmega.api.network.C2S.SetHiddenPacket;
import com.swacky.ohmega.api.network.OhmegaNetworking;
import com.swacky.ohmega.api.network.S2C.SyncDataPacket;
import com.swacky.ohmega.api.network.S2C.SyncHiddenPacket;
import com.swacky.ohmega.api.network.S2C.SyncKeybindUsePacket;
import com.swacky.ohmega.api.network.S2C.SyncSlotsPacket;
import com.swacky.ohmega.api.network.S2C.SyncStacksPacket;
import com.swacky.ohmega.api.network.S2C.SyncTypesPacket;
import com.swacky.ohmega.api.util.LogicalSide;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.EntrypointException;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.util.ExceptionUtil;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.config.ModConfig;

import java.util.Collection;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class OhmegaMain implements ModInitializer {
    @Override
    public void onInitialize() {
        // Bootstrap
        OhmegaBootstrap.bootstrap();
        CommonEvents.bootstrap();

        // Config
        ConfigRegistry.INSTANCE.register(Ohmega.MODID, ModConfig.Type.SERVER, OhmegaConfigImpl.Server.getSpec());

        // Networking
        // Send
        // C2S
        PayloadTypeRegistry.serverboundPlay().register(KeybindUsePacket.TYPE, KeybindUsePacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetExtensionVisiblePacket.TYPE, SetExtensionVisiblePacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetHiddenPacket.TYPE, SetHiddenPacket.CODEC);
        // S2C
        PayloadTypeRegistry.clientboundPlay().register(SyncDataPacket.TYPE, SyncDataPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncHiddenPacket.TYPE, SyncHiddenPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncKeybindUsePacket.TYPE, SyncKeybindUsePacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncSlotsPacket.TYPE, SyncSlotsPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncStacksPacket.TYPE, SyncStacksPacket.CODEC);
        PayloadTypeRegistry.clientboundConfiguration().register(SyncTypesPacket.TYPE, SyncTypesPacket.CODEC);
        // Receive
        ServerPlayNetworking.registerGlobalReceiver(KeybindUsePacket.TYPE, (packet, context) ->
                OhmegaNetworking.C2S.handleKeybindUse(packet, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SetExtensionVisiblePacket.TYPE, (packet, context) ->
                OhmegaNetworking.C2S.handleSetExtensionVisible(packet, context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SetHiddenPacket.TYPE, (packet, context) ->
                OhmegaNetworking.C2S.handleSetHidden(packet, context.player()));

        // Registration
        ArgumentTypeRegistry.registerArgumentType(
                Ohmega.id(AccessoryTypeArgument.KEY), AccessoryTypeArgument.class, AccessoryTypeArgument.SERIALISER);
        ArgumentTypeRegistry.registerArgumentType(
                Ohmega.id(AccessoryTypePredicateArgument.KEY), AccessoryTypePredicateArgument.class, AccessoryTypePredicateArgument.SERIALISER);
        OhmegaDataAttachmentsImpl.init();

        // Resource loader
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Ohmega.id(Ohmega.MODID), AccessoryTypeManager.getInstance());

        // Custom entrypoint invocation
        invokeEntrypoints("ohmega-common", IOhmegaEntrypoint.class, entrypoint -> Ohmega.invokeEntrypoint(LogicalSide.COMMON, entrypoint));

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            invokeEntrypoints("ohmega-server", IOhmegaEntrypoint.class, entrypoint -> Ohmega.invokeEntrypoint(LogicalSide.SERVER, entrypoint));
        }
    }

    public static <T> void invokeEntrypoints(String key, Class<T> type, Consumer<T> invoker) {
        RuntimeException exception = null;
        Collection<EntrypointContainer<T>> entrypoints = FabricLoaderImpl.INSTANCE.getEntrypointContainers(key, type);

        Log.debug(LogCategory.ENTRYPOINT, "Iterating over entrypoint '%s'", key);

        for (EntrypointContainer<T> container : entrypoints) {
            try {
                invoker.accept(container.getEntrypoint());
            } catch (EntrypointException _) {
                // no op
            } catch (Throwable t) {
                exception = ExceptionUtil.gatherExceptions(t,
                        exception,
                        exc -> new RuntimeException(String.format("Could not execute entrypoint stage '%s' due to errors, provided by '%s' at '%s'!",
                                key, container.getProvider().getMetadata().getId(), container.getDefinition()),
                                exc));
            }
        }

        if (exception != null) {
            throw exception;
        }
    }
}