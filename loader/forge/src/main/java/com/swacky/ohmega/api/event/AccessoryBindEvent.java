package com.swacky.ohmega.api.event;

import com.swacky.ohmega.api.common.item.IAccessory;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Even though accessory binding can be done at any point through {@link Ohmega#bindAccessory(Item, IAccessory)},
 * an event is provided here which is guaranteed to be fired very late in initialisation, including after registration.
 * <p>
 * Specifically, this event is fired at the tail of:
 * <ul>
 *     <li>{@link Minecraft#Minecraft(GameConfig)}</li>
 *     <li>{@link MinecraftServer#runServer()}</li>
 * </ul>
 */
public class AccessoryBindEvent implements IModBusEvent {}
