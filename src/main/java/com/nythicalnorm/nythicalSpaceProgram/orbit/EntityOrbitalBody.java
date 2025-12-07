package com.nythicalnorm.nythicalSpaceProgram.orbit;

import org.joml.Vector3d;

public abstract class EntityOrbitalBody extends Orbit {

    public void simulatePropagate(double TimeElapsed, Vector3d parentPos, double mass) {
        orbitalElements.setOrbitalPeriod(mass);
        Vector3d[] stateVectors = orbitalElements.ToCartesian(TimeElapsed);
        this.relativeOrbitalPos = stateVectors[0];
        this.relativeVelocity = stateVectors[1];
        Vector3d newAbs = new Vector3d(parentPos);
        absoluteOrbitalPos = newAbs.add(relativeOrbitalPos);
    }

    public boolean isInOrbit() {
        return orbitalElements != null;
    }
}
