package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.star;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nythicalnorm.voxelspaceprogram.network.NetworkEncoders;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitCodec;
import com.nythicalnorm.voxelspaceprogram.storage.PlanetDataResolver;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StarBodyCodec extends OrbitCodec<StarBody, StarBody.StarBuilder> {
    @Override
    public void encodeBuffer(StarBody sunBody, FriendlyByteBuf byteBuf) {
        super.encodeBuffer(sunBody, byteBuf);
        NetworkEncoders.writeASCII(byteBuf, sunBody.getName());
        byteBuf.writeDouble(sunBody.getRadius());
        byteBuf.writeDouble(sunBody.getMass());
        NetworkEncoders.writePlanetAtmosphere(byteBuf, sunBody.getAtmosphere());
    }

    @Override
    public StarBody.StarBuilder decodeBuffer(StarBody.StarBuilder sunBody, FriendlyByteBuf byteBuf) {
        super.decodeBuffer(sunBody, byteBuf);
        sunBody.setName(NetworkEncoders.readASCII(byteBuf));
        sunBody.setRadius(byteBuf.readDouble());
        sunBody.setMass(byteBuf.readDouble());
        sunBody.setAtmosphericEffects(NetworkEncoders.readPlanetAtmosphere(byteBuf));
        return sunBody;
    }

    @Override
    public StarBody.StarBuilder readCelestialBodyDatapack(StarBody.StarBuilder body, String name, JsonObject jsonObj, Map<String, String[]> tempChildPlanetsMap) {
        body.setName(name);
        body.setRadius(jsonObj.get("radius").getAsDouble());
        body.setMass(jsonObj.get("mass").getAsDouble());

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
