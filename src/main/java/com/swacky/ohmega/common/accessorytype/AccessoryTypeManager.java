package com.swacky.ohmega.common.accessorytype;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.swacky.ohmega.common.core.Ohmega;
import com.swacky.ohmega.common.core.init.OhmegaTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;

public class AccessoryTypeManager extends SimplePreparableReloadListener<ImmutableList<AccessoryType>> {
    private static final AccessoryTypeManager INSTANCE = new AccessoryTypeManager();

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(AccessoryTypeLow.class, AccessoryTypeLow.Deserializer.getInstance()).create();
    private static final TypeToken<Map<String, AccessoryTypeLow>> TOKEN = new TypeToken<>() {};

    private final ArrayList<AccessoryType> types = new ArrayList<>();

    private AccessoryTypeManager() {}

    public static AccessoryTypeManager getInstance() {
        return INSTANCE;
    }

    @Override
    protected @NotNull ImmutableList<AccessoryType> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        ImmutableList.Builder<AccessoryType> builder = ImmutableList.builderWithExpectedSize(3);
        for (String namespace : resourceManager.getNamespaces()) {
            try {
                for (Resource resource : resourceManager.getResourceStack(ResourceLocation.fromNamespaceAndPath(namespace, "ohmega/accessory_types.json"))) {
                    try (Reader reader = resource.openAsReader()) {
                        Map<String, AccessoryTypeLow> map = GsonHelper.fromJson(GSON, reader, TOKEN);
                        for (Map.Entry<String, AccessoryTypeLow> entry : map.entrySet()) {
                            builder.add(new AccessoryType(namespace, entry.getKey(), entry.getValue()));
                        }
                    } catch (RuntimeException e) {
                        Ohmega.LOGGER.warn("Invalid 'ohmega/accessory_types.json' in DataPack: '{}'", resource.sourcePackId(), e);
                    }
                }
            } catch (IOException ignored) {
            }
        }
        return builder.build();
    }

    @Override
    protected void apply(@NotNull ImmutableList<AccessoryType> types, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        this.types.clear();
        this.types.addAll(types);
        OhmegaTags.register();
    }

    public ImmutableList<AccessoryType> getTypes() {
        return ImmutableList.copyOf(this.types);
    }

    public AccessoryType get(ResourceLocation location) {
        for (AccessoryType type : this.getTypes()) {
            if (type.getId().equals(location)) {
                return type;
            }
        }
        return null;
    }
}
