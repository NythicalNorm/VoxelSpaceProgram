package com.nythicalnorm.voxelspaceprogram.solarsystem.planet;

import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import net.minecraft.nbt.CompoundTag;

public class PlanetLevelData {
    private OrbitId planetID;

    public PlanetLevelData(OrbitId planetName) {
        this.planetID = planetName;
    }

    public PlanetLevelData() {
        this.planetID = null;
    }

    public OrbitId getPlanetID() {
        return planetID;
    }

    public PlanetaryBody getPlanetaryBody(PlanetsProvider planets) {
        return planets.getPlanet(planetID);
    }

    public double getAccelerationDueToGravity(PlanetsProvider planets) {
        PlanetaryBody plnt = planets.getPlanet(this.planetID);
        double g = plnt.getAccelerationDueToGravity();
        return g*0.1d*0.08d;
    }

    public CompoundTag saveNBT(CompoundTag nbt) {
        planetID.encodeToNBT(nbt);
        return nbt;
    }

    public void loadNBT(CompoundTag nbt) {
        this.planetID = new OrbitId(nbt);
    }
//
//    public void copyFrom(@NotNull PlanetLevelData oldStore) {
//        planetName = oldStore.planetName;
//    }
//
//    public void encode (FriendlyByteBuf buffer) {
//        buffer.writeUtf (this.planetName);
//    }
//
//    public void decode (FriendlyByteBuf buffer) {
//        this.planetName = buffer.readUtf();
//    }
}
