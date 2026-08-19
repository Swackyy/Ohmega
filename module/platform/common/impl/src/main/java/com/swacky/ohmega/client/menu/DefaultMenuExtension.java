package com.swacky.ohmega.client.menu;

import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.menu.AccessoryMenuExtension;
import com.swacky.ohmega.api.config.OhmegaConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

// todo: implement fill direction capability
public final class DefaultMenuExtension extends AccessoryMenuExtension {
    public DefaultMenuExtension(@NonNull AbstractContainerMenu menu, @NonNull Player owner) {
        super(menu, owner);
    }

    @Override
    public void addSlots(@NonNull SlotAdder adder, @NonNull AccessoryData data) {
        int size = data.size();

        OhmegaConfig.Client.Service.Data configData = OhmegaConfig.Client.getData();
        int renderSlots = Math.min(configData.maxColumnSlots().get(), configData.maxColumnRenderSlots().get());
        int renderColumns = (int) Math.min(Math.ceil((double) size / renderSlots), configData.maxColumns().get());
        int slotsAvailable = Math.min(renderColumns * renderSlots, size);

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
