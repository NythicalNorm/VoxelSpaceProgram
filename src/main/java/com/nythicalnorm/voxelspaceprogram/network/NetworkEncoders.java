package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.solarsystem.*;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector3d;

import java.nio.charset.StandardCharsets;

public class NetworkEncoders {

    public static void writeOrbitalBody(FriendlyByteBuf friendlyByteBuf, Orbit orbitalBody) {
        orbitalBody.getType().encodeToBuffer(orbitalBody, friendlyByteBuf);
    }

    public static Orbit readOrbitalBody(FriendlyByteBuf friendlyByteBuf) {
        int stringSize = friendlyByteBuf.readVarInt();
        return CelestialBodyTypes.getType(friendlyByteBuf.readCharSequence(stringSize, StandardCharsets.US_ASCII).toString()).decodeFromBuffer(friendlyByteBuf);
    }

    public static void writeOrbitalElements(FriendlyByteBuf friendlyByteBuf, OrbitalElements orbitalElements) {
        friendlyByteBuf.writeDouble(orbitalElements.SemiMajorAxis);
        friendlyByteBuf.writeDouble(orbitalElements.Eccentricity);
        friendlyByteBuf.writeLong(orbitalElements.periapsisTime);

        friendlyByteBuf.writeDouble(orbitalElements.Inclination);
        friendlyByteBuf.writeDouble(orbitalElements.ArgumentOfPeriapsis);
        friendlyByteBuf.writeDouble(orbitalElements.LongitudeOfAscendingNode);
    }

    public static OrbitalElements readOrbitalElements(FriendlyByteBuf friendlyByteBuf) {
        return new OrbitalElements(
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readLong(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble()
        );
    }

    public static void writeVector3d(FriendlyByteBuf buffer, Vector3d pVector3f) {
        buffer.writeDouble(pVector3f.x());
        buffer.writeDouble(pVector3f.y());
        buffer.writeDouble(pVector3f.z());
    }

    public static Vector3d readVector3d(FriendlyByteBuf buffer) {
        return new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }
}
