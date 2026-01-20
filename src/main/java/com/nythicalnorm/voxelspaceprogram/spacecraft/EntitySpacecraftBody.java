package com.nythicalnorm.voxelspaceprogram.spacecraft;

import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.spacecraft.physics.PhysicsContext;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public abstract class EntitySpacecraftBody extends Orbit {
    protected Vector3f angularVelocity;
    protected boolean velocityChangedLastFrame;
    private static final float tolerance = 1e-8f;

    public EntitySpacecraftBody() {
        this.angularVelocity = new Vector3f();
        this.orbitalElements = new OrbitalElements(0d,0d, 0L, 0d, 0d, 0d);
    }

    public void simulatePropagate(long TimeElapsed, Vector3d parentPos, double mass) {
        if (this.orbitalElements == null) {
            return;
        }

        if (!velocityChangedLastFrame) {
            Vector3d[] stateVectors = orbitalElements.ToCartesian(TimeElapsed);
            this.relativeOrbitalPos = stateVectors[0];
            this.relativeVelocity = stateVectors[1];
        } else {
            orbitalElements.fromCartesian(this.relativeOrbitalPos, this.relativeVelocity, TimeElapsed);
            velocityChangedLastFrame = false;
        }
        absoluteOrbitalPos = new Vector3d(parentPos).add(relativeOrbitalPos);
        updateRotationFromVelocity();
    }

    private void updateRotationFromVelocity() {
        if (angularVelocity.x > tolerance || angularVelocity.y > tolerance || angularVelocity.z > tolerance) {
            Quaternionf rotationalVel = new Quaternionf(angularVelocity.x, angularVelocity.y, angularVelocity.z, 0f);
            //No Idea how this is going to work for Players ??? \_(ツ)_/
            //this.rotation.mul(rotationalVel.mul(0.5f));
        }
    }

    public Vector3f getAngularVelocity() {
        return new Vector3f(angularVelocity);
    }

    public void setVelocityForUpdate(Vector3d velocity, Vector3f angularVelocity) {
        this.relativeVelocity = velocity;
        this.angularVelocity = angularVelocity;
        velocityChangedLastFrame = true;
    }

    public void processMovement(SpacecraftControlState state) {
        this.relativeOrbitalPos = state.relativePos;
        this.relativeVelocity = state.relativeVelocity;
        this.angularVelocity = state.angularVelocity;
        this.rotation = state.rotation;
        velocityChangedLastFrame = true;
    }

    public abstract PhysicsContext getPhysicsContext();
}
