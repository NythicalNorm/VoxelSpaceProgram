package com.nythicalnorm.nythicalSpaceProgram.planet;

import com.nythicalnorm.nythicalSpaceProgram.common.Orbit;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.HashMap;

public class Star extends PlanetaryBody {
    public Star(PlanetAtmosphere effects, @Nullable HashMap<String, Orbit> childBody, double radius, double mass, ResourceLocation texture) {
        super(null, effects, childBody, radius, mass, 0f, 0, 0, texture);
    }

    public void simulatePlanets(double currentTime) {
        this.simulatePropagate(currentTime, new Vector3d(0d, 0d, 0d), this.getMass());
    }

    public void initCalcs() {
        this.setSphereOfInfluence(Double.POSITIVE_INFINITY);
        this.calculateOrbitalPeriod();
        super.UpdateSOIs();
    }
}
