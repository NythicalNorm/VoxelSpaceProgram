package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.CelestialBodyTypes;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBodyType;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.storage.VSPDataPackManager;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlanetDataResolver extends SimpleJsonResourceReloadListener {
    private static final Logger logger = VoxelSpaceProgram.getLogger();
    public static final Gson GSON_INSTANCE = Deserializers.createFunctionSerializer().create();

    public PlanetDataResolver() {
        super(GSON_INSTANCE, "vsp_planetary_bodies");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Map<String, String> tempDimensionsMap = new Object2ObjectOpenHashMap<>();
        Map<String, String[]> tempChildPlanetsMap = new Object2ObjectOpenHashMap<>();
        Map<String, PlanetaryBody> tempPlanetaryBodyMap = new Object2ObjectOpenHashMap<>();
        StarBody rootStar = null;

        pObject.forEach((key, element) -> {
            if (key == null || element == null) {
                return;
            }
            try {
                JsonObject jsonObject = element.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String bodyType = jsonObject.get("type").getAsString();
                OrbitalBodyType<? extends Orbit> orbitalBodyCodec = CelestialBodyTypes.getType(bodyType);
                if (orbitalBodyCodec != null) {
                    if (orbitalBodyCodec.getInstance() instanceof PlanetaryBody planetaryBody) {
                        planetaryBody.initStaticName(name);
                        parsePlanetaryBody(planetaryBody, jsonObject, tempDimensionsMap, tempChildPlanetsMap);
                        tempPlanetaryBodyMap.put(planetaryBody.getName(), planetaryBody);
                    }
                } else {
                    throw new IllegalStateException("Planetary Body is of unknown type: " + bodyType);
                }
            } catch (Exception e) {
                logger.error("Unable to parse datapack for planetary body {}", key.getPath());
                e.printStackTrace();
            }
        });

        for (PlanetaryBody planetaryBody : tempPlanetaryBodyMap.values()) {
            if (planetaryBody instanceof StarBody starBody) {
                rootStar = starBody;
            }
        }

        if (rootStar == null) {
            logger.error("No Stars found in the solar system data, can't start server...");
            return;
        }
        VSPDataPackManager.planetDatapackLoaded(new PlanetLoadedData(rootStar, tempPlanetaryBodyMap, tempDimensionsMap, tempChildPlanetsMap));
    }

    private void parsePlanetaryBody(PlanetaryBody body, JsonObject jsonObj, Map<String, String> tempDimensionsMap, Map<String, String[]> tempChildPlanetsMap) {
        body.radius = jsonObj.get("radius").getAsDouble();
        body.mass = jsonObj.get("mass").getAsDouble();

        JsonElement north_pole_dir = jsonObj.get("north_pole_dir");
        JsonElement starting_rotation = jsonObj.get("starting_rotation");
        if (north_pole_dir != null && starting_rotation != null) {
            JsonObject north_pole_obj = north_pole_dir.getAsJsonObject();
            body.setNorthPoleDir(north_pole_obj.get("right_ascension").getAsFloat(),
                    north_pole_obj.get("declination").getAsFloat(),
                    starting_rotation.getAsFloat());
        }
        JsonElement rotation_period = jsonObj.get("rotation_period");
        body.RotationPeriod = rotation_period != null ? Calcs.timeDoubleToLong(rotation_period.getAsDouble()) : 0L;

        JsonElement dimensionID = jsonObj.get("dimension_id");
        if (dimensionID != null) {
            tempDimensionsMap.put(body.getName(), dimensionID.getAsString());
        }

        JsonElement orbital_elements_json = jsonObj.get("orbital_elements");
        if (orbital_elements_json != null) {
            body.setOrbitalElements(parseOrbitalElements(orbital_elements_json.getAsJsonObject()));
        }

        JsonElement atmosphericData = jsonObj.get("atmospheric_data");
        if (atmosphericData != null) {
           body.atmosphericEffects = parseAtmosphericData(atmosphericData.getAsJsonObject());
        }

        List<String> childPlanetNames = new ArrayList<>();
        JsonElement childPlanets = jsonObj.get("child_planets");

        if (childPlanets != null) {
             JsonArray planetNameArray = childPlanets.getAsJsonArray();
             planetNameArray.forEach(jsonElement -> childPlanetNames.add(jsonElement.getAsString()));
        }

        tempChildPlanetsMap.put(body.getName(), childPlanetNames.toArray(new String[0]));
    }

    private @Nullable OrbitalElements parseOrbitalElements(JsonObject orbital_elements_json) {
        double semiMajorAxis = orbital_elements_json.get("semi_major_axis").getAsDouble();
        double eccentricity = orbital_elements_json.get("eccentricity").getAsDouble();
        double periapsisTime = orbital_elements_json.get("periapsis_time").getAsDouble();
        double inclination = orbital_elements_json.get("inclination").getAsDouble();
        double argumentOfPeriapsis = orbital_elements_json.get("argument_of_periapsis").getAsDouble();
        double longitudeOfAscendingNode = orbital_elements_json.get("longitude_of_ascending_node").getAsDouble();

        return new OrbitalElements(semiMajorAxis, eccentricity, Calcs.TimePerTickToTimePerMilliTick(periapsisTime)
                , inclination, argumentOfPeriapsis, longitudeOfAscendingNode);
    }

    private PlanetAtmosphere parseAtmosphericData(JsonObject jsonObj) {
        boolean hasAtmosphere = jsonObj.get("has_atmosphere").getAsBoolean();
        int surfaceColor = 0;
        int atmosphereColor = 0;
        double atmosphereHeight = 0d;
        float atmosphereAlpha = 0f;
        float alphaNight;
        float alphaDay;

        if (hasAtmosphere) {
            surfaceColor = Integer.parseInt(jsonObj.get("surface_color").getAsString(), 16);
            atmosphereColor = Integer.parseInt(jsonObj.get("atmosphere_color").getAsString(), 16);
            atmosphereHeight = jsonObj.get("atmosphere_height").getAsDouble();
            atmosphereAlpha = jsonObj.get("atmosphere_alpha").getAsFloat();
        }
        alphaNight = jsonObj.get("alpha_night").getAsFloat();
        alphaDay = jsonObj.get("alpha_day").getAsFloat();

        return new PlanetAtmosphere(hasAtmosphere, surfaceColor, atmosphereColor, atmosphereHeight, atmosphereAlpha, alphaNight, alphaDay);
    }

    public record PlanetLoadedData(StarBody rootStar, Map<String, PlanetaryBody> tempPlanetaryBodyMap, Map<String, String> tempDimensionsMap, Map<String, String[]> tempChildPlanetsMap) {

    }
}
