package com.swacky.ohmega.compat.polymer.common;

import com.swacky.ohmega.api.IOhmegaEntrypoint;
import com.swacky.ohmega.api.OhmegaEntrypoint;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.command.OhmegaCommandNodes;
import com.swacky.ohmega.api.common.init.OhmegaDataComponents;
import com.swacky.ohmega.api.util.LogicalSide;
import com.swacky.ohmega.compat.polymer.common.command.node.OpenCommand;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import it.unimi.dsi.fastutil.Function;
import org.jspecify.annotations.NonNull;

@OhmegaEntrypoint(LogicalSide.COMMON)
public class OhmegaPolymerMain implements IOhmegaEntrypoint {
    @Override
    public void invoke(@NonNull Function<String, Boolean> modLoaded) {
        if (modLoaded.apply("polymer")) {
            PolymerResourcePackUtils.addModAssets(Ohmega.MODID);
            PolymerComponent.registerDataComponent(
                    OhmegaDataComponents.getAccessoryActiveModifiers(),
                    OhmegaDataComponents.getActive(),
                    OhmegaDataComponents.getSlotIndex(),
                    OhmegaDataComponents.getSlotActiveModifiers());

            // Commands
            OhmegaCommandNodes.register(null, OpenCommand::new);
            OhmegaCommandNodes.register(OpenCommand.ELEMENT_ROOT, OpenCommand::new);
        }
    }
}
