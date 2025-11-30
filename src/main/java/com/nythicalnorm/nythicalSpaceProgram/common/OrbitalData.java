package com.nythicalnorm.nythicalSpaceProgram.common;

import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetDimensions;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.OrbitalElements;
import com.nythicalnorm.nythicalSpaceProgram.util.Calcs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Optional;

public abstract class OrbitalData {
    public Vector3d absoluteOrbitalPosition;
    public Vector3d relativeOrbitalPosition;
    public Quaternionf Rotation;
    public PlanetaryBody currentPlanetOn;
    public OrbitalElements currentOrbit;


    public void updatePlanetRot(Quaternionf existingRotation) {
        if (isOnPlanet()) {
            //quaternion to rotate the output of lookalong function to the correct -y direction.
            this.Rotation = new Quaternionf(new AxisAngle4f(Calcs.hPI,1f,0f,0f));
            Vector3f playerRelativePos = new Vector3f((float) relativeOrbitalPosition.x, (float) relativeOrbitalPosition.y, (float) relativeOrbitalPosition.z);
            playerRelativePos.normalize();
            Vector3f upVector = Calcs.getUpVectorForPlanetRot(new Vector3f(playerRelativePos), this);
            this.Rotation.lookAlong(playerRelativePos, upVector);
        }
    }

    public void updatePlanetPos(Level level, Vec3 position) {
        Optional<PlanetaryBody> planetOptional = PlanetDimensions.getDimPlanet(level.dimension());
        if (level != null) {
            planetOptional.ifPresentOrElse(plnt -> {
                currentPlanetOn = plnt;
                relativeOrbitalPosition = Calcs.planetDimPosToNormalizedVector(position, plnt.getRadius(), plnt.getPlanetRotation(), false);
                Vector3d newAbs = plnt.getPlanetAbsolutePos();
                absoluteOrbitalPosition = newAbs.add(relativeOrbitalPosition);
            }, () -> {
                currentPlanetOn = null;
            });
        }
    }

    public Optional<PlanetaryBody> getCurrentPlanet() {
        if (currentPlanetOn != null) {
            return Optional.of(currentPlanetOn);
        }
        else  {
            return Optional.empty();
        }
    }

    public boolean isOnPlanet()
    {
        return currentPlanetOn != null;
    }

    public Vector3d getAbsolutePositon() {
        return absoluteOrbitalPosition;
    }

    public Vector3d getRelativePositon() {
        return new Vector3d(relativeOrbitalPosition);
    }

    public Quaternionf getRotation() {
        return new Quaternionf(Rotation);
    }

    public CompoundTag saveNBT(CompoundTag nbt) {
        nbt.putDouble("NSP.AbsoluteOrbitalPosX", this.absoluteOrbitalPosition.x);
        nbt.putDouble("NSP.AbsoluteOrbitalPosY", this.absoluteOrbitalPosition.y);
        nbt.putDouble("NSP.AbsoluteOrbitalPosZ", this.absoluteOrbitalPosition.z);

        nbt.putDouble("NSP.RelativeOrbitalPosX", this.relativeOrbitalPosition.x);
        nbt.putDouble("NSP.RelativeOrbitalPosY", this.relativeOrbitalPosition.y);
        nbt.putDouble("NSP.RelativeOrbitalPosZ", this.relativeOrbitalPosition.z);

        nbt.putFloat("NSP.OrbitalRotationX", this.Rotation.x);
        nbt.putFloat("NSP.OrbitalRotationY", this.Rotation.y);
        nbt.putFloat("NSP.OrbitalRotationZ", this.Rotation.z);
        nbt.putFloat("NSP.OrbitalRotationW", this.Rotation.w);
        return nbt;
    }

    public void loadNBT(CompoundTag nbt) {
        this.absoluteOrbitalPosition = new Vector3d(nbt.getDouble("NSP.AbsoluteOrbitalPosX"),
                nbt.getDouble("NSP.AbsoluteOrbitalPosY"),nbt.getDouble("NSP.AbsoluteOrbitalPosZ"));

        this.relativeOrbitalPosition = new Vector3d(nbt.getDouble("NSP.RelativeOrbitalPosX"),
                nbt.getDouble("NSP.RelativeOrbitalPosY"),nbt.getDouble("NSP.RelativeOrbitalPosZ"));

        this.Rotation = new Quaternionf(nbt.getFloat("NSP.OrbitalRotationX"),
                nbt.getFloat("NSP.OrbitalRotationY"),nbt.getFloat("NSP.OrbitalRotationZ"),
                nbt.getFloat("NSP.OrbitalRotationW"));
    }

    public void copyFrom(@NotNull PlayerOrbitalData oldStore) {
        absoluteOrbitalPosition = oldStore.absoluteOrbitalPosition;
        relativeOrbitalPosition = oldStore.relativeOrbitalPosition;
        Rotation = oldStore.Rotation;
    }

    public void encode (FriendlyByteBuf buffer) {
        buffer.writeDouble(this.absoluteOrbitalPosition.x);
        buffer.writeDouble(this.absoluteOrbitalPosition.y);
        buffer.writeDouble(this.absoluteOrbitalPosition.z);

        buffer.writeDouble(this.relativeOrbitalPosition.x);
        buffer.writeDouble(this.relativeOrbitalPosition.y);
        buffer.writeDouble(this.relativeOrbitalPosition.z);

        buffer.writeFloat(this.Rotation.x);
        buffer.writeFloat(this.Rotation.y);
        buffer.writeFloat(this.Rotation.z);
        buffer.writeFloat(this.Rotation.w);
    }

    public void decode (FriendlyByteBuf buffer) {
        this.absoluteOrbitalPosition.x = buffer.readDouble();
        this.absoluteOrbitalPosition.y = buffer.readDouble();
        this.absoluteOrbitalPosition.z = buffer.readDouble();

        this.relativeOrbitalPosition.x = buffer.readDouble();
        this.relativeOrbitalPosition.y = buffer.readDouble();
        this.relativeOrbitalPosition.z = buffer.readDouble();

        this.Rotation.x = buffer.readFloat();
        this.Rotation.y = buffer.readFloat();
        this.Rotation.z = buffer.readFloat();
        this.Rotation.w = buffer.readFloat();
    }

    public boolean isInOrbit() {
        return currentOrbit != null;
    }
}
