package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.nythicalnorm.nythicalSpaceProgram.orbit.PlanetaryBody;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class RenderableObjects {
    private final PlanetaryBody body;
    private Vector3d differenceVector;
    private double distanceSquared;

    public RenderableObjects(PlanetaryBody body) {
        this.body = body;
        this.distanceSquared = Double.POSITIVE_INFINITY;
        differenceVector = new Vector3d();
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

    public Vector3f getNormalizedDiffVectorf() {
        return new Vector3f((float) differenceVector.x, (float)differenceVector.y, (float) differenceVector.z).normalize();
    }
}
