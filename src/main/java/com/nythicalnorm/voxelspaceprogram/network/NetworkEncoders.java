package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.*;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector3d;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class NetworkEncoders {

    public static void writeOrbitalBody(FriendlyByteBuf friendlyByteBuf, Orbit orbitalBody) {
        orbitalBody.getType().encodeToBuffer(orbitalBody, friendlyByteBuf);
    }

    public static Orbit readOrbitalBody(FriendlyByteBuf friendlyByteBuf) {
        return CelestialBodyTypes.getType(readASCII(friendlyByteBuf)).decodeFromBuffer(friendlyByteBuf);
    }

    public static Orbit readOrbitalBodyClient(FriendlyByteBuf friendlyByteBuf) {
        return CelestialBodyTypes.getType(readASCII(friendlyByteBuf)).decodeFromBufferToClient(friendlyByteBuf);
    }

    public static void writePlanetaryBodyList(FriendlyByteBuf friendlyByteBuf, List<PlanetaryBody> bodyList) {
        friendlyByteBuf.writeVarInt(bodyList.size());

        for (PlanetaryBody orbitBody : bodyList) {
            NetworkEncoders.writeOrbitalBody(friendlyByteBuf, orbitBody);
            List<OrbitId> planetChildBodiesIDs = new ArrayList<>();

            for (Orbit childBody : orbitBody.getChildren()) {
                if (childBody instanceof PlanetaryBody planetaryBody) {
                    planetChildBodiesIDs.add(planetaryBody.getOrbitId());
                }
            }

            friendlyByteBuf.writeVarInt(planetChildBodiesIDs.size());
            for (OrbitId childBodyID : planetChildBodiesIDs) {
                childBodyID.encodeToBuffer(friendlyByteBuf);
            }
        }
    }

    public static List<PlanetaryBody> readPlanetaryBodyList(FriendlyByteBuf friendlyByteBuf) {
        int planetNo = friendlyByteBuf.readVarInt();
        Map<OrbitId, TempPlanetaryHolder> tempPlanetHolderMap = new Object2ObjectOpenHashMap<>();

        for (int i = 0; i < planetNo; i++) {
            if (NetworkEncoders.readOrbitalBody(friendlyByteBuf) instanceof PlanetaryBody planetaryBody) {
                List<OrbitId> childPlanets = new ArrayList<>();
                int childSize = friendlyByteBuf.readVarInt();

                for (int j = 0; j < childSize; j++) {
                    OrbitId childOrbitId = new OrbitId(friendlyByteBuf);
                    childPlanets.add(childOrbitId);
                }

                tempPlanetHolderMap.put(planetaryBody.getOrbitId(), new TempPlanetaryHolder(planetaryBody, childPlanets));
            }
        }
        // setting the parent references
        for (TempPlanetaryHolder holder : tempPlanetHolderMap.values()) {
            for (OrbitId childID : holder.orbitIdList) {
                if (tempPlanetHolderMap.containsKey(childID)) {
                   holder.planetaryBody.addChildBody(tempPlanetHolderMap.get(childID).planetaryBody);
                } else {
                    VoxelSpaceProgram.logError("unable to parse planetaryBody due to improper static bodies orbiting planets");
                }
            }
        }
        List<PlanetaryBody> bodyList = new ArrayList<>();
        tempPlanetHolderMap.forEach((orbitId, tempPlanetaryHolder) -> bodyList.add(tempPlanetaryHolder.planetaryBody));
        return bodyList;
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

    public static void writeASCII(FriendlyByteBuf friendlyByteBuf, String text) {
        friendlyByteBuf.writeVarInt(text.length());
        friendlyByteBuf.writeCharSequence(text, StandardCharsets.US_ASCII);
    }

    public static String readASCII(FriendlyByteBuf friendlyByteBuf) {
        int stringSize = friendlyByteBuf.readVarInt();
        return friendlyByteBuf.readCharSequence(stringSize, StandardCharsets.US_ASCII).toString();
    }

    private record TempPlanetaryHolder(PlanetaryBody planetaryBody, List<OrbitId> orbitIdList) {

    }
}
