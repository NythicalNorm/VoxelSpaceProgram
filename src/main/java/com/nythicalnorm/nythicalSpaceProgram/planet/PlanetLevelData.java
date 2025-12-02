package com.nythicalnorm.nythicalSpaceProgram.planet;

import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Stack;

public class PlanetLevelData {
    private String planetName;

    public PlanetLevelData(String planetName) {
        this.planetName = planetName;
    }

    public PlanetLevelData() {
        this.planetName = "";
    }

    public String getPlanetName() {
        return planetName;
    }

    public PlanetaryBody getPlanetaryBody(Planets planets) {
        return planets.getPlanet(planetName);
    }

    public double getAccelerationDueToGravity(Planets planets) {
        Optional<Double> levelGravity = Optional.empty();
        PlanetaryBody plnt = planets.getPlanet(this.planetName);
        double g = plnt.getAccelerationDueToGravity();
        double adjustedg = g*0.1d*0.08d;
        return adjustedg;
    }

    public CompoundTag saveNBT(CompoundTag nbt) {
        nbt.putString("NSP.planetName", this.planetName);
        return nbt;
    }

    public void loadNBT(CompoundTag nbt) {
        this.planetName = nbt.getString("NSP.planetName");
    }

    public void copyFrom(@NotNull PlanetLevelData oldStore) {
        planetName = oldStore.planetName;
    }

    public void encode (FriendlyByteBuf buffer) {
        buffer.writeUtf (this.planetName);
    }

    public void decode (FriendlyByteBuf buffer) {
        this.planetName = buffer.readUtf();
    }
}
