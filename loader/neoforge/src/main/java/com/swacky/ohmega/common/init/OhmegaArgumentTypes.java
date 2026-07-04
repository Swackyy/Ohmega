package com.swacky.ohmega.common.init;

import com.swacky.ohmega.api.common.command.argument.AccessoryTypeArgument;
import com.swacky.ohmega.api.common.command.argument.AccessoryTypePredicateArgument;
import com.swacky.ohmega.common.Ohmega;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OhmegaArgumentTypes {
    public static void register(IEventBus bus) {
        DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENTS = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Ohmega.MODID);

        ARGUMENTS.register(AccessoryTypeArgument.KEY, () ->
                ArgumentTypeInfos.registerByClass(AccessoryTypeArgument.class, AccessoryTypeArgument.SERIALISER));
        ARGUMENTS.register(AccessoryTypePredicateArgument.KEY, () ->
                ArgumentTypeInfos.registerByClass(AccessoryTypePredicateArgument.class, AccessoryTypePredicateArgument.SERIALISER));

        ARGUMENTS.register(bus);
    }
}
