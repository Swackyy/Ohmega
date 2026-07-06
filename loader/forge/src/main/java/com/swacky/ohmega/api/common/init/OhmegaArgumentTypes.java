package com.swacky.ohmega.api.common.init;

import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypePredicateArgument;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class OhmegaArgumentTypes {
    public static void register(BusGroup group) {
        DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENTS = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, Ohmega.MODID);

        ARGUMENTS.register(AccessoryTypeArgument.KEY, () ->
                ArgumentTypeInfos.registerByClass(AccessoryTypeArgument.class, AccessoryTypeArgument.SERIALISER));
        ARGUMENTS.register(AccessoryTypePredicateArgument.KEY, () ->
                ArgumentTypeInfos.registerByClass(AccessoryTypePredicateArgument.class, AccessoryTypePredicateArgument.SERIALISER));

        ARGUMENTS.register(group);
    }
}
