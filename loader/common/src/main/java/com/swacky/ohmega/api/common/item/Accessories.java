package com.swacky.ohmega.api.common.item;

import com.google.common.collect.ImmutableList;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import com.swacky.ohmega.api.common.init.OhmegaDataAttachments;
import com.swacky.ohmega.api.common.init.OhmegaTags;
import com.swacky.ohmega.config.OhmegaConfig;
import it.unimi.dsi.fastutil.booleans.BooleanObjectPair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds data related to accessory items
 */
public final class Accessories {
    private static final @NonNull Map<Item, Accessory> BOUND_ACCESSORIES = new IdentityHashMap<>();
    private static final @NonNull Map<Item, List<AccessoryType>> BOUND_TYPES = new IdentityHashMap<>();
    private static final @NonNull List<Item> TO_TYPE_QUERY = new ArrayList<>();

    /**
     * Retrieves a list of the accessory's possible effective {@link AccessoryType},
     * with values in ascending order of priority index
     * @param item the item to find the effective {@link AccessoryType} of
     * @return the {@link AccessoryType} of lowest priority index bound to the given accessory, or,
     * if no type can be found (including such a case where the item is not an accessory), then {@link AccessoryType#NONE}
     * @apiNote Only used internally as this method is more expensive than querying the cached value
     * generated from this with {@link #getType(LivingEntity, Item)}
     */
    @SuppressWarnings("deprecation")
    private static @NonNull List<AccessoryType> getEffectiveTypes(@NonNull Item item) {
        List<AccessoryType> list = new ArrayList<>();

        for (Map.Entry<AccessoryType, TagKey<Item>> entry : OhmegaTags.getTags().entrySet()) {
            AccessoryType candidate = entry.getKey();

            if (candidate.allowReference() && item.builtInRegistryHolder().is(entry.getValue())) {
                int index = 0;

                while (index < list.size() && list.get(index).getPriority() <= candidate.getPriority()) {
                    index++;
                }

                list.add(index, candidate);
            }
        }

        BooleanObjectPair<AccessoryType> override = AccessoryTypeManager.getTypeOverride(item);

        if (override != null && (override.leftBoolean() || list.isEmpty())) {
            list.addFirst(override.right());
        }

        return list;
    }

    /**
     * @param item the item to get the binding of
     * @return the {@link Accessory} binding
     */
    public static @Nullable Accessory get(@NonNull Item item) {
        Accessory candidate = BOUND_ACCESSORIES.get(item);

        if (candidate != null) {
            return candidate;
        }

        if (item instanceof IAccessory binding) {
            Accessory accessory = new Accessory(binding);

            BOUND_ACCESSORIES.put(item, accessory);
            BOUND_TYPES.put(item, getEffectiveTypes(item));
            return accessory;
        }

        return null;
    }

    /**
     * Check if an item is registered as an accessory, either by implementing {@link Accessory} in your {@link Item} class
     * or by calling {@link #bind(Item, IAccessory)},
     * @param item the item to check if it is bound
     * @return {@code true} if the {@link Item} class implements {@link Accessory} or is accessory bound by code ({@link #bind(Item, IAccessory)}
     */
    public static boolean isBound(@NonNull Item item) {
        return BOUND_ACCESSORIES.containsKey(item) || item instanceof IAccessory;
    }

    /**
     * Use this to bind an {@link Accessory} instance to an {@link Item}, essentially turning it into an accessory.
     * If you have read access on the type of item (if the item class is yours), instead implement the {@link Accessory} interface
     * @param item item to bind
     * @param binding the {@link Accessory} instance to store on the item, determining most accessory behaviour
     * @return {@code true} if successfully bound, {@code false} otherwise for any given reason (subject to change)
     */
    public static boolean bind(@NonNull Item item, @NonNull IAccessory binding) {
        if (item instanceof AirItem) {
            return false;
        }

        BOUND_ACCESSORIES.put(item, new Accessory(binding));

        if (OhmegaConfig.Server.isLoaded()) {
            BOUND_TYPES.put(item, getEffectiveTypes(item));
        } else {
            TO_TYPE_QUERY.add(item);
        }

        return true;
    }

    /**
     * Used internally to cache the effective {@link AccessoryType}s of every
     */
    public static void surveyRegistry() {
        BOUND_TYPES.clear();

        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof IAccessory accessory) {
                bind(item, accessory);
            }
        }

        for (Item item : TO_TYPE_QUERY) {
            BOUND_TYPES.put(item, getEffectiveTypes(item));
        }
    }

    /**
     * Retrieves the accessory's effective {@link AccessoryType}
     * @param item the item to find the effective {@link AccessoryType} of
     * @return the {@link AccessoryType} of lowest priority index bound to the given accessory, or,
     * if no type can be found (including such a case where the item is not an accessory), then {@link AccessoryType#NONE}
     * @apiNote If you are checking equality to another type that may <strong>not</strong> be obtained with this method,
     * use {@link AccessoryType#equals(Object)} instead of the equality operator ({@code ==}),
     * as instances are not guaranteed to be the same reference on an integrated server
     */
    public static @NonNull AccessoryType getType(@Nullable LivingEntity entity, @NonNull Item item) {
        if (OhmegaConfig.Server.getData().disableAccessoryTypes().get()) {
            return AccessoryType.GENERIC.get();
        }

        List<AccessoryType> candidates = BOUND_TYPES.get(item);

        if (candidates != null) {
            ImmutableList<AccessoryType> types = OhmegaDataAttachments.getData(entity).getTypes();
            int size = candidates.size();

            for (int i = 0; i < size; i++) {
                AccessoryType candidate = candidates.get(i);

                if (types.contains(candidate) && (i == 0 || candidate.allowFallback())) {
                    return candidate;
                }
            }

            if (!candidates.isEmpty()) {
                return candidates.getFirst();
            }
        }

        return AccessoryType.NONE;
    }

    /**
     * Checks the surveyed data for if a given {@link AccessoryType} actually has any accessory items bound with it
     * @param type the {@link AccessoryType} to check the usage of
     * @return {@code true} if the type is used at all, does not include cascading and as such only views the immediate bound type, {@code false} otherwise
     */
    public static boolean isTypeUsed(@NonNull AccessoryType type) {
        for (List<AccessoryType> list : BOUND_TYPES.values()) {
            if (list.contains(type)) {
                return true;
            }
        }

        return false;
    }
}
