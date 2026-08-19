package com.swacky.ohmega.api.common.event;

import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.IAccessory;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;

/**
 * Even though accessory binding can be done at any point through {@link Accessories#bind(Item, IAccessory)},
 * an event is provided here which is guaranteed to be fired very late in initialisation, including after registration.
 * <p>
 * Specifically, this event is fired at the tail of:
 * <ul>
 *     <li>{@link Minecraft#Minecraft(GameConfig)}</li>
 *     <li>{@link MinecraftServer#runServer()}</li>
 * </ul>
 */
public interface AccessoryBindEvent {
    Event<AccessoryBindEvent> EVENT = EventFactory.createArrayBacked(AccessoryBindEvent.class,
        listeners -> () -> {
            for (AccessoryBindEvent listener : listeners) {
                listener.process();
            }
        }
    );

    void process();
}
