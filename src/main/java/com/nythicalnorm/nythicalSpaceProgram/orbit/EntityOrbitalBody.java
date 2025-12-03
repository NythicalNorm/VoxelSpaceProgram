package com.nythicalnorm.nythicalSpaceProgram.orbit;

import org.joml.Vector3d;

public abstract class EntityOrbitalBody extends Orbit {

    public void simulatePropagate(double TimeElapsed, Vector3d parentPos, double mass) {
        orbitalElements.setOrbitalPeriod(mass);
        relativeOrbitalPos = orbitalElements.ToCartesian(TimeElapsed);
        Vector3d newAbs = new Vector3d(parentPos);
        absoluteOrbitalPos = newAbs.add(relativeOrbitalPos);
    }

    public boolean isInOrbit() {
        return orbitalElements != null;
    }
}
