
package com.nythicalnorm.voxelspaceprogram.solarsystem.planet;

import com.nythicalnorm.voxelspaceprogram.planettexgen.biometex.PlanetTexture;
import com.nythicalnorm.voxelspaceprogram.solarsystem.*;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.HashMap;

public class PlanetaryBody extends Orbit {
    protected double radius = 1000;
    protected double mass = 10E24;
    protected AxisAngle4f NorthPoleDir = new AxisAngle4f();
    protected float RotationPeriod = 0f;
    protected PlanetAtmosphere atmosphericEffects = new PlanetAtmosphere(false, 0, 0, 0, 0.0f, 1.0f, 1.0f);
    protected @Nullable ResourceKey<Level> dimension = null;

    // calculated on load:
    private PlanetTexture planetTexture;
    private double SOI;

    public PlanetaryBody() {
    }

    public PlanetaryBody (String name, @Nullable OrbitalElements orbitalElements, @Nullable ResourceKey<Level> dimension, PlanetAtmosphere effects, HashMap<OrbitId, Orbit>  childBodies,
                          double radius, double mass, float inclinationAngle, float startingRot, float rotationPeriod) {
        initStaticName(name);
        this.orbitalElements = orbitalElements;
        this.dimension = dimension;
        this.radius = radius;
        this.RotationPeriod = Calcs.timeDoubleToLong(rotationPeriod);
        this.atmosphericEffects = effects;
        this.childElements = childBodies;
        this.mass = mass;
        this.isStableOrbit = true;
        Vector3f normalizedNorthPoleDir = new Vector3f(0f, (float) Math.cos(inclinationAngle),(float) Math.sin(inclinationAngle));
        this.NorthPoleDir = new AxisAngle4f(startingRot, normalizedNorthPoleDir);
        relativeOrbitalPos = new Vector3d(0d, 0d, 0d);
        absoluteOrbitalPos = new Vector3d(0d, 0d, 0d);
        relativeVelocity = new Vector3d(0d, 0d, 0d);
        rotation = new Quaternionf();
        //planetTexture = new ServerPlanetTexture();
    }

    public void initStaticName(String pName) {
        this.id = OrbitId.getIdFromString(pName);
        this.name = pName.toLowerCase().trim();
        this.displayName = Component.translatable(String.format("voxelspaceprogram.planets.%s", name));
    }

    @Override
    public OrbitalBodyType<? extends Orbit> getType() {
        return CelestialBodyTypes.PLANETARY_BODY;
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
                if (orbitBody instanceof PlanetaryBody body && body.orbitalElements != null) {
                    double soi = Math.pow(body.mass/this.mass, 0.4d);
                    soi = soi * body.orbitalElements.SemiMajorAxis;
                    body.setSphereOfInfluence(soi);
                    body.UpdateSOIs();
                }
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
        return atmosphericEffects;
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
        if (!this.atmosphericEffects.hasAtmosphere()) {
            return 0;
        }
        return this.atmosphericEffects.getAtmosphereHeight() + this.radius;
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
