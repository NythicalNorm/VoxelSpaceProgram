package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBodyCodec;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.StarBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.StarBodyCodec;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBodyType;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntityBodySpacecraftCodec;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class CelestialBodyTypes {
    private static final Map<String, OrbitalBodyType<?>> AllCelestialBodyTypes = new Object2ObjectOpenHashMap<>();

    public static final OrbitalBodyType<PlanetaryBody> PLANETARY_BODY = registerOrbitalBody(new OrbitalBodyType<>("planet", new PlanetaryBodyCodec(), PlanetaryBody::new));

    public static final OrbitalBodyType<StarBody> STAR_BODY = registerOrbitalBody(new OrbitalBodyType<>("star", new StarBodyCodec(), StarBody::new));

    public static final OrbitalBodyType<EntitySpacecraftBody> ENTITY_SPACECRAFT_BODY = registerOrbitalBody(new OrbitalBodyType<>("entity_spacecraft", new EntityBodySpacecraftCodec(), EntitySpacecraftBody::new));

    public static <T extends Orbit> OrbitalBodyType<T> registerOrbitalBody(OrbitalBodyType<T> orbitalBodyType) {
        AllCelestialBodyTypes.put(orbitalBodyType.getTypeName(), orbitalBodyType);
        return orbitalBodyType;
    }

    public static OrbitalBodyType <? extends Orbit> getType(String name) {
        OrbitalBodyType<? extends Orbit> orbitalBodyType = AllCelestialBodyTypes.get(name);
        return orbitalBodyType;
    }

    public static String getOrbitalBodyTypeName(Orbit orbitalBody) {
        return orbitalBody.getType().getTypeName();
    }
}
