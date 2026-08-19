package com.swacky.ohmega.api.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Holds data pertaining to accessory renderers (see {@link IAccessoryRenderer})
 */
public final class AccessoryRenderers {
    private static final @NonNull Map<Item, IHumanoidAccessoryRenderer.Factory> HUMANOID_RENDERERS = new IdentityHashMap<>();
    private static final @NonNull Map<Item, ILivingAccessoryRenderer.Factory> LIVING_RENDERERS = new IdentityHashMap<>();
    private static final @NonNull Map<Item, Predicate<EntityType<?>>> NO_RENDERS = new IdentityHashMap<>();

    /**
     * Registers an {@link IAccessoryRenderer} for entities with {@link HumanoidModel}s
     * @param item the accessory item to bind the renderer to
     * @param factory renderer creation factory to apply later, usually a constructor reference
     */
    public static void registerHumanoid(@NonNull Item item, IHumanoidAccessoryRenderer.@NonNull Factory factory) {
        HUMANOID_RENDERERS.put(item, factory);
    }

    /**
     * Registers an {@link IAccessoryRenderer} for any {@link LivingEntity}, however this can of course be specially selected
     * further with the rendering code itself
     * @param item the accessory item to bind the renderer to
     * @param factory renderer creation factory to apply later, usually a constructor reference
     * @apiNote If a humanoid renderer of the same {@link Item} key has been registered, it will be removed and this will take priority
     */
    public static void registerLiving(@NonNull Item item, ILivingAccessoryRenderer.@NonNull Factory factory) {
        HUMANOID_RENDERERS.remove(item);
        LIVING_RENDERERS.put(item, factory);
    }

    /**
     * Registers an item to be render preventing, meaning that when it is equipped, the equipper's {@link EntityModel} will not be rendered
     * @param item the accessory item to bind the renderer to
     * @param filter a way of filtering out certain entity types, by only returning {@code true}
     *               if an entity with that given {@link EntityType} will be affected and its rendering prevented
     * @return {@code true} if the prevention succeeded, {@code false} otherwise,
     * indicating a no-render with the same {@link Item} key has already been registered
     */
    public static boolean registerNoRender(@NonNull Item item, @NonNull Predicate<EntityType<?>> filter) {
        if (!NO_RENDERS.containsKey(item)) {
            NO_RENDERS.put(item, filter);
            return true;
        }

        return false;
    }

    /**
     * Registers an item to be render preventing, meaning that when it is equipped, the equipper's {@link EntityModel} will not be rendered.
     * Does not take in any filter as it will apply to every {@link EntityType}
     * @param item the accessory item to bind the renderer to
     * @return {@code true} if the prevention succeeded, {@code false} otherwise,
     * indicating a no-render with the same {@link Item} key has already been registered
     */
    public static boolean registerNoRender(@NonNull Item item) {
        return registerNoRender(item, _ -> true);
    }

    /**
     * Retrieve the {@link IHumanoidAccessoryRenderer.Factory} registered with the given {@link Item} key
     * @param key {@link Item} to obtain the humanoid renderer factory for
     * @return the {@link IHumanoidAccessoryRenderer.Factory} bound to the provided key, or {@code null} if there is none bound
     */
    public static IHumanoidAccessoryRenderer.@Nullable Factory getHumanoidFactory(@NonNull Item key) {
        return HUMANOID_RENDERERS.get(key);
    }

    /**
     * Retrieve the {@link ILivingAccessoryRenderer.Factory} registered with the given {@link Item} key
     * @param key {@link Item} to obtain the living entity renderer factory for
     * @return the {@link ILivingAccessoryRenderer.Factory} bound to the provided key, or {@code null} if there is none bound
     */
    public static ILivingAccessoryRenderer.@Nullable Factory getLivingFactory(@NonNull Item key) {
        return LIVING_RENDERERS.get(key);
    }

    /**
     * Check whether an {@link Item} will prevent rendering given an {@link EntityType}
     * @param key the {@link Item} to check for
     * @param type the type of the entity to test with the filter, if there is a binding
     * @return {@code true} if rendering should be prevented with the given key and {@link EntityType}, {@code false} if it should be allowed to proceed
     */
    public static boolean isNoRender(@NonNull Item key, @NonNull EntityType<?> type) {
        if (NO_RENDERS.containsKey(key)) {
            return NO_RENDERS.get(key).test(type);
        }

        return false;
    }
}
