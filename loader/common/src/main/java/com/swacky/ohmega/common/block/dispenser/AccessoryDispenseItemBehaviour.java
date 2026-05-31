package com.swacky.ohmega.common.block.dispenser;

import com.swacky.ohmega.api.common.item.AccessoryHelper;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AccessoryDispenseItemBehaviour extends DefaultDispenseItemBehavior {
    private static final AccessoryDispenseItemBehaviour INSTANCE = new AccessoryDispenseItemBehaviour();

    private AccessoryDispenseItemBehaviour() {}

    public static AccessoryDispenseItemBehaviour getInstance() {
        return INSTANCE;
    }

    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        List<LivingEntity> entities = source.level().getEntitiesOfClass(
                LivingEntity.class,
                new AABB(source.pos().relative(source.state().getValue(DispenserBlock.FACING))),
                entity -> entity.isAlive() && !entity.isSpectator());

        if (!entities.isEmpty()) {
            for (LivingEntity entity : entities) {
                int slot = AccessoryHelper.getFirstOpenSlot(entity, AccessoryHelper.getType(stack.getItem()));

                if (slot >= 0) {
                    AccessoryData data = AccessoryHelper.getData(entity);

                    if (data.isItemValid(entity, slot, stack, EquipContext.DISPENSE)) {
                        data.setStack(
                                entity,
                                slot,
                                stack.split(1),
                                EquipContext.DISPENSE);
                        return stack;
                    }
                }
            }
        }

        return super.execute(source, stack);
    }
}
