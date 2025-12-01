package com.nythicalnorm.nythicalSpaceProgram.planet;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.dimensions.SpaceDimension;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.HashMap;
import java.util.Optional;

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

    //server side call only
    public static PlanetaryBody getDimensionPlanet(DimensionType dim) {
        for (ResourceKey<Level> level : planetDimensions.keySet()) {
            if (level == null || NythicalSpaceProgram.getSolarSystem() == null) {
                continue;
            }
            if (NythicalSpaceProgram.getSolarSystem().getServer().get().getLevel(level).dimensionType() == dim) {
                return Planets.getPlanet(planetDimensions.get(level));
            }
        }
        return null;
    }

    //server side call only
    public static ServerLevel getDimensionLevel(DimensionType dim) {
        if (NythicalSpaceProgram.getSolarSystem() == null) {
            return null;
        }
        if (NythicalSpaceProgram.getSolarSystem().getServer().isEmpty()) {
            return null;
        }

        for (ServerLevel level :  NythicalSpaceProgram.getSolarSystem().getServer().get().getAllLevels()) {
            if (level.dimensionType() == dim) {
                return level;
            }
        }
        return null;
    }

    public static Optional<PlanetaryBody> getDimPlanet(ResourceKey<Level> dim) {
        if (planetDimensions.containsKey(dim)) {
            return Optional.of(Planets.PLANETARY_BODIES.get(planetDimensions.get(dim)));
        }
        return Optional.empty();
    }

    public static Optional<Double> getAccelerationDueToGravityAt(ResourceKey<Level> levelResourceKey) {
        Optional<Double> levelGravity = Optional.empty();
        if (PlanetDimensions.isDimensionPlanet(levelResourceKey)) {
            Optional<PlanetaryBody> plnt = getDimPlanet(levelResourceKey);
            if (plnt.isPresent()) {
                double g = plnt.get().getAccelerationDueToGravity();
                double adjustedg = g*0.1d*0.08d;
                levelGravity = Optional.of(adjustedg);
            }
        } else if (PlanetDimensions.isDimensionSpace(levelResourceKey)) {
            levelGravity = Optional.of(0d);
        }
        return levelGravity;
    }

    public static boolean isDimensionSpace(ResourceKey<Level> dim) {
        return dim == SpaceDimension.SPACE_LEVEL_KEY;
    }
}
