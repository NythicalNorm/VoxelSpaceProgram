package com.nythicalnorm.voxelspaceprogram.util;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

public class CelestialBodyUtils {
    public static Vector3d getRelativePositon(Vector3dc pos, CelestialBody celestialBody) {
        Vec3 vec3Pos = new Vec3(pos.x(), pos.y(), pos.z());
        return Calcs.planetDimPosToNormalizedVector(vec3Pos, celestialBody.getRadius(), celestialBody.getRotation(), false);
    }

    public static Quaterniond getSpaceRotationFromPlanetPos(Vector3dc relativePlanetPosition, CelestialBody celestialBody) {
        Quaternionf planetRotation = new Quaternionf(new AxisAngle4d(Mth.HALF_PI,1f,0f,0f));
        Vector3f playerRelativePos = new Vector3f((float) relativePlanetPosition.x(), (float) relativePlanetPosition.y(), (float) relativePlanetPosition.z());
        playerRelativePos.normalize();
        Vector3f upVector = Calcs.getUpVectorForPlanetRot(new Vector3f(playerRelativePos), celestialBody);
        planetRotation.lookAlong(playerRelativePos, upVector);
        return new Quaterniond(planetRotation);
    }
}
