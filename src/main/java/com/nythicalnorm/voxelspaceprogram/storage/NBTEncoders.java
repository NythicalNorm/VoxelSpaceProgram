package com.nythicalnorm.voxelspaceprogram.storage;

import com.nythicalnorm.voxelspaceprogram.solarsystem.CelestialBodyTypes;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class NBTEncoders {
    public static CompoundTag putVector3d(Vector3d vector) {
        CompoundTag vectorTag = new CompoundTag();
        vectorTag.putDouble("x", vector.x);
        vectorTag.putDouble("y", vector.y);
        vectorTag.putDouble("z", vector.z);
        return vectorTag;
    }

    public static Vector3d getVector3d(CompoundTag tag) {
        return new Vector3d(
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z")
        );
    }

    public static CompoundTag putVector3f(Vector3f vector) {
        CompoundTag vectorTag = new CompoundTag();
        vectorTag.putFloat("x", vector.x);
        vectorTag.putFloat("y", vector.y);
        vectorTag.putFloat("z", vector.z);
        return vectorTag;
    }

    public static Vector3f getVector3f(CompoundTag tag) {
        return new Vector3f(
                tag.getFloat("x"),
                tag.getFloat("y"),
                tag.getFloat("z")
        );
    }

    public static CompoundTag putQuaternionf(Quaternionf quaternionf) {
        CompoundTag quaternionTag = new CompoundTag();
        quaternionTag.putDouble("x", quaternionf.x);
        quaternionTag.putDouble("y", quaternionf.y);
        quaternionTag.putDouble("z", quaternionf.z);
        quaternionTag.putDouble("w", quaternionf.w);
        return quaternionTag;
    }

    public static Quaternionf getQuaternionf(CompoundTag tag) {
        return new Quaternionf(
                tag.getFloat("x"),
                tag.getFloat("y"),
                tag.getFloat("z"),
                tag.getFloat("w")
        );
    }

    public static CompoundTag putOrbitalElements(@Nullable OrbitalElements orbitalElements) {
        CompoundTag elementsTag = new CompoundTag();
        if (orbitalElements == null) {
            return elementsTag;
        }

        elementsTag.putDouble("major_axis", orbitalElements.SemiMajorAxis);
        elementsTag.putDouble("eccentricity", orbitalElements.Eccentricity);
        elementsTag.putLong("periapsis_time", orbitalElements.periapsisTime);

        elementsTag.putDouble("inclination", orbitalElements.Inclination);
        elementsTag.putDouble("argument_periapsis", orbitalElements.ArgumentOfPeriapsis);
        elementsTag.putDouble("longitude", orbitalElements.LongitudeOfAscendingNode);

        return elementsTag;
    }

    public static OrbitalElements getOrbitalElements(CompoundTag tag) {
        return new OrbitalElements(
                tag.getDouble("major_axis"),
                tag.getDouble("eccentricity"),
                tag.getLong("periapsis_time"),

                tag.getDouble("inclination"),
                tag.getDouble("argument_periapsis"),
                tag.getDouble("longitude")
        );
    }

    public static CompoundTag putOrbitalBody(OrbitalBody orbitalBody) {
       return orbitalBody.getType().encodeToNBT(orbitalBody);
    }

    public static OrbitalBody getOrbitalBody(CompoundTag tag) {
       return CelestialBodyTypes.getType(tag.getString("type_name")).decodeFromNBT(tag).build();
    }
}
