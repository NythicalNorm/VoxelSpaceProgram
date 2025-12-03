
package com.nythicalnorm.nythicalSpaceProgram.orbit;

import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetAtmosphere;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PlanetaryBody extends Orbit {
    //private final String[] childBodies;
    private final double radius;
    private final double mass;
    private final AxisAngle4f NorthPoleDir;
    private final float RotationPeriod;
    private final PlanetAtmosphere atmoshpericEffects;
    public ResourceLocation texture; //temp val

    private double SOI;

    public PlanetaryBody (@Nullable OrbitalElements orbitalElements, PlanetAtmosphere effects, HashMap<String, Orbit>  childBodies,
                          double radius, double mass, float inclinationAngle, float startingRot, float rotationPeriod, ResourceLocation texture) {
        this.orbitalElements = orbitalElements;
        this.radius = radius;
        this.texture = texture;
        this.RotationPeriod = rotationPeriod;
        this.atmoshpericEffects = effects;
        this.childElements = childBodies;
        this.mass = mass;
        this.isStableOrbit = true;
        Vector3f normalizedNorthPoleDir = new Vector3f(0f, (float) Math.cos(inclinationAngle),(float) Math.sin(inclinationAngle));
        this.NorthPoleDir = new AxisAngle4f(startingRot, normalizedNorthPoleDir);
        relativeOrbitalPos = new Vector3d(0d, 0d, 0d);
        absoluteOrbitalPos = new Vector3d(0d, 0d, 0d);
        rotation = new Quaternionf();
    }

    private void simulate(double TimeElapsed, Vector3d parentPos) {
        if (orbitalElements != null) {
            this.relativeOrbitalPos = orbitalElements.ToCartesian(TimeElapsed);
            Vector3d newAbs = new Vector3d(parentPos);
            this.absoluteOrbitalPos = newAbs.add(relativeOrbitalPos);

            float rotationAngle = NorthPoleDir.angle + (float)((2*Math.PI/RotationPeriod)*TimeElapsed);
            this.rotation = new Quaternionf().rotationTo(NorthPoleDir.x,NorthPoleDir.y,NorthPoleDir.z, 0f, 1f, 0f);
            Quaternionf rotated = new Quaternionf(new AxisAngle4f(rotationAngle, 0f, 1f, 0f));
            this.rotation.mul(rotated);
        }
    }

    public void simulatePropagate(double TimeElapsed, Vector3d parentPos, double mass) {
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
        allPlanetsAddresses.put(name, currentAddress);

        for (Map.Entry<String, Orbit> orbitBody : childElements.entrySet()) {
            if (orbitBody.getValue() instanceof PlanetaryBody) {
                PlanetaryBody plntBody = (PlanetaryBody) orbitBody.getValue();
                plntBody.setChildAddresses(allPlanetsAddresses, (Stack<String>) currentAddress.clone(), orbitBody.getKey());
            }
        }
    }

    public void addChildSpacecraft(String key, EntityOrbitalBody orbitData) {
        this.childElements.put(key ,orbitData);
    }

    public void removeChild(String oldAddress) {
        this.childElements.remove(oldAddress);
    }
    public double getRadius(){
        return radius;
    }

    public float getRotationPeriod() {
        return RotationPeriod;
    }

    public PlanetAtmosphere getAtmoshpere() {
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
                    if (orbitBody.orbitalElements != null) {
                        orbitBody.orbitalElements.setOrbitalPeriod(this.mass);
                    }
                    body.calculateOrbitalPeriod();
                }
            }
        }
    }
}
