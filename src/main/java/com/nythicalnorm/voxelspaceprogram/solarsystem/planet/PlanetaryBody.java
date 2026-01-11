
package com.nythicalnorm.voxelspaceprogram.solarsystem.planet;

import com.nythicalnorm.voxelspaceprogram.solarsystem.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PlanetaryBody extends Orbit {
    private final double radius;
    private final double mass;
    private final AxisAngle4f NorthPoleDir;
    private final float RotationPeriod;
    private final PlanetAtmosphere atmoshpericEffects;

    private final ResourceKey<Level> dimension;
    private double SOI;

    public PlanetaryBody (@Nullable OrbitalElements orbitalElements, @Nullable ResourceKey<Level> dimension, PlanetAtmosphere effects, HashMap<String, Orbit>  childBodies,
                          double radius, double mass, float inclinationAngle, float startingRot, float rotationPeriod) {
        this.orbitalElements = orbitalElements;
        this.dimension = dimension;
        this.radius = radius;
        this.RotationPeriod = Calcs.timeDoubleToLong(rotationPeriod);
        this.atmoshpericEffects = effects;
        this.childElements = childBodies;
        this.mass = mass;
        this.isStableOrbit = true;
        Vector3f normalizedNorthPoleDir = new Vector3f(0f, (float) Math.cos(inclinationAngle),(float) Math.sin(inclinationAngle));
        this.NorthPoleDir = new AxisAngle4f(startingRot, normalizedNorthPoleDir);
        relativeOrbitalPos = new Vector3d(0d, 0d, 0d);
        absoluteOrbitalPos = new Vector3d(0d, 0d, 0d);
        relativeVelocity = new Vector3d(0d, 0d, 0d);
        rotation = new Quaternionf();
    }

    private void simulate(long TimeElapsed, Vector3d parentPos) {
        if (orbitalElements != null) {
            Vector3d[] stateVectors = orbitalElements.ToCartesian(TimeElapsed);
            this.relativeOrbitalPos = stateVectors[0];
            this.relativeVelocity = stateVectors[1];

            Vector3d newAbs = new Vector3d(parentPos);
            this.absoluteOrbitalPos = newAbs.add(relativeOrbitalPos);

            float rotationAngle = NorthPoleDir.angle + (float)((2*Math.PI/RotationPeriod) * TimeElapsed);
            this.rotation = new Quaternionf().rotationTo(NorthPoleDir.x,NorthPoleDir.y,NorthPoleDir.z, 0f, 1f, 0f);
            Quaternionf rotated = new Quaternionf(new AxisAngle4f(rotationAngle, 0f, 1f, 0f));
            this.rotation.mul(rotated);
        }
    }

    public void simulatePropagate(long TimeElapsed, Vector3d parentPos, double mass) {
        simulate(TimeElapsed, parentPos);

        if (childElements != null) {
            for (Orbit body : childElements.values()) {
                body.simulatePropagate(TimeElapsed, absoluteOrbitalPos, this.mass);
            }
        }
    }

    public void UpdateSOIs() {
        if (childElements != null) {
            for (Orbit orbitBody : childElements.values()) {
                if (orbitBody instanceof PlanetaryBody body) {
                    double soi = Math.pow(body.mass/this.mass, 0.4d);
                    soi = soi * body.orbitalElements.SemiMajorAxis;
                    body.setSphereOfInfluence(soi);
                    body.UpdateSOIs();
                }
            }
        }
    }

    public void setChildAddresses(HashMap<String, Stack<String>> allPlanetsAddresses, Stack<String> currentAddress, String name) {
        currentAddress.push(name);
        this.id = name;
        allPlanetsAddresses.put(name, currentAddress);

        for (Map.Entry<String, Orbit> orbitBody : childElements.entrySet()) {
            if (orbitBody.getValue() instanceof PlanetaryBody plntBody) {
                plntBody.setChildAddresses(allPlanetsAddresses, (Stack<String>) currentAddress.clone(), orbitBody.getKey());
            }
        }
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public double getRadius(){
        return radius;
    }

    public float getRotationPeriod() {
        return RotationPeriod;
    }

    public PlanetAtmosphere getAtmosphere() {
        return atmoshpericEffects;
    }

    public AxisAngle4f getNorthPoleDir() {
        return new AxisAngle4f(NorthPoleDir);
    }

    public double getAccelerationDueToGravity() {
        double val = OrbitalElements.UniversalGravitationalConstant*this.mass;
        return val/(radius*radius);
    }

    public double getSphereOfInfluence() {
        return SOI;
    }

    public void setSphereOfInfluence(double SOI) {
        this.SOI = SOI;
    }

    public double getAtmosphereRadius() {
        if (!this.atmoshpericEffects.hasAtmosphere()) {
            return 0;
        }
        return this.atmoshpericEffects.getAtmosphereHeight() + this.radius;
    }

    public double getMass() {
        return this.mass;
    }

    protected void calculateOrbitalPeriod() {
        if (childElements != null) {
            for (Orbit orbitBody : childElements.values()) {
                if (orbitBody instanceof PlanetaryBody body) {
                    if (orbitBody.getOrbitalElements() != null) {
                        orbitBody.getOrbitalElements().setOrbitalPeriod(this.mass);
                    }
                    body.calculateOrbitalPeriod();
                }
            }
        }
    }

    protected void setChildrenParents() {
        if (childElements != null) {
            for (Orbit orbitBody : childElements.values()) {
                orbitBody.setParent(this);
                if (orbitBody instanceof PlanetaryBody planetaryBody) {
                    planetaryBody.setChildrenParents();
                }
            }
        }
    }
}
