package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.planet.PlanetAtmosphere;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.planet.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public abstract class CelestialBody extends OrbitalBody {
    protected final String name;
    protected final double radius;
    protected final double mass;
    protected final PlanetAtmosphere atmosphericEffects;
    protected final @Nullable ResourceKey<Level> dimension;

    //calculated on load
    private double SOI;

    public CelestialBody(String name, double radius, double mass, PlanetAtmosphere atmosphericEffects, @Nullable ResourceKey<Level> dimension, OrbitalBody.Builder<?> bodyBuilder) {
        super(bodyBuilder);
        this.name = name;
        this.displayName = Component.translatable(String.format("voxelspaceprogram.planets.%s", name));
        this.radius = radius;
        this.mass = mass;
        this.atmosphericEffects = atmosphericEffects;
        this.dimension = dimension;
        this.childElements = new Object2ObjectOpenHashMap<>();
    }

    public String getName() {
        return name;
    }

    public @Nullable ResourceKey<Level> getDimension() {
        return dimension;
    }

    protected void simulate(long TimeElapsed, Vector3d parentPos) {
        if (orbitalElements != null) {
            Vector3d[] stateVectors = orbitalElements.ToCartesian(TimeElapsed);
            this.relativeOrbitalPos = stateVectors[0];
            this.relativeVelocity = stateVectors[1];

            Vector3d newAbs = new Vector3d(parentPos);
            this.absoluteOrbitalPos = newAbs.add(relativeOrbitalPos);
        }
    }

    public void simulatePropagate(long TimeElapsed, Vector3d parentPos, double mass) {
        simulate(TimeElapsed, parentPos);

        if (childElements != null) {
            for (OrbitalBody body : childElements.values()) {
                body.simulatePropagate(TimeElapsed, absoluteOrbitalPos, this.mass);
            }
        }
    }

    public void UpdateSOIs() {
        if (childElements != null) {
            for (OrbitalBody orbitBody : childElements.values()) {
                if (orbitBody instanceof CelestialBody body && body.orbitalElements != null) {
                    double soi = Math.pow(body.mass/this.mass, 0.4d);
                    soi = soi * body.orbitalElements.SemiMajorAxis;
                    body.setSphereOfInfluence(soi);
                    body.UpdateSOIs();
                }
            }
        }
    }

    public double getRadius(){
        return radius;
    }

    public PlanetAtmosphere getAtmosphere() {
        return atmosphericEffects;
    }

    public double getAccelerationDueToGravity() {
        double val = OrbitalElements.UniversalGravitationalConstant*this.mass;
        return val/(radius*radius);
    }

    public double getEntityAccelerationDueToGravity() {
        return getAccelerationDueToGravity() * 0.1d * 0.08d;
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
            for (OrbitalBody orbitBody : childElements.values()) {
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
            for (OrbitalBody orbitBody : childElements.values()) {
                orbitBody.setParent(this);
                if (orbitBody instanceof PlanetaryBody planetaryBody) {
                    planetaryBody.setChildrenParents();
                }
            }
        }
    }
}
