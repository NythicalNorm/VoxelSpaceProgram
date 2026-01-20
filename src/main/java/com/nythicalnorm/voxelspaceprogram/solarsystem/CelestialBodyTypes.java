package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBodyCodec;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.StarBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.StarBodyCodec;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBodyType;
import com.nythicalnorm.voxelspaceprogram.spacecraft.ClientPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.PlayerSpacecraftCodec;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.ServerPlayerSpacecraftBody;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

public class CelestialBodyTypes {
    private static final Map<String, OrbitalBodyType<?>> AllCelestialBodyTypes = new Object2ObjectOpenHashMap<>();

    public static final OrbitalBodyType<PlanetaryBody> PLANETARY_BODY = registerOrbitalBody(new OrbitalBodyType<>("planet", new PlanetaryBodyCodec(), PlanetaryBody::new));

    public static final OrbitalBodyType<StarBody> STAR_BODY = registerOrbitalBody(new OrbitalBodyType<>("star", new StarBodyCodec(), StarBody::new));

    public static final OrbitalBodyType<EntitySpacecraftBody> PLAYER_SPACECRAFT_BODY = registerOrbitalBody(new OrbitalBodyType<>("player_spacecraft", new PlayerSpacecraftCodec(), ServerPlayerSpacecraftBody::new));

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

    @OnlyIn(Dist.CLIENT)
    public static class BodyTypeClientExt {
        public static final Map<String, OrbitalBodyType.Supplier<? extends Orbit>> celestialBodyClientSuppliers = Map.of(
                PLAYER_SPACECRAFT_BODY.getTypeName(), ClientPlayerSpacecraftBody::new
        );
    }
}
