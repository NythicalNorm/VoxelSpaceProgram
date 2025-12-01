package com.nythicalnorm.nythicalSpaceProgram.planet;

import com.nythicalnorm.nythicalSpaceProgram.common.Orbit;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.OrbitalElements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class Planets {
    public static HashMap<String, PlanetaryBody> PLANETARY_BODIES = new HashMap<>();

    public static PlanetaryBody NILA = registerPlanet("nila", new PlanetaryBody(new OrbitalElements(
            382599226,0.091470106618193394721,6.476694128611285E-02,
            5.4073390958703955178,2.162973108375887854,2.7140591915324141503),
            //2358720),
            new PlanetAtmosphere(false, new float[]{0f, 0f, 0f, 0f}, new float[]{0f, 0f, 0f, 0f}, 0, 1.0f, 0.005f),
            null,1737400, 7.34767309E22,  0f, 0, 2358720,
            ResourceLocation.parse("nythicalspaceprogram:textures/planets/nila_test.png")), null
    );

    public static PlanetaryBody BUMI = registerPlanet("bumi", new PlanetaryBody(new OrbitalElements(
            149653496273.0d,4.657951002584728917e-6,1.704239718110438E-02,
            5.1970176873649567284,2.8619013937171278172,6.2504793475201942954),
             // 31557600),
             new PlanetAtmosphere(true, new float[]{0.7215686274509804f,0.8235294117647058f,1f, 1.0f}, new float[]{0.4823529411764706f,0.6705882352941176f,1f, 1.0f},
                     100000, 1.0f, 0.5f),
                    new HashMap<>() {{put("nila", NILA);}},6371000, 5.97219E24, 0.408407f , 0, 86400,
            ResourceLocation.parse("nythicalspaceprogram:textures/planets/overworld_test.png")), Level.OVERWORLD
            );

    public static Star SURIYAN = (Star) registerPlanet("suriyan", new Star(new PlanetAtmosphere(true, new float[]{1f,1f,1f, 1f}, new float[]{1f,1f,1f, 0f}, 200000, 1.0f, 1.0f),
            new HashMap<>() {{put("bumi", BUMI);}},696340000, 1.989E30, ResourceLocation.parse("nythicalspaceprogram:textures/planets/kathir_test.png")), null
    );


    private static PlanetaryBody registerPlanet(String name, PlanetaryBody plnt, ResourceKey<Level> planetDim) {
        PLANETARY_BODIES.put(name, plnt);
        PlanetDimensions.registerPlanetDim(name, planetDim);
        return plnt;
    }

    public static void UpdatePlanets(double currentTime) {
        SURIYAN.simulatePlanets(currentTime);
    }

    public static PlanetaryBody getPlanet(String key) {
        return PLANETARY_BODIES.get(key);
    }

    public static void planetInit() {
        SURIYAN.initCalcs();
    }
}
