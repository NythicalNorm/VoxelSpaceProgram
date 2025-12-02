package com.nythicalnorm.nythicalSpaceProgram.planet;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlanetLevelDataProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    public static Capability<PlanetLevelData> PLANET_LEVEL_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });

    private PlanetLevelData planetLevelData = null;
    private final LazyOptional<PlanetLevelData> optional = LazyOptional.of(this::getPlanetLevelData);

    public PlanetLevelDataProvider(PlanetLevelData planetLevelData) {
        this.planetLevelData = planetLevelData;
    }

    private @NotNull PlanetLevelData getPlanetLevelData() {
        if (this.planetLevelData == null) {
            this.planetLevelData = new PlanetLevelData();
        }

        return this.planetLevelData;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        getPlanetLevelData().saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getPlanetLevelData().loadNBT(nbt);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLANET_LEVEL_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }
}
