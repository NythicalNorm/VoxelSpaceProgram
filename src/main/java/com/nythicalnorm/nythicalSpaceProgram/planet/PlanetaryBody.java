
package com.nythicalnorm.nythicalSpaceProgram.planet;

import com.nythicalnorm.nythicalSpaceProgram.solarsystem.OrbitalElements;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3fc;

public class PlanetaryBody {
    private final OrbitalElements orbitalElements;
    private final String parentBody;
    private final double radius;
    private final double mass;
    private final AxisAngle4f NorthPoleDir;
    private final float RotationPeriod;
    private final AtmosphericEffects atmoshpericEffects;
    public ResourceLocation texture; //temp val


    public PlanetaryBody (OrbitalElements orbitalElements, AtmosphericEffects effects, @Nullable String ParentBody, double radius, double mass, Vector3fc normalizedNorthPoleDir, float startingRot, float rotationPeriod, ResourceLocation texture) {
        this.orbitalElements = orbitalElements;
        this.radius = radius;
        this.texture = texture;
        this.NorthPoleDir = new AxisAngle4f(startingRot, normalizedNorthPoleDir);
        this.RotationPeriod = rotationPeriod;
        this.atmoshpericEffects = effects;
        this.parentBody = ParentBody;
        this.mass = mass;
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

    public AtmosphericEffects getAtmoshpericEffects() {
        return atmoshpericEffects;
    }

    public double getAccelerationDueToGravity() {
        double val = OrbitalElements.universalGravitationalConstant*this.mass;
        return val/(radius*radius);
    }
}
