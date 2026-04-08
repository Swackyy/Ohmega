package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public final class OhmegaHooks {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static void accessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) {
        IMPL.accessoryAttributeModifiersEvent(stack, builder);
    }

    public static void accessoryBindEvent() {
        IMPL.accessoryBindEvent();
    }

    public static boolean accessoryCanEquipEvent(Player player, ItemStack stack, EquipContext context, boolean initial) {
        return IMPL.accessoryCanEquipEvent(player, stack, context, initial);
    }

    public static boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean initial) {
        return IMPL.accessoryCanUnequipEvent(player, stack, initial);
    }

    public static boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context) {
        return IMPL.accessoryEquipEvent(player, stack, context);
    }

    public static Map<Item, Pair<AccessoryType, Boolean>> accessoryOverrideTypesEvent() {
        return IMPL.accessoryOverrideTypesEvent();
    }

    public static void accessoryTickPostEvent(Player player, ItemStack stack) {
        IMPL.accessoryTickPostEvent(player, stack);
    }

    public static boolean accessoryTickPreEvent(Player player, ItemStack stack) {
        return IMPL.accessoryTickPreEvent(player, stack);
    }

    public static boolean accessoryUnequipEvent(Player player, ItemStack stack) {
        return IMPL.accessoryUnequipEvent(player, stack);
    }

    public static boolean accessoryUseEvent(Player player, ItemStack stack) {
        return IMPL.accessoryUseEvent(player, stack);
    }

    public static Map<Identifier, AccessoryType> registerAccessoryTypesEvent() {
        return IMPL.registerAccessoryTypesEvent();
    }

    public interface Service {
        void accessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder);

        void accessoryBindEvent();

        boolean accessoryCanEquipEvent(Player player, ItemStack stack, EquipContext context, boolean initial);

        boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean initial);

        boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context);

        Map<Item, Pair<AccessoryType, Boolean>> accessoryOverrideTypesEvent();

        void accessoryTickPostEvent(Player player, ItemStack stack);

        boolean accessoryTickPreEvent(Player player, ItemStack stack);

        boolean accessoryUnequipEvent(Player player, ItemStack stack);

        boolean accessoryUseEvent(Player player, ItemStack stack);

        Map<Identifier, AccessoryType> registerAccessoryTypesEvent();
    }
}
