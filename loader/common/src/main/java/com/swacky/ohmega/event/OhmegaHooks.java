package com.swacky.ohmega.event;

import com.google.common.collect.ImmutableMap;
import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class OhmegaHooks {
    private static final Service IMPL = OhmegaCommon.loadService(Service.class);

    public static ImmutableMap<Item, AccessoryType> accessoryOverrideTypesEvent() {
        return IMPL.accessoryOverrideTypesEvent();
    }

    public static boolean accessoryTickEventPre(Player player, ItemStack stack) {
        return IMPL.accessoryTickEventPre(player, stack);
    }

    public static void accessoryTickEventPost(Player player, ItemStack stack) {
        IMPL.accessoryTickEventPost(player, stack);
    }

    public static boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context) {
        return IMPL.accessoryEquipEvent(player, stack, context);
    }

    public static boolean accessoryUnequipEvent(Player player, ItemStack stack) {
        return IMPL.accessoryUnequipEvent(player, stack);
    }

    public static boolean accessoryCanEquipEvent(Player player, ItemStack stack, boolean initial) {
        return IMPL.accessoryCanEquipEvent(player, stack, initial);
    }

    public static boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean initial) {
        return IMPL.accessoryCanUnequipEvent(player, stack, initial);
    }

    public static boolean accessoryUseEvent(Player player, ItemStack stack) {
        return IMPL.accessoryUseEvent(player, stack);
    }

    public static void accessoryAttributeModifiersEvent(Item item, AccessoryModifiers.Builder builder) {
        IMPL.accessoryAttributeModifiersEvent(item, builder);
    }

    public interface Service {
        ImmutableMap<Item, AccessoryType> accessoryOverrideTypesEvent();

        boolean accessoryTickEventPre(Player player, ItemStack stack);

        void accessoryTickEventPost(Player player, ItemStack stack);

        boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context);

        boolean accessoryUnequipEvent(Player player, ItemStack stack);

        boolean accessoryCanEquipEvent(Player player, ItemStack stack, boolean initial);

        boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean initial);

        boolean accessoryUseEvent(Player player, ItemStack stack);

        void accessoryAttributeModifiersEvent(Item item, AccessoryModifiers.Builder builder);
    }
}
