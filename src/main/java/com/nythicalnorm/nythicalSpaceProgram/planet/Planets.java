package com.nythicalnorm.nythicalSpaceProgram.planet;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.common.Orbit;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.dimensions.SpaceDimension;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.OrbitalElements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

public class Planets {
    //public HashMap<String, PlanetaryBody> planetaryBodies = new HashMap<>();
    public HashMap<String, Stack<String>> allPlanetsAddresses = new HashMap<>();
    private static final HashMap<ResourceKey<Level>, String> planetDimensions = new HashMap<>(){{put(Level.OVERWORLD, "bumi");}};

    public Planets(boolean isClientSide) {
        SURIYAN.setChildAddresses(allPlanetsAddresses);
        SURIYAN.initCalcs();
    }

    public PlanetaryBody NILA =  new PlanetaryBody(new OrbitalElements(
            382599226,0.091470106618193394721,6.476694128611285E-02,
            5.4073390958703955178,2.162973108375887854,2.7140591915324141503),
            //2358720),
            new PlanetAtmosphere(false, new float[]{0f, 0f, 0f, 0f}, new float[]{0f, 0f, 0f, 0f}, 0, 1.0f, 0.005f),
            new HashMap<>(),1737400, 7.34767309E22,  0f, 0, 2358720,
            ResourceLocation.parse("nythicalspaceprogram:textures/planets/nila_test.png"));

    public PlanetaryBody BUMI = new PlanetaryBody(new OrbitalElements(
            149653496273.0d,4.657951002584728917e-6,1.704239718110438E-02,
            5.1970176873649567284,2.8619013937171278172,6.2504793475201942954),
             // 31557600),
             new PlanetAtmosphere(true, new float[]{0.7215686274509804f,0.8235294117647058f,1f, 1.0f}, new float[]{0.4823529411764706f,0.6705882352941176f,1f, 1.0f},
                     100000, 1.0f, 0.5f),
                    new HashMap<>() {{put("nila", NILA);}},6371000, 5.97219E24, 0.408407f , 0, 86400,
            ResourceLocation.parse("nythicalspaceprogram:textures/planets/overworld_test.png"));

    public Star SURIYAN = new Star(new PlanetAtmosphere(true, new float[]{1f,1f,1f, 1f}, new float[]{1f,1f,1f, 0f}, 200000, 1.0f, 1.0f),
            new HashMap<>() {{put("bumi", BUMI);}},696340000, 1.989E30, ResourceLocation.parse("nythicalspaceprogram:textures/planets/kathir_test.png"));


    public void UpdatePlanets(double currentTime) {
        SURIYAN.simulatePlanets(currentTime);
    }

    public PlanetaryBody getPlanet(String key) {
        Stack<String> name = allPlanetsAddresses.get(key);
        return getPlanet(name);
    }

    public PlanetaryBody getPlanet(Stack<String> address) {
        if (address != null) {
            Orbit orb = SURIYAN.getOrbit((Stack<String>)address.clone());
            if (orb instanceof PlanetaryBody) {
                return (PlanetaryBody) orb;
            }
        }
        return null;
    }

    public Set<String> getAllPlanetNames() {
        return allPlanetsAddresses.keySet();
    }

    public Stack<String> getPlanetAddress(String plnt) {
        return allPlanetsAddresses.get(plnt);
    }

    public boolean isDimensionPlanet(ResourceKey<Level> dim) {
        if (dim == null) {
            return false;
        }
        return planetDimensions.containsKey(dim);
    }

    public String getDimensionPlanet(ResourceKey<Level> dim) {
        return planetDimensions.get(dim);
    }


    public boolean isDimensionSpace(ResourceKey<Level> dim) {
        return dim == SpaceDimension.SPACE_LEVEL_KEY;
    }

    public PlanetaryBody getDimensionPlanet(DimensionType dim) {
        for (ResourceKey<Level> level : planetDimensions.keySet()) {
            if (level == null || NythicalSpaceProgram.getSolarSystem().isEmpty()) {
                continue;
            }
            Level currentLevel = NythicalSpaceProgram.getSolarSystem().get().getServer().get().getLevel(level);
            if (currentLevel != null) {
                if (currentLevel.dimensionType() == dim) {
                    return NythicalSpaceProgram.getSolarSystem().get().getPlanets().getPlanet((planetDimensions.get(level)));
                }
            }
        }
        return null;
    }

    public Optional<PlanetaryBody> getDimPlanet(Level level) {
        LazyOptional<PlanetLevelData> planetLevelData = level.getCapability(PlanetLevelDataProvider.PLANET_LEVEL_DATA);

        if (planetLevelData.isPresent()) {
            return Optional.of(getPlanet(planetLevelData.resolve().get().getPlanetName()));
        }
        return Optional.empty();
    }
}
