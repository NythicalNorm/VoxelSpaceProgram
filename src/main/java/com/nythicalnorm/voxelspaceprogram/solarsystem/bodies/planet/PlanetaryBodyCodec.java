package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.planet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nythicalnorm.voxelspaceprogram.network.NetworkEncoders;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitCodec;
import com.nythicalnorm.voxelspaceprogram.storage.PlanetDataResolver;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.AxisAngle4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlanetaryBodyCodec extends OrbitCodec<PlanetaryBody, PlanetaryBody.PlanetBuilder> {

    @Override
    public void encodeBuffer(PlanetaryBody planetBody, FriendlyByteBuf byteBuf) {
        super.encodeBuffer(planetBody, byteBuf);
        NetworkEncoders.writeASCII(byteBuf, planetBody.getName());
        byteBuf.writeDouble(planetBody.getRadius());
        byteBuf.writeDouble(planetBody.getMass());

        byteBuf.writeFloat(planetBody.getNorthPoleDir().angle);
        byteBuf.writeFloat(planetBody.getNorthPoleDir().x);
        byteBuf.writeFloat(planetBody.getNorthPoleDir().y);
        byteBuf.writeFloat(planetBody.getNorthPoleDir().z);

        byteBuf.writeLong(planetBody.getRotationPeriod());
        NetworkEncoders.writePlanetAtmosphere(byteBuf, planetBody.getAtmosphere());

        if (planetBody.getDimension() == null) {
            byteBuf.writeBoolean(false);
        } else {
            byteBuf.writeBoolean(true);
            byteBuf.writeResourceKey(planetBody.getDimension());
        }
    }

    @Override
    public PlanetaryBody.PlanetBuilder decodeBuffer(PlanetaryBody.PlanetBuilder planetBuilder, FriendlyByteBuf byteBuf) {
        super.decodeBuffer(planetBuilder, byteBuf);
        planetBuilder.setName(NetworkEncoders.readASCII(byteBuf));
        planetBuilder.setRadius(byteBuf.readDouble());
        planetBuilder.setMass(byteBuf.readDouble());

        planetBuilder.setNorthPoleDir(new AxisAngle4f(
                byteBuf.readFloat(),
                byteBuf.readFloat(),
                byteBuf.readFloat(),
                byteBuf.readFloat()));

        planetBuilder.setRotationPeriod(byteBuf.readLong());
        planetBuilder.setAtmosphericEffects(NetworkEncoders.readPlanetAtmosphere(byteBuf));

        if (byteBuf.readBoolean()) {
            planetBuilder.setDimension(byteBuf.readResourceKey(Registries.DIMENSION));
        }

        return planetBuilder;
    }

    @Override
    public PlanetaryBody.PlanetBuilder readCelestialBodyDatapack(PlanetaryBody.PlanetBuilder body, String name, JsonObject jsonObj, Map<String, String[]> tempChildPlanetsMap) {
        body.setName(name);
        body.setRadius(jsonObj.get("radius").getAsDouble());
        body.setMass(jsonObj.get("mass").getAsDouble());

        JsonElement north_pole_dir = jsonObj.get("north_pole_dir");
        JsonElement starting_rotation = jsonObj.get("starting_rotation");
        if (north_pole_dir != null && starting_rotation != null) {
            JsonObject north_pole_obj = north_pole_dir.getAsJsonObject();
            body.setNorthPoleDir(north_pole_obj.get("right_ascension").getAsFloat(),
                    north_pole_obj.get("declination").getAsFloat(),
                    starting_rotation.getAsFloat());
        }
        JsonElement rotation_period = jsonObj.get("rotation_period");
        body.setRotationPeriod(rotation_period != null ? Calcs.timeDoubleToLong(rotation_period.getAsDouble()) : 0L);

        JsonElement dimensionID = jsonObj.get("dimension_id");
//        if (dimensionID != null) {
//            tempDimensionsMap.put(name, dimensionID.getAsString());
//        }
        if (dimensionID != null) {
            ResourceLocation dimensionResourceLoc = ResourceLocation.parse(dimensionID.getAsString());
            ResourceKey<Level> dimensionLevelKey = ResourceKey.create(Registries.DIMENSION, dimensionResourceLoc);
            body.setDimension(dimensionLevelKey);
        }

        JsonElement orbital_elements_json = jsonObj.get("orbital_elements");
        if (orbital_elements_json != null) {
            body.setOrbitalElements(PlanetDataResolver.parseOrbitalElements(orbital_elements_json.getAsJsonObject()));
        }

        JsonElement atmosphericData = jsonObj.get("atmospheric_data");
        if (atmosphericData != null) {
            body.setAtmosphericEffects(PlanetDataResolver.parseAtmosphericData(atmosphericData.getAsJsonObject()));
        }

        List<String> childPlanetNames = new ArrayList<>();
        JsonElement childPlanets = jsonObj.get("child_planets");

        if (childPlanets != null) {
            JsonArray planetNameArray = childPlanets.getAsJsonArray();
            planetNameArray.forEach(jsonElement -> childPlanetNames.add(jsonElement.getAsString()));
        }

        tempChildPlanetsMap.put(name, childPlanetNames.toArray(new String[0]));

        return body;
    }
}
