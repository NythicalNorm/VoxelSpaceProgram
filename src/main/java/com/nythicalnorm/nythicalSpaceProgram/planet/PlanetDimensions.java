package com.nythicalnorm.nythicalSpaceProgram.planet;

import com.nythicalnorm.nythicalSpaceProgram.dimensions.SpaceDimension;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class PlanetDimensions {
    private static final HashMap<String, ResourceKey<Level>> planetDimensions = new HashMap<>();

    public static void registerPlanetDim(String name, ResourceKey<Level> planetLevel) {
        planetDimensions.put(name, planetLevel);
    }

    public static boolean isDimensionPlanet(ResourceKey<Level> dim) {
        if (dim == null) {
            return false;
        }
        return planetDimensions.containsValue(dim);
    }

    public static boolean isDimensionSpace(ResourceKey<Level> dim) {
        return dim == SpaceDimension.SPACE_LEVEL_KEY;
    }
}
