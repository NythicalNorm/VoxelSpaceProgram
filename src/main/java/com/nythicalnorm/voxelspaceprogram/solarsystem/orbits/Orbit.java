package com.nythicalnorm.voxelspaceprogram.solarsystem.orbits;

import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.Collection;
import java.util.Map;

public abstract class Orbit {
    protected OrbitId id;
    protected Component displayName = Component.empty();
    protected Vector3d relativeOrbitalPos = new Vector3d();
    protected Vector3d absoluteOrbitalPos = new Vector3d();
    protected Vector3d relativeVelocity = new Vector3d();
    protected Quaternionf rotation = new Quaternionf();
    protected @Nullable OrbitalElements orbitalElements;
    protected Map<OrbitId, Orbit> childElements;
    protected @Nullable Orbit parent; // Nullable only in the case of the sun
    protected boolean isStableOrbit = false;

    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    public OrbitId getOrbitId() {
        return id;
    }

    public abstract OrbitalBodyType<? extends Orbit> getType();

    public boolean isStableOrbit() {
        return isStableOrbit;
    }

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

    public void setParent(@Nullable Orbit parent) {
        this.parent = parent;
    }

    public @Nullable Orbit getParent() {
        return parent;
    }

    public void setStableOrbit(boolean stableOrbit) {
        isStableOrbit = stableOrbit;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public abstract void simulatePropagate(long TimeElapsed, Vector3d parentPos, double parentMass);

    public Orbit getChild(OrbitId name) {
        return childElements.get(name) ;
    }

    public void addChildBody(Orbit orbitData) {
        orbitData.setParent(this);
        this.childElements.put(orbitData.getOrbitId(), orbitData);
    }

    public void removeChild(OrbitId oldAddress) {
        this.childElements.remove(oldAddress);
    }

    public Collection<Orbit> getChildren() {
        if (childElements != null) {
            return childElements.values();
        }
        return null;
    }

    public void setOrbitalElements(@Nullable OrbitalElements orbitalElements) {
        this.orbitalElements = orbitalElements;
    }

    public @Nullable OrbitalElements getOrbitalElements() {
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

    public void removeYourself() {
        removeParent();
    }

    public void removeParent() {
        if (parent != null) {
            if (parent.hasChild(this)) {
                parent.removeChild(this.id);
                this.parent = null;
            }
        }
    }

//    public CompoundTag saveNBT(CompoundTag nbt) {
//        nbt.putDouble("NSP.AbsoluteOrbitalPosX", this.absoluteOrbitalPos.x);
//        nbt.putDouble("NSP.AbsoluteOrbitalPosY", this.absoluteOrbitalPos.y);
//        nbt.putDouble("NSP.AbsoluteOrbitalPosZ", this.absoluteOrbitalPos.z);
//
//        nbt.putDouble("NSP.RelativeOrbitalPosX", this.relativeOrbitalPos.x);
//        nbt.putDouble("NSP.RelativeOrbitalPosY", this.relativeOrbitalPos.y);
//        nbt.putDouble("NSP.RelativeOrbitalPosZ", this.relativeOrbitalPos.z);
//
//        nbt.putFloat("NSP.OrbitalrotationX", this.rotation.x);
//        nbt.putFloat("NSP.OrbitalrotationY", this.rotation.y);
//        nbt.putFloat("NSP.OrbitalrotationZ", this.rotation.z);
//        nbt.putFloat("NSP.OrbitalrotationW", this.rotation.w);
//        return nbt;
//    }
//
//    public void loadNBT(CompoundTag nbt) {
//        this.absoluteOrbitalPos = new Vector3d(nbt.getDouble("NSP.AbsoluteOrbitalPosX"),
//                nbt.getDouble("NSP.AbsoluteOrbitalPosY"),nbt.getDouble("NSP.AbsoluteOrbitalPosZ"));
//
//        this.relativeOrbitalPos = new Vector3d(nbt.getDouble("NSP.RelativeOrbitalPosX"),
//                nbt.getDouble("NSP.RelativeOrbitalPosY"),nbt.getDouble("NSP.RelativeOrbitalPosZ"));
//
//        this.rotation = new Quaternionf(nbt.getFloat("NSP.OrbitalrotationX"),
//                nbt.getFloat("NSP.OrbitalrotationY"),nbt.getFloat("NSP.OrbitalrotationZ"),
//                nbt.getFloat("NSP.OrbitalrotationW"));
//    }
}
