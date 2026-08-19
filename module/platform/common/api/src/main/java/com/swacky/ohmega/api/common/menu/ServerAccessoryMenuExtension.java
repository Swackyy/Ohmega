package com.swacky.ohmega.api.common.menu;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

/**
 * A dummy extension used purely on the server side to ensure behaviour is kept consistent across clients and the server
 */
public final class ServerAccessoryMenuExtension extends AccessoryMenuExtension {
    public ServerAccessoryMenuExtension(@NonNull AbstractContainerMenu menu, @NonNull Player owner) {
        super(menu, owner);
    }

    @Override
    public @NonNull AccessorySlot createSlot(@NonNull Player player, int index, int x, int y) {
        return new AccessorySlot(player, index, x, y);
    }

    @Override
    public void addSlots(@NonNull SlotAdder adder, @NonNull AccessoryData data) {}
}
