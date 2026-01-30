package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.planet;

import com.nythicalnorm.voxelspaceprogram.solarsystem.*;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBodyType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;

public abstract class PlanetaryBody extends CelestialBody {
    protected final AxisAngle4f NorthPoleDir;
    protected final long RotationPeriod;

    public PlanetaryBody(PlanetBuilder planetBuilder) {
        super(planetBuilder.name, planetBuilder.radius, planetBuilder.mass, planetBuilder.atmosphericEffects, planetBuilder.dimension, planetBuilder);
        this.NorthPoleDir = planetBuilder.NorthPoleDir;
        this.RotationPeriod = planetBuilder.RotationPeriod;
    }

    @Override
    public OrbitalBodyType<? extends OrbitalBody, ? extends Builder<?>> getType() {
        return OrbitalBodyTypesHolder.PLANETARY_BODY;
    }

    @Override
    protected void simulate(long TimeElapsed, Vector3d parentPos) {
        super.simulate(TimeElapsed, parentPos);

        float rotationAngle = NorthPoleDir.angle + (float)((2*Math.PI/RotationPeriod) * (TimeElapsed % RotationPeriod));
        this.rotation.identity().rotationTo(NorthPoleDir.x,NorthPoleDir.y,NorthPoleDir.z, 0f, 1f, 0f);
        Quaternionf rotated = new Quaternionf(new AxisAngle4f(rotationAngle, 0f, 1f, 0f));
        this.rotation.mul(rotated);
    }

    public long getRotationPeriod() {
        return RotationPeriod;
    }

    public AxisAngle4f getNorthPoleDir() {
        return new AxisAngle4f(NorthPoleDir);
    }

    public static class PlanetBuilder extends OrbitalBody.Builder<PlanetaryBody> {
        private String name;
        private double radius = 1000;
        private double mass = 10E24;
        private AxisAngle4f NorthPoleDir = new AxisAngle4f();
        private long RotationPeriod = 0L;
        private PlanetAtmosphere atmosphericEffects = new PlanetAtmosphere(false, 0, 0, 0, 0.0f, 1.0f, 1.0f);
        private @Nullable ResourceKey<Level> dimension = null;

        public PlanetBuilder() {

        }

        public void setName(String name) {
            this.name = name.toLowerCase().trim();
            this.setId(OrbitId.getIdFromString(name));
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }

        public void setMass(double mass) {
            this.mass = mass;
        }

        public void setNorthPoleDir(AxisAngle4f northPoleDir) {
            this.NorthPoleDir = northPoleDir;
        }

        public void setNorthPoleDir(float rightAscension, float declination, float startingRotation) {
            rightAscension = (float) Math.toRadians(rightAscension);
            declination = (float) Math.toRadians(declination);

            Vector3f normalizedNorthPoleDir = new Vector3f();
            normalizedNorthPoleDir.x = Mth.sin(declination) * Mth.cos(rightAscension);
            normalizedNorthPoleDir.y = Mth.cos(declination);
            normalizedNorthPoleDir.z = Mth.sin(declination) * Mth.sin(rightAscension);
            NorthPoleDir = new AxisAngle4f(startingRotation, normalizedNorthPoleDir);
        }

        public void setRotationPeriod(long rotationPeriod) {
            RotationPeriod = rotationPeriod;
        }

        public void setAtmosphericEffects(PlanetAtmosphere atmosphericEffects) {
            this.atmosphericEffects = atmosphericEffects;
        }

        public void setDimension(@Nullable ResourceKey<Level> dimension) {
            this.dimension = dimension;
        }

        @Override
        public PlanetaryBody build() {
            return new ServerPlanetaryBody(this);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public PlanetaryBody buildClientSide() {
            return new ClientPlanetaryBody(this);
        }
    }
}
