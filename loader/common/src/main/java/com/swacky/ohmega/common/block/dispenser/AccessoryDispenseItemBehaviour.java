package com.swacky.ohmega.common.block.dispenser;

import com.swacky.ohmega.api.common.dataattachment.AccessoryDataEntry;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class AccessoryDispenseItemBehaviour extends DefaultDispenseItemBehavior {
    private static final AccessoryDispenseItemBehaviour INSTANCE = new AccessoryDispenseItemBehaviour();

    private AccessoryDispenseItemBehaviour() {}

    public static AccessoryDispenseItemBehaviour getInstance() {
        return INSTANCE;
    }

    @Override
    protected @NonNull ItemStack execute(BlockSource source, @NonNull ItemStack stack) {
        List<LivingEntity> entities = source.level().getEntitiesOfClass(
                LivingEntity.class,
                new AABB(source.pos().relative(source.state().getValue(DispenserBlock.FACING))),
                entity -> entity.isAlive() && !entity.isSpectator());

        if (!entities.isEmpty()) {
            for (LivingEntity entity : entities) {
                int index = AccessoryHelper.getFirstOpenSlot(entity, Accessories.getType(entity, stack.getItem()));

                if (index >= 0) {
                    AccessoryDataEntry entry = OhmegaDataAttachments.getData(entity).getEntry(index);

                    if (entry.isItemValid(entity, stack, EquipContext.DISPENSE)) {
                        entry.setStack(entity, stack.split(1), index, EquipContext.DISPENSE);
                        return stack;
                    }
                }
            }
        }

        return super.execute(source, stack);
    }
}
