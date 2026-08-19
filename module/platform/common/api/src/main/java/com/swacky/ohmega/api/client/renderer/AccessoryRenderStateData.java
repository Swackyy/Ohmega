package com.swacky.ohmega.api.client.renderer;

import com.swacky.ohmega.api.client.OhmegaClient;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

/**
 * Abstraction class for extra data attached to {@link LivingEntityRenderState}
 * @param entries the {@link AccessoryDataEntry} list containing all accessory {@link ItemStack}s and other related data,
 *                passed to the {@link IAccessoryRenderer}s
 */
public record AccessoryRenderStateData(ArrayList<AccessoryDataEntry> entries) {
    private static final Service IMPL = OhmegaClient.loadService(Service.class);

    public static void bootstrap() {}

    /**
     * Allows for retrieving the accessory data stored on the {@link LivingEntityRenderState},
     * implemented differently for each loader and as such is abstracted away
     * @param state the vanilla render state to pull data from
     * @return the stored {@link AccessoryRenderStateData} on the render state
     */
    public static AccessoryRenderStateData getData(LivingEntityRenderState state) {
        return IMPL.getData(state);
    }

    public interface Service {
        Identifier ID = Ohmega.id("accessory_data");

        AccessoryRenderStateData getData(LivingEntityRenderState state);
    }
}
