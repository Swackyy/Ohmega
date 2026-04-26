package com.swacky.ohmega.common.menu;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import com.swacky.ohmega.config.OhmegaConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

/**
 * Ohmega's default menu implementation of the accessory extension.
 */
// todo: implement fill direction capability
public final class DefaultMenuExtension extends AccessoryMenuExtension {
    public DefaultMenuExtension(AbstractContainerMenu menu, Player player) {
        super(menu, player);
    }

    @Override
    public void addSlotsClient(@NonNull SlotAdder adder) {
        ImmutableList<AccessoryType> slotTypes = AccessoryHelper.getSlotTypes();

        int renderSlots = Math.min(OhmegaConfig.Client.maxColumnSlots(), OhmegaConfig.Client.maxColumnRenderSlots());
        int renderColumns = (int) Math.min(Math.ceil((double) slotTypes.size() / renderSlots), OhmegaConfig.Client.maxColumns());
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
