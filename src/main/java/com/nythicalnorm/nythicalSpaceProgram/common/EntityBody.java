package com.nythicalnorm.nythicalSpaceProgram.common;

import org.joml.Vector3d;

public abstract class EntityBody extends Orbit {

    public void simulatePropagate(double TimeElapsed, Vector3d parentPos, double mass) {
        orbitalElements.setOrbitalPeriod(mass);
        relativeOrbitalPos = orbitalElements.ToCartesian(TimeElapsed);
        absoluteOrbitalPos = parentPos.add(relativeOrbitalPos);
    }

    public boolean isInOrbit() {
        return orbitalElements != null;
    }
}
