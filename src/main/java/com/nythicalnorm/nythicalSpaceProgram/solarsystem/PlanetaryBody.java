
package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import net.minecraft.resources.ResourceLocation;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3fc;

public class PlanetaryBody {
    private final OrbitalElements orbitalElements;
    private final double radius;
    private final AxisAngle4f NorthPoleDir;
    private final float RotationPeriod;
    public ResourceLocation texture; //temp val

    public PlanetaryBody (OrbitalElements orbitalElements, double radius, Vector3fc normalizedNorthPoleDir, float startingRot, float rotationPeriod, ResourceLocation texture) {
        this.orbitalElements = orbitalElements;
        this.radius = radius;
        this.texture = texture;
        this.NorthPoleDir = new AxisAngle4f(startingRot, normalizedNorthPoleDir);
        this.RotationPeriod = rotationPeriod;
    }

    public Vector3d CalculateCartesianPosition(double TimeElapsed) {
        return orbitalElements.ToCartesian(TimeElapsed);
    }

    public Quaternionf getRotationAt(double TimeElapsed) {
        NorthPoleDir.angle = (float)((2*Math.PI/RotationPeriod)*TimeElapsed);
        return new Quaternionf(NorthPoleDir) ;
    }

    public double getRadius(){
        return radius;
    }
}
