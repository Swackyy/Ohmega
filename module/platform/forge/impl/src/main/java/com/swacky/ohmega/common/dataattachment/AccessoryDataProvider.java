package com.swacky.ohmega.common.dataattachment;

import com.swacky.ohmega.api.common.Ohmega;
import com.swacky.ohmega.api.common.dataattachment.AccessoryData;
import com.swacky.ohmega.common.init.OhmegaDataAttachmentsImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("deprecation")
public final class AccessoryDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Identifier CAPABILITY_ID = Ohmega.id("accessory_data");

    private AccessoryData data;
    private final LazyOptional<AccessoryData> capabilityOpt;

    public AccessoryDataProvider() {
        this.data = new AccessoryData();
        this.capabilityOpt = LazyOptional.of(() -> this.data);
    }

    @NonNull
    @Override
    public <T> LazyOptional<T> getCapability(@NonNull Capability<T> cap, Direction side) {
        return OhmegaDataAttachmentsImpl.ACCESSORIES.orEmpty(cap, capabilityOpt);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
        return (CompoundTag) AccessoryData.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), data)
                .result().orElseGet(CompoundTag::new);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag tag) {
        AccessoryData.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), tag).resultOrPartial().ifPresent(data -> this.data = data);
    }
}
