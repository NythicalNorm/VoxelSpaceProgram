package com.nythicalnorm.voxelspaceprogram.solarsystem.orbits;

import com.google.gson.JsonObject;
import com.nythicalnorm.voxelspaceprogram.network.NetworkEncoders;
import com.nythicalnorm.voxelspaceprogram.solarsystem.CelestialBodyTypes;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Map;

public abstract class OrbitCodec<T extends OrbitalBody, M extends OrbitalBody.Builder<T>> {
    public void encodeBuffer(T orbit, FriendlyByteBuf byteBuf) {
        String typeName = CelestialBodyTypes.getOrbitalBodyTypeName(orbit);
        NetworkEncoders.writeASCII(byteBuf, typeName);
        orbit.id.encodeToBuffer(byteBuf);
        byteBuf.writeComponent(orbit.displayName);
        NetworkEncoders.writeVector3d(byteBuf, orbit.getRelativePos());
        NetworkEncoders.writeVector3d(byteBuf, orbit.getAbsolutePos());
        NetworkEncoders.writeVector3d(byteBuf, orbit.getRelativeVelocity());
        byteBuf.writeQuaternion(orbit.getRotation());

        if (orbit.getOrbitalElements() == null) {
            byteBuf.writeBoolean(false);
        }
        else {
            byteBuf.writeBoolean(true);
            NetworkEncoders.writeOrbitalElements(byteBuf, orbit.getOrbitalElements());
        }
        byteBuf.writeBoolean(orbit.isStableOrbit());
//        if (orbit.getParent() == null) {
//            byteBuf.writeBoolean(false);
//        } else {
//            byteBuf.writeBoolean(true);
//            orbit.id.encodeToBuffer(byteBuf);
//        }
    }

    public M decodeBuffer (M orbit, FriendlyByteBuf byteBuf) {
        orbit.setId(new OrbitId(byteBuf));
        orbit.setDisplayName(byteBuf.readComponent());
        orbit.setRelativeOrbitalPos(NetworkEncoders.readVector3d(byteBuf));
        orbit.setAbsoluteOrbitalPos(NetworkEncoders.readVector3d(byteBuf));
        orbit.setRelativeVelocity(NetworkEncoders.readVector3d(byteBuf));
        orbit.setRotation(byteBuf.readQuaternion());

        if (byteBuf.readBoolean()) {
            orbit.setOrbitalElements(NetworkEncoders.readOrbitalElements(byteBuf));
        }

        orbit.setStableOrbit(byteBuf.readBoolean());

        return orbit;
    }

    public M readCelestialBodyDatapack(M body, String name, JsonObject jsonObj, Map<String, String[]> tempChildPlanetsMap) {
        return null;
    }
}
