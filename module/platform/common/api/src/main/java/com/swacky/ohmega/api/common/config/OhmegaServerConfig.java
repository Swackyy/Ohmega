package com.swacky.ohmega.api.common.config;

import com.google.common.collect.ImmutableSet;
import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.item.Accessories;
import com.swacky.ohmega.api.common.item.EquipContext;
import com.swacky.ohmega.api.util.BooleanLazySavedValue;
import com.swacky.ohmega.api.util.LazySavedValue;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class OhmegaServerConfig {
    private static final @NonNull Service IMPL = Ohmega.loadService(Service.class);

    private static @NonNull List<AccessoryType> defaultSlotTypes = List.of();
    private static @NonNull ImmutableSet<AccessoryType> keyboundSlotTypes = ImmutableSet.of();

    public static void bootstrap() {}

    public static @NonNull Data getData() {
        return IMPL.getData();
    }

    public static boolean isLoaded() {
        return IMPL.isLoaded();
    }

    public static void revalidateCached() {
        Data data = getData();
        List<? extends String> slotTypes = data.defaultSlotTypes().getObject();
        List<AccessoryType> list;

        if (slotTypes != null) {
            int size = slotTypes.size();
            list = new ArrayList<>(size);

            if (data.disableAccessoryTypes().get()) {
                for (int i = 0; i < size; i++) {
                    list.add(AccessoryType.GENERIC.get());
                }
            } else {
                for (String id : slotTypes) {
                    AccessoryType type = AccessoryTypeManager.get(Identifier.parse(id));

                    if (type != AccessoryType.NONE && (!data.shrinkDefaultSlotTypes().get() || Accessories.isTypeUsed(type))) {
                        list.add(type);
                    }
                }
            }
        } else {
            list = List.of();
        }

        if (!list.equals(defaultSlotTypes)) {
            defaultSlotTypes = list;

            for (LivingEntity tracker : AccessoryData.DEFAULT_TRACKERS) {
                OhmegaDataAttachments.getData(tracker).defaultSlots(tracker, EquipContext.CONFIG);
            }
        }

        List<? extends String> types = data.keyboundSlotTypes().getObject();

        if (types != null) {
            ImmutableSet.Builder<AccessoryType> builder = new ImmutableSet.Builder<>();

            for (String id : types) {
                builder.add(AccessoryTypeManager.get(Identifier.parse(id)));
            }

            keyboundSlotTypes = builder.build();
        } else {
            keyboundSlotTypes = ImmutableSet.of();
        }
    }

    public static @NonNull List<AccessoryType> getDefaultSlotTypes() {
        return defaultSlotTypes;
    }

    public static @NonNull ImmutableSet<AccessoryType> getKeyboundSlotTypes() {
        return keyboundSlotTypes;
    }

    public record Data(
            @NonNull LazySavedValue<@NonNull List<? extends String>> defaultSlotTypes,
            @NonNull BooleanLazySavedValue shrinkDefaultSlotTypes,
            @NonNull LazySavedValue<@NonNull List<? extends String>> keyboundSlotTypes,
            @NonNull LazySavedValue<@NonNull KeepAccessoriesBehaviour> keepAccessoriesBehaviour,
            @NonNull BooleanLazySavedValue disableAccessoryTypes,
            @NonNull BooleanLazySavedValue allowHideAccessories,
            @NonNull BooleanLazySavedValue injectVanillaClear) {

        public void pull() {
            defaultSlotTypes.pull();
            shrinkDefaultSlotTypes.pull();
            keyboundSlotTypes.pull();
            keepAccessoriesBehaviour.pull();
            disableAccessoryTypes.pull();
            allowHideAccessories.pull();
            injectVanillaClear.pull();
            revalidateCached();
        }
    }

    public interface Service {
        @NonNull String GENERIC = AccessoryType.GENERIC_ID.toString();
        @NonNull String NORMAL = AccessoryType.NORMAL_ID.toString();
        @NonNull String UTILITY = AccessoryType.UTILITY_ID.toString();
        @NonNull String SPECIAL = AccessoryType.SPECIAL_ID.toString();
        @NonNull Predicate<Object> ACCESSORY_TYPE_VALIDATOR = object -> {
            if (AccessoryTypeManager.isEmpty()) {
                return true;
            }

            if (object instanceof String string) {
                return string.equals(AccessoryType.NONE_ID.toString()) || AccessoryTypeManager.exists(Identifier.tryParse(string));
            }

            return false;
        };
        // - - -

        @NonNull String DEFAULT_SLOT_TYPES_KEY = "defaultSlotTypes";
        @NonNull String DEFAULT_SLOT_TYPES_DESCRIPTION = """
                Defines the types and number of slots to default to for the accessory inventory""";
        @NonNull List<String> DEFAULT_SLOT_TYPES_DEFAULT = List.of(
                NORMAL,
                NORMAL,
                NORMAL,
                UTILITY,
                UTILITY,
                SPECIAL);
        @NonNull String DEFAULT_SLOT_TYPES_NEW_VALUE_DEFAULT = NORMAL;
        // - - -
        @NonNull String SHRINK_DEFAULT_SLOT_TYPES_KEY = "shrinkDefaultSlotTypes";
        @NonNull String SHRINK_DEFAULT_SLOT_TYPES_DESCRIPTION = """
                If true, will automatically shrink the default slot types based on registered items' types.
                This means that if an accessory type exists but no items are tagged with it, all instances of the type will be removed from the default slot list""";
        boolean SHRINK_DEFAULT_SLOT_TYPES_DEFAULT = false;
        // - - -
        @NonNull String KEYBOUND_SLOT_TYPES_KEY = "keyboundSlotTypes";
        @NonNull String KEYBOUND_SLOT_TYPES_DESCRIPTION = """
                Defines the types of accessories that can be key-bound""";
        @NonNull List<String> KEYBOUND_SLOT_TYPES_DEFAULT = List.of(
                GENERIC,
                UTILITY,
                SPECIAL);
        @NonNull String KEYBOUND_SLOT_TYPES_NEW_VALUE_DEFAULT = "";
        // - - -
        @NonNull String KEEP_ACCESSORIES_BEHAVIOUR_KEY = "keepAccessoriesBehaviour";
        @NonNull String KEEP_ACCESSORIES_BEHAVIOUR_DESCRIPTION = """
                Defines how to handle player death in terms of dropping accessories
                DEFAULT: Uses the vanilla 'keepInventory' game-rule
                ALWAYS_ON: Will never drop accessories on death
                ALWAYS_OFF: Will always drop accessories on death""";
        // - - -
        @NonNull String DISABLE_ACCESSORY_TYPES_KEY = "disableAccessoryTypes";
        @NonNull String DISABLE_ACCESSORY_TYPES_DESCRIPTION = """
                If true, effectively no accessory types will be used, and they will all be overridden, changing them all to 'ohmega:generic'""";
        boolean DISABLE_ACCESSORY_TYPES_DEFAULT = false;
        // - - -
        @NonNull String ALLOW_HIDE_ACCESSORIES_KEY = "allowHideAccessories";
        @NonNull String ALLOW_HIDE_ACCESSORIES_DESCRIPTION = """
                Will prevent players from toggling visibility on their accessories if false, so that they always render""";
        boolean ALLOW_HIDE_ACCESSORIES_DEFAULT = true;
        // - - -
        @NonNull String INJECT_VANILLA_CLEAR_KEY = "injectVanillaClear";
        @NonNull String INJECT_VANILLA_CLEAR_DESCRIPTION = """
                Injects accessory clear operations into vanilla inventory clearing code""";
        boolean INJECT_VANILLA_CLEAR_DEFAULT = false;
        // - - -

        @NonNull Data getData();

        boolean isLoaded();
    }
}