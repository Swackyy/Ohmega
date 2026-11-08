package com.swacky.ohmega.common.command.node;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.swacky.ohmega.api.common.command.CommandUtils;
import com.swacky.ohmega.api.common.command.ICommonCommandSource;
import com.swacky.ohmega.api.common.command.node.ICommandNode;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class DebugCommandBase implements ICommandNode {
    public static final String ELEMENT_ROOT = "debug";
    public static final String ELEMENT_DATA = "data";

    protected static final String ARGUMENT_INDEX = "index";

    public static final String DATA_FEEDBACK = CommandUtils.command(ELEMENT_ROOT).add(ELEMENT_DATA).feedback();
    public static final String DATA_FEEDBACK_INDEX = CommandUtils.command(ELEMENT_ROOT).add(ELEMENT_DATA).feedback("index");

    protected static int doData(ICommonCommandSource.SuccessReference successReference, Entity entity, int index, boolean broadcast) throws
            CommandSyntaxException {
        LivingEntity target = CommandUtils.convertLiving(entity);

        if (index < 0) {
            AccessoryData.CODEC.encodeStart(NbtOps.INSTANCE, OhmegaDataAttachments.getData(target)).resultOrPartial().ifPresent(tag ->
                    successReference.send(() -> Component.translatable(DATA_FEEDBACK,
                            target.getDisplayName(),
                            NbtUtils.toPrettyComponent(tag)
                    ), broadcast));
        } else {
            AccessoryDataEntry.CODEC.encodeStart(NbtOps.INSTANCE, OhmegaDataAttachments.getData(target).getEntry(index)).resultOrPartial().ifPresent(tag ->
                    successReference.send(() -> Component.translatable(DATA_FEEDBACK_INDEX,
                            target.getDisplayName(),
                            index,
                            NbtUtils.toPrettyComponent(tag)
                    ), broadcast));
        }

        return Command.SINGLE_SUCCESS;
    }
}
