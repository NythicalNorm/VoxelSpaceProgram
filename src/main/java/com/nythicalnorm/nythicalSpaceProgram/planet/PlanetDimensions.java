package com.nythicalnorm.nythicalSpaceProgram.planet;

import com.nythicalnorm.nythicalSpaceProgram.dimensions.SpaceDimension;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class PlanetDimensions {
    private static final HashMap<ResourceKey<Level>, String> planetDimensions = new HashMap<>();

    public static void registerPlanetDim(String name, ResourceKey<Level> planetLevel) {
        planetDimensions.put(planetLevel, name);
    }

    public static boolean isDimensionPlanet(ResourceKey<Level> dim) {
        if (dim == null) {
            return false;
        }
        return planetDimensions.containsKey(dim);
    }

    public static PlanetaryBody getDimPlanet(ResourceKey<Level> dim) {
        return Planets.PLANETARY_BODIES.get(planetDimensions.get(dim));
    }

    public static boolean isDimensionSpace(ResourceKey<Level> dim) {
        return dim == SpaceDimension.SPACE_LEVEL_KEY;
    }
}
