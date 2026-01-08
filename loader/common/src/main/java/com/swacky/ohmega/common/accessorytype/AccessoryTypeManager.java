package com.swacky.ohmega.common.accessorytype;

import com.google.common.collect.ImmutableSet;
import com.google.gson.reflect.TypeToken;
import com.swacky.ohmega.common.OhmegaCommon;
import com.swacky.ohmega.common.init.OhmegaTags;
import com.swacky.ohmega.event.OhmegaHooks;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;

import java.io.Reader;
import java.util.HashSet;
import java.util.Map;

public final class AccessoryTypeManager extends SimplePreparableReloadListener<ImmutableSet<AccessoryType>> {
    private static final AccessoryTypeManager INSTANCE = new AccessoryTypeManager();
    private static final int DEFAULT_SIZE = 4;
    public static final String LOCATION = OhmegaCommon.MODID + "/accessory_types.json";
    private static final TypeToken<Map<String, AccessoryType.Builder>> TOKEN = new TypeToken<>() {};

    private final HashSet<AccessoryType> types = new HashSet<>();

    private AccessoryTypeManager() {}

    public static AccessoryTypeManager getInstance() {
        return INSTANCE;
    }

    @Override
    protected @NonNull ImmutableSet<AccessoryType> prepare(@NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        ImmutableSet.Builder<AccessoryType> builder = ImmutableSet.builderWithExpectedSize(DEFAULT_SIZE);

        for (String namespace : manager.getNamespaces()) {
            for (Resource resource : manager.getResourceStack(Identifier.fromNamespaceAndPath(namespace, LOCATION))) {
                try (Reader reader = resource.openAsReader()) {
                    Map<String, AccessoryType.Builder> map = GsonHelper.fromJson(AccessoryType.Deserializer.GSON, reader, TOKEN);

                    for (Map.Entry<String, AccessoryType.Builder> entry : map.entrySet()) {
                        builder.add(entry.getValue().build(namespace, entry.getKey()));
                    }
                } catch (Exception e) {
                    OhmegaCommon.LOGGER.warn("Could not read '{}' in DataPack: '{}'", LOCATION, resource.sourcePackId(), e);
                }
            }
        }

        return builder.addAll(OhmegaHooks.registerAccessoryTypesEvent()).build();
    }

    public void apply(ImmutableSet<AccessoryType> types) {
        this.types.clear();
        this.types.addAll(types);

        OhmegaTags.refresh();
    }

    public void clear() {
        types.clear();
    }

    @Override
    protected void apply(@NonNull ImmutableSet<AccessoryType> types, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profiler) {
        apply(types);
    }

    public ImmutableSet<AccessoryType> getTypes() {
        return ImmutableSet.copyOf(types);
    }

    public @NonNull AccessoryType get(Identifier id) {
        for (AccessoryType type : types) {
            if (type.getId().equals(id)) {
                return type;
            }
        }

        return AccessoryType.NORMAL.get();
    }

    public boolean exists(String id) {
        for (AccessoryType type : types) {
            if (type.getId().equals(Identifier.parse(id))) {
                return true;
            }
        }

        return false;
    }
}
