package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetaryBody;
import org.joml.Vector3d;

public class RenderableObjects {
    private final PlanetaryBody body;
    private Vector3d differenceVector;
    private double distanceSquared;

    public RenderableObjects(PlanetaryBody body) {
        this.body = body;
        this.distanceSquared = Double.POSITIVE_INFINITY;
    }

    public PlanetaryBody getBody() {
        return body;
    }

    public double getDistanceSquared() {
        return distanceSquared;
    }

    public double getDistance() {
        return Math.sqrt(distanceSquared);
    }

    public void setDistanceSquared(double distanceSquared) {
        this.distanceSquared = distanceSquared;
    }

    public Vector3d getDifferenceVector() {
        return differenceVector;
    }

    public void setDifferenceVector(Vector3d differenceVector) {
        this.differenceVector = differenceVector;
    }
}
