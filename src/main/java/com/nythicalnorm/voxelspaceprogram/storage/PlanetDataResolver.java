package com.nythicalnorm.voxelspaceprogram.storage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.CelestialBodyTypes;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.planet.PlanetAtmosphere;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.star.StarBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBodyType;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Map;

public class PlanetDataResolver extends SimpleJsonResourceReloadListener {
    private static final Logger logger = VoxelSpaceProgram.getLogger();
    public static final Gson GSON_INSTANCE = Deserializers.createFunctionSerializer().create();

    public PlanetDataResolver() {
        super(GSON_INSTANCE, "vsp_planetary_bodies");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        Map<String, String[]> tempChildPlanetsMap = new Object2ObjectOpenHashMap<>();
        Map<String, CelestialBody> tempPlanetaryBodyMap = new Object2ObjectOpenHashMap<>();
        StarBody rootStar = null;

        pObject.forEach((key, element) -> {
            if (key == null || element == null) {
                return;
            }
            try {
                JsonObject jsonObject = element.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String bodyType = jsonObject.get("type").getAsString();
                OrbitalBodyType<? extends OrbitalBody, ? extends OrbitalBody.Builder<?>> orbitalBodyType = CelestialBodyTypes.getType(bodyType);
                if (orbitalBodyType != null) {
                    OrbitalBody.Builder<?> planetBuilder = orbitalBodyType.readCelestialBodyDataPack(name, jsonObject, tempChildPlanetsMap);
                    OrbitalBody readOrbitalBody = planetBuilder.build();
                    if (readOrbitalBody instanceof CelestialBody celestialBody) {
                        tempPlanetaryBodyMap.put(celestialBody.getName(), celestialBody);
                    }
                } else {
                    throw new IllegalStateException("Planetary Body is of unknown type: " + bodyType);
                }
            } catch (Exception e) {
                logger.error("Unable to parse datapack for planetary body {}", key.getPath());
                e.printStackTrace();
            }
        });

        for (CelestialBody celestialBody : tempPlanetaryBodyMap.values()) {
            if (celestialBody instanceof StarBody starBody) {
                rootStar = starBody;
            }
        }

        if (rootStar == null) {
            logger.error("No Stars found in the solar system data, can't start server...");
            return;
        }
        VSPDataPackManager.planetDatapackLoaded(new PlanetLoadedData(rootStar, tempPlanetaryBodyMap, tempChildPlanetsMap));
    }

    public static OrbitalElements parseOrbitalElements(JsonObject orbital_elements_json) {
        double semiMajorAxis = orbital_elements_json.get("semi_major_axis").getAsDouble();
        double eccentricity = orbital_elements_json.get("eccentricity").getAsDouble();
        double periapsisTime = orbital_elements_json.get("periapsis_time").getAsDouble();
        double inclination = orbital_elements_json.get("inclination").getAsDouble();
        double argumentOfPeriapsis = orbital_elements_json.get("argument_of_periapsis").getAsDouble();
        double longitudeOfAscendingNode = orbital_elements_json.get("longitude_of_ascending_node").getAsDouble();

        return new OrbitalElements(semiMajorAxis, eccentricity, Calcs.TimePerTickToTimePerMilliTick(periapsisTime)
                , inclination, argumentOfPeriapsis, longitudeOfAscendingNode);
    }

    public static PlanetAtmosphere parseAtmosphericData(JsonObject jsonObj) {
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

    public record PlanetLoadedData(StarBody rootStar, Map<String, CelestialBody> tempPlanetaryBodyMap, Map<String, String[]> tempChildPlanetsMap) {

    }
}
