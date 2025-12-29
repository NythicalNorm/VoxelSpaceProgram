package com.nythicalnorm.nythicalSpaceProgram.orbit;

import com.nythicalnorm.nythicalSpaceProgram.network.NetworkEncoders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

public abstract class Orbit {
    protected Vector3d relativeOrbitalPos;
    protected Vector3d absoluteOrbitalPos;
    protected Vector3d relativeVelocity;
    protected Quaternionf rotation;
    protected OrbitalElements orbitalElements;
    protected HashMap<String, Orbit> childElements;
    protected Orbit parent;
    protected boolean isStableOrbit;

    public Vector3d getRelativePos() {
        return new Vector3d(relativeOrbitalPos);
    }

    public Vector3d getAbsolutePos() {
        return new Vector3d(absoluteOrbitalPos);
    }

    public Vector3d getRelativeVelocity() {
        return new Vector3d(relativeVelocity);
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setParent(Orbit parent) {
        this.parent = parent;
    }

    public Orbit getParent() {
        return parent;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
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

    public Orbit getChild(String name) {
        return childElements.get(name) ;
    }

    public Collection<Orbit> getChildren() {
        if (childElements != null) {
            if (!childElements.isEmpty()) {
                return childElements.values();
            }
        }
        return null;
    }

    public void setOrbitalElements(OrbitalElements orbitalElements) {
        this.orbitalElements = orbitalElements;
    }

    public OrbitalElements getOrbitalElements() {
        return orbitalElements;
    }

    public boolean hasChild(Orbit body) {
        if (childElements != null) {
            if (!childElements.isEmpty()) {
                return childElements.containsValue(body);
            }
        }
        return false;
    }

    public double getRelativePosDistance() {
        return this.relativeOrbitalPos.length();
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

        NetworkEncoders.writeOrbitalElements(buffer, this.orbitalElements);
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

        this.orbitalElements = NetworkEncoders.readOrbitalElements(buffer);
    }
}
