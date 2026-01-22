package com.nythicalnorm.voxelspaceprogram.solarsystem.orbits;

import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

public abstract class OrbitalBody {
    protected final OrbitId id;
    protected Component displayName;
    protected Vector3d relativeOrbitalPos;
    protected Vector3d absoluteOrbitalPos;
    protected Vector3d relativeVelocity;
    protected Quaternionf rotation;
    protected @Nullable OrbitalElements orbitalElements;
    protected ConcurrentMap<OrbitId, OrbitalBody> childElements;
    protected @Nullable OrbitalBody parent; // Nullable only in the case of the sun
    protected boolean isStableOrbit;

    public OrbitalBody(OrbitalBody.Builder<?> builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.relativeOrbitalPos = builder.relativeOrbitalPos;
        this.absoluteOrbitalPos = builder.absoluteOrbitalPos;
        this.relativeVelocity = builder.relativeVelocity;
        this.rotation = builder.rotation;
        this.orbitalElements = builder.orbitalElements;
        this.isStableOrbit = builder.isStableOrbit;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    public OrbitId getOrbitId() {
        return id;
    }

    public abstract OrbitalBodyType<? extends OrbitalBody, ? extends OrbitalBody.Builder<?>> getType();

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

    public void setParent(@Nullable OrbitalBody parent) {
        this.parent = parent;
    }

    public @Nullable OrbitalBody getParent() {
        return parent;
    }

    public void setStableOrbit(boolean stableOrbit) {
        isStableOrbit = stableOrbit;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }

    public abstract void simulatePropagate(long TimeElapsed, Vector3d parentPos, double parentMass);

    public OrbitalBody getChild(OrbitId name) {
        return childElements.get(name) ;
    }

    public void addChildBody(OrbitalBody orbitData) {
        orbitData.setParent(this);
        this.childElements.put(orbitData.getOrbitId(), orbitData);
    }

    public void removeChild(OrbitId oldAddress) {
        this.childElements.remove(oldAddress);
    }

    public Collection<OrbitalBody> getChildren() {
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

    public boolean hasChild(OrbitalBody body) {
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

    public abstract static class Builder<T extends OrbitalBody> {
        protected OrbitId id = null;
        protected Component displayName = Component.empty();
        protected Vector3d relativeOrbitalPos = new Vector3d();
        protected Vector3d absoluteOrbitalPos = new Vector3d();
        protected Vector3d relativeVelocity = new Vector3d();
        protected Quaternionf rotation = new Quaternionf();
        protected @Nullable OrbitalElements orbitalElements;
        protected @Nullable OrbitalBody parent; // Nullable only in the case of the sun
        protected boolean isStableOrbit = true;

        public void setId(OrbitId id) {
            this.id = id;
        }

        public void setDisplayName(Component displayName) {
            this.displayName = displayName;
        }

        public void setRelativeOrbitalPos(Vector3d relativeOrbitalPos) {
            this.relativeOrbitalPos = relativeOrbitalPos;
        }

        public void setAbsoluteOrbitalPos(Vector3d absoluteOrbitalPos) {
            this.absoluteOrbitalPos = absoluteOrbitalPos;
        }

        public void setRelativeVelocity(Vector3d relativeVelocity) {
            this.relativeVelocity = relativeVelocity;
        }

        public void setRotation(Quaternionf rotation) {
            this.rotation = rotation;
        }

        public void setOrbitalElements(@Nullable OrbitalElements orbitalElements) {
            this.orbitalElements = orbitalElements;
        }

        public void setParent(@Nullable OrbitalBody parent) {
            this.parent = parent;
        }

        public void setStableOrbit(boolean stableOrbit) {
            isStableOrbit = stableOrbit;
        }

        public abstract T build();

        @OnlyIn(Dist.CLIENT)
        public abstract T buildClientSide();
    }
}
