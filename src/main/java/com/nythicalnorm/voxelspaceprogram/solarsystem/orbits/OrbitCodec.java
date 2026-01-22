package com.nythicalnorm.voxelspaceprogram.solarsystem.orbits;

import com.google.gson.JsonObject;
import com.nythicalnorm.voxelspaceprogram.network.NetworkEncoders;
import com.nythicalnorm.voxelspaceprogram.solarsystem.CelestialBodyTypes;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.storage.NBTEncoders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

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

    public CompoundTag encodeNBT(T orbit) {
        CompoundTag tag = new CompoundTag();
        String typeName = CelestialBodyTypes.getOrbitalBodyTypeName(orbit);
        tag.putString("type_name", typeName);

        orbit.id.encodeToNBT(tag);

        String componentString = Component.Serializer.toJson(orbit.displayName);
        tag.putString("display_name", componentString);

        tag.put("relative_pos", NBTEncoders.putVector3d(orbit.getRelativePos()));
        tag.put("absolute_pos", NBTEncoders.putVector3d(orbit.getAbsolutePos()));
        tag.put("relative_velocity", NBTEncoders.putVector3d(orbit.getRelativeVelocity()));
        tag.put("rotation", NBTEncoders.putQuaternionf(orbit.getRotation()));

        if (orbit.getOrbitalElements() != null) {
            tag.put("orbital_elements", NBTEncoders.putOrbitalElements(orbit.orbitalElements));
        }
        tag.putBoolean("is_stable", orbit.isStableOrbit);
        return tag;
    }

    public M decodeNBT(M orbit, CompoundTag tag) {
        orbit.setId(new OrbitId(tag));
        Component displayName = Component.Serializer.fromJson(tag.getString("display_name"));
        orbit.setDisplayName(displayName);

        orbit.setRelativeOrbitalPos(NBTEncoders.getVector3d(tag.getCompound("relative_pos")));
        orbit.setAbsoluteOrbitalPos(NBTEncoders.getVector3d(tag.getCompound("absolute_pos")));
        orbit.setRelativeVelocity(NBTEncoders.getVector3d(tag.getCompound("relative_velocity")));
        orbit.setRotation(NBTEncoders.getQuaternionf(tag.getCompound("rotation")));

        if (tag.contains("orbital_elements")) {
            orbit.setOrbitalElements(NBTEncoders.getOrbitalElements(tag.getCompound("orbital_elements")));
        }

        orbit.setStableOrbit(tag.getBoolean("is_stable"));

        return orbit;
    }

    public M readCelestialBodyDatapack(M body, String name, JsonObject jsonObj, Map<String, String[]> tempChildPlanetsMap) {
        return null;
    }
}
