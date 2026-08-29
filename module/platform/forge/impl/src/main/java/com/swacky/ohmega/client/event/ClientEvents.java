package com.swacky.ohmega.client.event;

import com.swacky.ohmega.api.client.OhmegaClient;
import com.swacky.ohmega.api.client.command.IClientCommandSource;
import com.swacky.ohmega.api.client.event.ClientCallbacks;
import com.swacky.ohmega.api.client.renderer.AccessoryRenderers;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaItems;
import com.swacky.ohmega.client.renderer.HaloRenderer;
import com.swacky.ohmega.config.OhmegaConfigImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = Ohmega.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    private static final Runnable LOAD_FUNCTION = () -> Minecraft.getInstance().options.load(true);

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(consumer -> {
                Pack pack = Pack.readMetaAndCreate(
                        new PackLocationInfo(
                                OhmegaClient.PACK_DARK_ID.toString(),
                                Component.literal(OhmegaClient.PACK_DARK_ID.getNamespace() + '/' + OhmegaClient.PACK_DARK_ID.getPath()),
                                PackSource.BUILT_IN,
                                Optional.empty()
                        ),
                        new PathPackResources.PathResourcesSupplier(ModList.getModFileById(Ohmega.MODID).getFile().findResource(
                                "resourcepacks",
                                OhmegaClient.PACK_DARK_ID.getPath())),
                        PackType.CLIENT_RESOURCES,
                        new PackSelectionConfig(false, Pack.Position.BOTTOM, false)
                );

                if (pack != null) {
                    consumer.accept(pack);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> AccessoryRenderers.registerLiving(OhmegaItems.getAngelRing(), HaloRenderer::new));
    }

    @SubscribeEvent
    private static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            AccessoryTypeManager.runConfigLoadTasks();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        IConfigSpec<ForgeConfigSpec> spec = event.getConfig().getSpec();

        if (spec == OhmegaConfigImpl.Client.getSpec()) {
            ClientCallbacks.onClientConfigReload();
        } else if (spec == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigReload(LOAD_FUNCTION);
        }
    }

    @SubscribeEvent
    public static void onConfigUnload(ModConfigEvent.Unloading event) {
        if (event.getConfig().getSpec() == OhmegaConfigImpl.Server.getSpec()) {
            ClientCallbacks.onServerConfigUnload(LOAD_FUNCTION);
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ClientCallbacks.onItemTooltip(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onKeybindRegistration(RegisterKeyMappingsEvent event) {
        event.register(OhmegaBinds.EDIT_MAGNETICS);
        event.register(OhmegaBinds.EDIT_NUDGE_DOWN);
        event.register(OhmegaBinds.EDIT_NUDGE_LEFT);
        event.register(OhmegaBinds.EDIT_NUDGE_RIGHT);
        event.register(OhmegaBinds.EDIT_NUDGE_UP);
        event.register(OhmegaBinds.EDIT_REDO);
        event.register(OhmegaBinds.EDIT_SHOW_LINES);
        event.register(OhmegaBinds.EDIT_UNDO);
        event.register(OhmegaBinds.OPEN_ACCESSORY_INVENTORY);
        event.register(OhmegaBinds.OPEN_EDIT_UI);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        ClientCallbacks.onKeyInput(event.getInfo());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientCallbacks.onJoinWorld(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onPostScreenInit(ScreenEvent.Init.Post event) {
        ClientCallbacks.onPostScreenInit(event.getScreen(), event::addListener);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        ClientCallbacks.onRegisterCommands(event.getDispatcher(), event.getBuildContext(), context -> {
            CommandSourceStack source = context.getSource();

            return new IClientCommandSource() {
                @Override
                public void sendSuccess(@NonNull Component message) {
                    source.sendSuccess(() -> message, false);
                }

                @Override
                public void sendError(@NonNull Component message) {
                    source.sendFailure(message);
                }

                @Override
                public @NonNull LocalPlayer getPlayer() {
                    return Minecraft.getInstance().player;
                }
            };
        });
    }
}
