package com.nythicalnorm.nythicalSpaceProgram.common;

import com.nythicalnorm.nythicalSpaceProgram.solarsystem.OrbitalElements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Stack;

public abstract class Orbit {
    public Vector3d relativeOrbitalPos;
    public Vector3d absoluteOrbitalPos;
    public Quaternionf rotation;
    public OrbitalElements orbitalElements;
    public HashMap<String, Orbit> childElements;
    public boolean isStableOrbit;

    public Vector3d getRelativePos() {
        return new Vector3d(relativeOrbitalPos);
    }

    public Vector3d getAbsolutePos() {
        return new Vector3d(absoluteOrbitalPos);
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public abstract void simulatePropagate(double TimeElapsed, Vector3d parentPos, double parentMass);


    public Orbit getOrbit(Stack<String> stack) {
        if (!stack.isEmpty()) {
            String key = stack.pop();
            Orbit childElement = childElements.get(key);
            if (childElement != null) {
                return childElement.getOrbit(stack);
            }
            else {
                return null;
            }
        }
        return this;
    }

    public CompoundTag saveNBT(CompoundTag nbt) {
        nbt.putDouble("NSP.AbsoluteOrbitalPosX", this.absoluteOrbitalPos.x);
        nbt.putDouble("NSP.AbsoluteOrbitalPosY", this.absoluteOrbitalPos.y);
        nbt.putDouble("NSP.AbsoluteOrbitalPosZ", this.absoluteOrbitalPos.z);

        nbt.putDouble("NSP.RelativeOrbitalPosX", this.relativeOrbitalPos.x);
        nbt.putDouble("NSP.RelativeOrbitalPosY", this.relativeOrbitalPos.y);
        nbt.putDouble("NSP.RelativeOrbitalPosZ", this.relativeOrbitalPos.z);

        nbt.putFloat("NSP.OrbitalrotationX", this.rotation.x);
        nbt.putFloat("NSP.OrbitalrotationY", this.rotation.y);
        nbt.putFloat("NSP.OrbitalrotationZ", this.rotation.z);
        nbt.putFloat("NSP.OrbitalrotationW", this.rotation.w);
        return nbt;
    }

    public void loadNBT(CompoundTag nbt) {
        this.absoluteOrbitalPos = new Vector3d(nbt.getDouble("NSP.AbsoluteOrbitalPosX"),
                nbt.getDouble("NSP.AbsoluteOrbitalPosY"),nbt.getDouble("NSP.AbsoluteOrbitalPosZ"));

        this.relativeOrbitalPos = new Vector3d(nbt.getDouble("NSP.RelativeOrbitalPosX"),
                nbt.getDouble("NSP.RelativeOrbitalPosY"),nbt.getDouble("NSP.RelativeOrbitalPosZ"));

        this.rotation = new Quaternionf(nbt.getFloat("NSP.OrbitalrotationX"),
                nbt.getFloat("NSP.OrbitalrotationY"),nbt.getFloat("NSP.OrbitalrotationZ"),
                nbt.getFloat("NSP.OrbitalrotationW"));
    }

    public void encode (FriendlyByteBuf buffer) {
        buffer.writeDouble(this.absoluteOrbitalPos.x);
        buffer.writeDouble(this.absoluteOrbitalPos.y);
        buffer.writeDouble(this.absoluteOrbitalPos.z);

        buffer.writeDouble(this.relativeOrbitalPos.x);
        buffer.writeDouble(this.relativeOrbitalPos.y);
        buffer.writeDouble(this.relativeOrbitalPos.z);

        buffer.writeFloat(this.rotation.x);
        buffer.writeFloat(this.rotation.y);
        buffer.writeFloat(this.rotation.z);
        buffer.writeFloat(this.rotation.w);
    }

    public void decode (FriendlyByteBuf buffer) {
        this.absoluteOrbitalPos.x = buffer.readDouble();
        this.absoluteOrbitalPos.y = buffer.readDouble();
        this.absoluteOrbitalPos.z = buffer.readDouble();

        this.relativeOrbitalPos.x = buffer.readDouble();
        this.relativeOrbitalPos.y = buffer.readDouble();
        this.relativeOrbitalPos.z = buffer.readDouble();

        this.rotation.x = buffer.readFloat();
        this.rotation.y = buffer.readFloat();
        this.rotation.z = buffer.readFloat();
        this.rotation.w = buffer.readFloat();
    }
}
