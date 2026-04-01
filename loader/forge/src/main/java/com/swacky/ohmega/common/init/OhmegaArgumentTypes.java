package com.swacky.ohmega.common.init;

import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.command.argument.AccessoryTypeArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class OhmegaArgumentTypes {
    public static void register(BusGroup group) {
        DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENTS = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, Ohmega.MODID);

        ARGUMENTS.register(AccessoryTypeArgument.KEY, () ->
                ArgumentTypeInfos.registerByClass(AccessoryTypeArgument.class, SingletonArgumentInfo.contextFree(AccessoryTypeArgument::new)));

        ARGUMENTS.register(group);
    }
}
