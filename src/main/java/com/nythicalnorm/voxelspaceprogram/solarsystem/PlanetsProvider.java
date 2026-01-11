package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.PlanetAtmosphere;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.PlanetLevelData;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.PlanetLevelDataProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.util.LazyOptional;
import org.joml.Quaternionf;

import java.util.*;

public class PlanetsProvider {
    //public HashMap<String, PlanetaryBody> planetaryBodies = new HashMap<>();
    public HashMap<String, Stack<String>> allPlanetsAddresses = new HashMap<>();
    private static final Map<ResourceKey<Level>, PlanetaryBody> planetDimensions = new Object2ObjectOpenHashMap<>();

    public PlanetsProvider(boolean isClientSide) {
        SURIYAN.setChildAddresses(allPlanetsAddresses);
        SURIYAN.initCalcs();

        //reeaally temporary but need to put this here right now
        planetDimensions.put(BUMI.getDimension(), BUMI);
    }

    public PlanetaryBody NILA =  new PlanetaryBody(new OrbitalElements(
            382599226,0.091470106618193394721,6.476694128611285E-02,
            5.4073390958703955178,2.162973108375887854, Calcs.TimePerTickToTimePerMilliTick(2.7140591915324141503)),
            //2358720),
            null, new PlanetAtmosphere(false, 0, 0, 0, 0.0f, 1.0f, 0.005f),
            new HashMap<>(),1737400, 7.34767309E22,  0f, 0, 2358720);

    public PlanetaryBody BUMI = new PlanetaryBody(new OrbitalElements(
            149653496273.0d,4.657951002584728917e-6,1.704239718110438E-02,
            5.1970176873649567284,2.8619013937171278172,Calcs.TimePerTickToTimePerMilliTick(6.2504793475201942954)),
             // 31557600),
            Level.OVERWORLD, new PlanetAtmosphere(true, 0x5ba3e6, 0x0077ff,
                     100000, 0.25f,1.0f, 0.5f),
                    new HashMap<>() {{put("nila", NILA);}},6371000, 5.97219E24, 0.408407f , 0, 86400);

    public Star SURIYAN = new Star(new PlanetAtmosphere(true, 0xffffa8, 0xFFE742, 250000000, 0.5f,1.0f, 1.0f),
            new HashMap<>() {{put("bumi", BUMI);}},696340000, 1.989E30);


    public void UpdatePlanets(long currentTime) {
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

    public Orbit getOrbit(Stack<String> address) {
        return SURIYAN.getOrbit((Stack<String>)address.clone());
    }

    public void playerChangeOrbitalSOIs(String playerUUid, Orbit playerSpacecraftBody, Stack<String> newAddress, OrbitalElements orbitalElementsNew) {
        Orbit oldParent = playerSpacecraftBody.getParent();

        if  (oldParent instanceof PlanetaryBody oldPlanet) {//(allPlayerOrbitalAddresses.containsKey(oldAddress.firstElement())) {
            //copying the orbit so that new SOI has the same info

            Orbit newOrbitPlanet = getPlanet(newAddress);

            EntitySpacecraftBody entitybody = (EntitySpacecraftBody) oldPlanet.getChild(playerUUid);
            orbitalElementsNew.setOrbitalPeriod(((PlanetaryBody)newOrbitPlanet).getMass());
            entitybody.setOrbitalElements(orbitalElementsNew);

            //removing the old reference to the object
            oldPlanet.removeChild(playerUUid);

            //adding to the new orbit
            if (newOrbitPlanet instanceof PlanetaryBody plnt) {
                plnt.addChildSpacecraft(playerUUid, entitybody);
            }
        } else {
            VoxelSpaceProgram.logError("No Old Orbit given for changing SOIs");
        }
    }

    public void playerJoinedOrbital(String PlayerUUid, Stack<String> newAddress, EntitySpacecraftBody OrbitalDataNew) {
        Orbit newOrbitPlanet = getPlanet(newAddress);

        if (newOrbitPlanet instanceof PlanetaryBody plnt) {
            OrbitalDataNew.getOrbitalElements().setOrbitalPeriod(plnt.getMass());
            //temp default Rotation
            OrbitalDataNew.setRotation(new Quaternionf());
            plnt.addChildSpacecraft(PlayerUUid, OrbitalDataNew);
        }
    }

    public Set<String> getAllPlanetNames() {
        return allPlanetsAddresses.keySet();
    }

    public Stack<String> getPlanetAddress(String plnt) {
        return allPlanetsAddresses.get(plnt);
    }

    public PlanetaryBody[] getAllPlanetOrbits() {
        PlanetaryBody[] allPlanetList = new PlanetaryBody[allPlanetsAddresses.size()];
        int index = 0;
        for (Stack<String> address : allPlanetsAddresses.values()) {
            allPlanetList[index] = getPlanet(address);
            index++;
        }
        return allPlanetList;
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
                return Optional.of(getPlanet(optionalPlanetData.get().getPlanetName()));
            }
        }
        return Optional.empty();
    }
}
