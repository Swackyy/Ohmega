package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public final class OhmegaHooks {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static void accessoryAttributeModifiersEvent(ItemStack stack, AccessoryModifiers.Builder builder) {
        IMPL.accessoryAttributeModifiersEvent(stack, builder);
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

    public static Map<Item, AccessoryType> accessoryOverrideTypesEvent() {
        return IMPL.accessoryOverrideTypesEvent();
    }

    public static void accessoryTickEventPost(Player player, ItemStack stack) {
        IMPL.accessoryTickEventPost(player, stack);
    }

    public static boolean accessoryTickEventPre(Player player, ItemStack stack) {
        return IMPL.accessoryTickEventPre(player, stack);
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

        boolean accessoryCanEquipEvent(Player player, ItemStack stack, EquipContext context, boolean initial);

        boolean accessoryCanUnequipEvent(Player player, ItemStack stack, boolean initial);

        boolean accessoryEquipEvent(Player player, ItemStack stack, EquipContext context);

        Map<Item, AccessoryType> accessoryOverrideTypesEvent();

        void accessoryTickEventPost(Player player, ItemStack stack);

        boolean accessoryTickEventPre(Player player, ItemStack stack);

        boolean accessoryUnequipEvent(Player player, ItemStack stack);

        boolean accessoryUseEvent(Player player, ItemStack stack);

        Map<Identifier, AccessoryType> registerAccessoryTypesEvent();
    }
}
