package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.network.NetworkEncoders;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.OrbitId;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public abstract class OrbitCodec<T extends Orbit> {
    public void encodeBuffer(T orbit, FriendlyByteBuf byteBuf) {
        String typeName = CelestialBodyTypes.getOrbitalBodyTypeName(orbit);
        byteBuf.writeVarInt(typeName.length());
        byteBuf.writeCharSequence(typeName, StandardCharsets.US_ASCII);
        orbit.id.encodeToBuffer(byteBuf);
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

    public T decodeBuffer (T orbit, FriendlyByteBuf byteBuf) {
        orbit.id = new OrbitId(byteBuf);
        orbit.relativeOrbitalPos = NetworkEncoders.readVector3d(byteBuf);
        orbit.absoluteOrbitalPos = NetworkEncoders.readVector3d(byteBuf);
        orbit.relativeVelocity = NetworkEncoders.readVector3d(byteBuf);
        orbit.rotation = byteBuf.readQuaternion();

        if (byteBuf.readBoolean()) {
            orbit.setOrbitalElements(NetworkEncoders.readOrbitalElements(byteBuf));
        }

        orbit.setStableOrbit(byteBuf.readBoolean());

        return orbit;
    }
}
