package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.client.screen.AccessoryInventoryScreen;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaBinds;
import com.swacky.ohmega.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.common.init.OhmegaMenus;
import com.swacky.ohmega.common.inv.AccessoryInventoryMenu;
import com.swacky.ohmega.config.OhmegaConfig;
import com.swacky.ohmega.network.C2S.OpenAccessoryInventoryPacket;
import com.swacky.ohmega.network.C2S.ResizeCapPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = OhmegaCommon.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerKbs(RegisterKeyMappingsEvent event) {
        event.register(OhmegaBinds.OPEN_ACC_INV);
    }

    @SuppressWarnings("RedundantCast")
    @SubscribeEvent
    public static void registerMenus(RegisterMenuScreensEvent event) {
        event.register(OhmegaMenus.ACCESSORY_INVENTORY.get(), (MenuScreens.ScreenConstructor<AccessoryInventoryMenu, AccessoryInventoryScreen>) AccessoryInventoryScreen::new);
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == OhmegaConfig.SPEC_SERVER) {
            ArrayList<KeyMapping> list = new ArrayList<>();
            for (ImmutableList<KeyMapping> immutableList : OhmegaBinds.Generated.getSlotKeys().values()) {
                list.addAll(immutableList);
            }

            Minecraft mc = Minecraft.getInstance();
            mc.options.keyMappings = ArrayUtils.addAll(Arrays.stream(mc.options.keyMappings).filter(v -> !(v instanceof OhmegaBinds.KeyMappingProxy)).toList().toArray(new KeyMapping[0]), list.toArray(new KeyMapping[0]));
            mc.options.load(true);
        }
    }

    @SubscribeEvent
    public static void onConfigUnload(ModConfigEvent.Unloading event) {
        if (event.getConfig().getSpec() == OhmegaConfig.SPEC_SERVER) {
            Minecraft mc = Minecraft.getInstance();
            mc.options.keyMappings = Arrays.stream(mc.options.keyMappings).filter(v -> !(v instanceof OhmegaBinds.KeyMappingProxy)).toList().toArray(new KeyMapping[0]);
            mc.options.load(true);
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (OhmegaConfig.SPEC_SERVER.isLoaded()) {
            Minecraft mc = Minecraft.getInstance();
            if (event.getConfig().getSpec() == OhmegaConfig.SPEC_CLIENT && !OhmegaConfig.CONFIG_CLIENT.compatibilityMode.get()) {
                if (mc.player != null && mc.player.containerMenu instanceof AccessoryInventoryMenu) {
                    PacketDistributor.sendToServer(new OpenAccessoryInventoryPacket());
                }
            } else if (event.getConfig().getSpec() == OhmegaConfig.SPEC_SERVER) {
                ArrayList<KeyMapping> list = new ArrayList<>();
                for (ImmutableList<KeyMapping> immutableList : OhmegaBinds.Generated.getSlotKeys().values()) {
                    list.addAll(immutableList);
                }

                mc.options.keyMappings = ArrayUtils.addAll(Arrays.stream(mc.options.keyMappings).filter(v -> !(v instanceof OhmegaBinds.KeyMappingProxy)).toList().toArray(new KeyMapping[0]), list.toArray(new KeyMapping[0]));
                mc.options.load(true);

                if (mc.player != null) {
                    mc.player.getData(OhmegaDataAttachments.ACCESSORY_HANDLER.get()).reloadCfg();
                    PacketDistributor.sendToServer(new ResizeCapPacket());

                    if (!OhmegaConfig.CONFIG_CLIENT.compatibilityMode.get() && mc.player.containerMenu instanceof AccessoryInventoryMenu) {
                        mc.screen = null;
                        mc.player.connection.send(new ServerboundContainerClosePacket(mc.player.containerMenu.containerId));
                        PacketDistributor.sendToServer(new OpenAccessoryInventoryPacket());
                    }
                }
            }
        }
    }
}
