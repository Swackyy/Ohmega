package com.swacky.ohmega.event;

import com.swacky.ohmega.api.AccessoryModifiers;
import com.swacky.ohmega.api.SoundData;
import com.swacky.ohmega.api.event.EquipContext;
import com.swacky.ohmega.common.Ohmega;
import com.swacky.ohmega.common.accessorytype.AccessoryType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public final class OhmegaHooks {
    private static final Service IMPL = Ohmega.loadService(Service.class);

    public static void bootstrap() {}

    public static void accessoryBind() {
        IMPL.accessoryBind();
    }

    public static void accessoryTickPost(Player player, ItemStack stack) {
        IMPL.accessoryTickPost(player, stack);
    }

    public static boolean accessoryTickPre(Player player, ItemStack stack) {
        return IMPL.accessoryTickPre(player, stack);
    }

    public static boolean allowWalkOnPowderSnow(ItemStack stack, boolean original) {
        return IMPL.allowWalkOnPowderSnow(stack, original);
    }

    public static AccessoryModifiers attributeModifiers(ItemStack stack, AccessoryModifiers.Builder builder) {
        return IMPL.attributeModifiers(stack, builder);
    }

    public static boolean autoSync(ItemStack stack, boolean original) {
        return IMPL.autoSync(stack, original);
    }

    public static boolean canEquip(Player player, ItemStack stack, EquipContext context, boolean original) {
        return IMPL.canEquip(player, stack, context, original);
    }

    public static boolean canUnequip(Player player, ItemStack stack, boolean original) {
        return IMPL.canUnequip(player, stack, original);
    }

    public static boolean compatibleWith(ItemStack stack, ItemStack other, boolean original) {
        return IMPL.compatibleWith(stack, other, original);
    }

    public static boolean equip(Player player, ItemStack stack, EquipContext context) {
        return IMPL.equip(player, stack, context);
    }

    public static SoundData equipSound(ItemStack stack, SoundData original) {
        return IMPL.equipSound(stack, original);
    }

    public static boolean isPiglinSafe(ItemStack stack, boolean original) {
        return IMPL.isPiglinSafe(stack, original);
    }

    public static boolean keybindUse(Player player, ItemStack stack) {
        return IMPL.keybindUse(player, stack);
    }

    public static double mobVisibility(ItemStack stack, Entity targetingEntity, double original) {
        return IMPL.mobVisibility(stack, targetingEntity, original);
    }

    public static Map<Item, Pair<AccessoryType, Boolean>> overrideTypes() {
        return IMPL.overrideTypes();
    }

    public static boolean preferVanillaUse(ItemStack stack, boolean original) {
        return IMPL.preferVanillaUse(stack, original);
    }

    public static Map<Identifier, AccessoryType> registerAccessoryTypes() {
        return IMPL.registerAccessoryTypes();
    }

    public static boolean unequip(Player player, ItemStack stack) {
        return IMPL.unequip(player, stack);
    }

    public interface Service {
        void accessoryBind();

        void accessoryTickPost(Player player, ItemStack stack);

        boolean accessoryTickPre(Player player, ItemStack stack);

        boolean allowWalkOnPowderSnow(ItemStack stack, boolean original);

        AccessoryModifiers attributeModifiers(ItemStack stack, AccessoryModifiers.Builder builder);

        boolean autoSync(ItemStack stack, boolean original);

        boolean canEquip(Player player, ItemStack stack, EquipContext context, boolean original);

        boolean canUnequip(Player player, ItemStack stack, boolean original);

        boolean compatibleWith(ItemStack stack, ItemStack other, boolean original);

        boolean equip(Player player, ItemStack stack, EquipContext context);

        SoundData equipSound(ItemStack stack, SoundData original);

        boolean isPiglinSafe(ItemStack stack, boolean original);

        boolean keybindUse(Player player, ItemStack stack);

        double mobVisibility(ItemStack stack, Entity targetingEntity, double original);

        Map<Item, Pair<AccessoryType, Boolean>> overrideTypes();

        boolean preferVanillaUse(ItemStack stack, boolean original);

        Map<Identifier, AccessoryType> registerAccessoryTypes();

        boolean unequip(Player player, ItemStack stack);
    }
}
