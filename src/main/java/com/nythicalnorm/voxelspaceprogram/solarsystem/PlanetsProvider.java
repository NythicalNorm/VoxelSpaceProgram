package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.*;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.*;

public class PlanetsProvider {
    private static final Map<ResourceKey<Level>, PlanetaryBody> planetDimensions = new Object2ObjectOpenHashMap<>();
    private final Map<OrbitId, PlanetaryBody> allPlanetaryBodies = new Object2ObjectOpenHashMap<>();
    private final Map<OrbitId, EntitySpacecraftBody> allSpacecraftBodies = new Object2ObjectOpenHashMap<>();
    //public Map<OrbitId, EntitySpacecraftBody> allSpacecraftBodies = new Object2ObjectOpenHashMap<>();

    public PlanetaryBody NILA =  new PlanetaryBody("nila", new OrbitalElements(
            382599226,0.091470106618193394721,6.476694128611285E-02,
            5.4073390958703955178,2.162973108375887854, Calcs.TimePerTickToTimePerMilliTick(2.7140591915324141503)),
            //2358720),
            null, new PlanetAtmosphere(false, 0, 0, 0, 0.0f, 1.0f, 0.005f),
            new HashMap<>(),1737400, 7.34767309E22,  0f, 0, 2358720);

    public PlanetaryBody BUMI = new PlanetaryBody("bumi", new OrbitalElements(
            149653496273.0d,4.657951002584728917e-6,1.704239718110438E-02,
            5.1970176873649567284,2.8619013937171278172,Calcs.TimePerTickToTimePerMilliTick(6.2504793475201942954)),
             // 31557600),
            Level.OVERWORLD, new PlanetAtmosphere(true, 0x5ba3e6, 0x0077ff,
                     100000, 0.25f,1.0f, 0.5f),
                    new HashMap<>() {{put(NILA.getOrbitId(), NILA);}},6371000, 5.97219E24, 0.408407f , 0, 86400);

    public Star SURIYAN = new Star("suriyan", new PlanetAtmosphere(true, 0xffffa8, 0xFFE742, 250000000, 0.5f,1.0f, 1.0f),
            new HashMap<>() {{put(BUMI.getOrbitId(), BUMI);}},696340000, 1.989E30);


    public PlanetsProvider(boolean isClientSide) {
        //reeaally temporary but need to put this here right now, going to write a json parser for this next
        allPlanetaryBodies.put(NILA.getOrbitId(), NILA);
        allPlanetaryBodies.put(BUMI.getOrbitId(), BUMI);
        allPlanetaryBodies.put(SURIYAN.getOrbitId(), SURIYAN);
        planetDimensions.put(BUMI.getDimension(), BUMI);

        SURIYAN.initCalcs();
    }

    public void UpdatePlanets(long currentTime) {
        SURIYAN.simulatePlanets(currentTime);
    }

    public Map<OrbitId, EntitySpacecraftBody> getAllSpacecraftBodies() {
        return allSpacecraftBodies;
    }

    public @Nullable PlanetaryBody getPlanet(String key) {
        for (PlanetaryBody planetaryBody : allPlanetaryBodies.values()) {
            if (planetaryBody.getName().equals(key)) {
                return planetaryBody;
            }
        }
        return null;
    }

    public PlanetaryBody getPlanet(OrbitId planetID) {
        return allPlanetaryBodies.get(planetID);
    }

    public Orbit getSpacecraftOrbit(OrbitId spacecraftBodyAddress) {
        return allSpacecraftBodies.get(spacecraftBodyAddress);
    }

    public void playerChangeOrbitalSOIs(Orbit spacecraftBody, OrbitId newParentID, OrbitalElements orbitalElementsNew) {
        PlanetaryBody newOrbitPlanet = getPlanet(newParentID);

        orbitalElementsNew.setOrbitalPeriod(newOrbitPlanet.getMass());
        spacecraftBody.setOrbitalElements(orbitalElementsNew);

        //removing the old reference to the object
        spacecraftBody.removeParent();
        // adding reference to new object
        newOrbitPlanet.addChildBody(spacecraftBody);
    }

    // Need to split off this into its own data packet
    public void playerJoinedOrbital(OrbitId newParentID, EntitySpacecraftBody OrbitalDataNew) {
        Orbit newOrbitPlanet = getPlanet(newParentID);

        if (newOrbitPlanet instanceof PlanetaryBody plnt) {
            OrbitalDataNew.getOrbitalElements().setOrbitalPeriod(plnt.getMass());
            //temp default Rotation
            OrbitalDataNew.setRotation(new Quaternionf());
            plnt.addChildBody(OrbitalDataNew);
        }
    }

    public List<String> getAllPlanetNames() {
        List<String> planetNames = new ArrayList<>();
        for (PlanetaryBody planetaryBody : allPlanetaryBodies.values()) {
            planetNames.add(planetaryBody.getName());
        }
        return planetNames;
    }

    public Map<OrbitId, PlanetaryBody> getAllPlanetaryBodies() {
        return allPlanetaryBodies;
    }

    public List<PlanetaryBody> getAllPlanetOrbitsList() {
        return allPlanetaryBodies.values().stream().toList();
    }

    public boolean isDimensionPlanet(ResourceKey<Level> dim) {
        if (dim == null) {
            return false;
        }
        return planetDimensions.containsKey(dim);
    }

    public PlanetaryBody getDimensionPlanet(ResourceKey<Level> dim) {
        return planetDimensions.get(dim);
    }

    public boolean isDimensionSpace(ResourceKey<Level> dim) {
        return dim == SpaceDimension.SPACE_LEVEL_KEY;
    }

    public PlanetaryBody getDimensionPlanet(DimensionType dim) {
        for (ResourceKey<Level> level : planetDimensions.keySet()) {
            if (level == null || VoxelSpaceProgram.getSolarSystem().isEmpty()) {
                continue;
            }
            Level currentLevel = VoxelSpaceProgram.getSolarSystem().get().getServer().getLevel(level);
            if (currentLevel != null) {
                if (currentLevel.dimensionType() == dim) {
                    return planetDimensions.get(level);
                }
            }
        }
        return null;
    }

    public Optional<PlanetaryBody> getDimPlanet(Level level) {
        LazyOptional<PlanetLevelData> planetLevelData = level.getCapability(PlanetLevelDataProvider.PLANET_LEVEL_DATA);

        if (planetLevelData.isPresent()) {
            Optional<PlanetLevelData> optionalPlanetData = planetLevelData.resolve();
            if (optionalPlanetData.isPresent()) {
                return Optional.of(getPlanet(optionalPlanetData.get().getPlanetID()));
            }
        }
        return Optional.empty();
    }
}
