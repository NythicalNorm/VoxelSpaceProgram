package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import com.nythicalnorm.nythicalSpaceProgram.dimensions.PlanetDimensions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.HashMap;

public class Planets {
    public static HashMap<String, PlanetaryBody> PLANETARY_BODIES = new HashMap<>();

    public static PlanetaryBody BUMI = registerPlanet("bumi", new PlanetaryBody(new OrbitalElements(
            22374000,0.174533,0.8,
            3.081359034620368E+02,1.239837028145578E+02,0,
            10000,null),
            1737400, new Vector3f(0,1,0), 0, 10000,
            ResourceLocation.parse("nythicalspaceprogram:textures/planets/overworld_test.png")), Level.OVERWORLD
            );

    private static PlanetaryBody registerPlanet(String name, PlanetaryBody plnt, ResourceKey<Level> planetDim) {
        PLANETARY_BODIES.put(name, plnt);
        PlanetDimensions.registerPlanetDim(name, planetDim);
        return plnt;
    }


    public static PlanetaryBody getPlanet(String key) {
        return PLANETARY_BODIES.get(key);
    }
}
