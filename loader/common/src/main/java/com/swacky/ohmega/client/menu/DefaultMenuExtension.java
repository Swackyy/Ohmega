package com.swacky.ohmega.client.menu;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

/**
 * Ohmega's default accessory menu extension implementation
 */
// todo: implement fill direction capability
public final class DefaultMenuExtension extends AccessoryMenuExtension {
    public DefaultMenuExtension(AbstractContainerMenu menu, Player player) {
        super(menu, player);
    }

    @Override
    public void addSlots(@NonNull SlotAdder adder) {
        ImmutableList<AccessoryType> slotTypes = AccessoryHelper.getSlotTypes();

        OhmegaConfig.Client.Service.Data data = OhmegaConfig.Client.getData();
        int renderSlots = Math.min(data.maxColumnSlots().get(), data.maxColumnRenderSlots().get());
        int renderColumns = (int) Math.min(Math.ceil((double) slotTypes.size() / renderSlots), data.maxColumns().get());
        int slotsAvailable = Math.min(renderColumns * renderSlots, slotTypes.size());

        boolean stop = false;
        int index = 0;

        for (int i = 0; i < renderColumns; i++) {
            if (stop) {
                break;
            }

            int slotsCreatedCurrentColumn = 0;

            for (int j = 0; true; j++) {
                adder.add(index++, 5 + 18 * i, 5 + j * 18);

                if (++slotsCreatedCurrentColumn >= renderSlots) {
                    break;
                }

                if (index >= slotsAvailable) {
                    stop = true;
                    break;
                }
            }
        }
    }
}
