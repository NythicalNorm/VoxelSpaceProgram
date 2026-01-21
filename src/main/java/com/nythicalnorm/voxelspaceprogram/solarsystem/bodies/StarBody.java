package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import com.nythicalnorm.voxelspaceprogram.solarsystem.CelestialBodyTypes;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBodyType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3d;

public class StarBody extends CelestialBody {

    public StarBody(StarBuilder starBuilder) {
        super(starBuilder.name, starBuilder.radius, starBuilder.mass, starBuilder.atmosphericEffects, null, starBuilder);
    }

    @Override
    public OrbitalBodyType<? extends OrbitalBody, ? extends Builder<?>> getType() {
        return CelestialBodyTypes.STAR_BODY;
    }

    public void simulatePlanets(long currentTime) {
        this.simulatePropagate(currentTime, new Vector3d(0d, 0d, 0d), this.getMass());
    }

    public void initCalcs() {
        this.setSphereOfInfluence(Double.POSITIVE_INFINITY);
        this.calculateOrbitalPeriod();
        super.UpdateSOIs();
        this.parent = null;
        this.setChildrenParents();
    }

    public static class StarBuilder extends OrbitalBody.Builder<StarBody> {
        private String name;
        private double radius = 1000;
        private double mass = 10E24;
        private PlanetAtmosphere atmosphericEffects = new PlanetAtmosphere(false, 0, 0, 0, 0.0f, 1.0f, 1.0f);

        public StarBuilder() {

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

        public void setAtmosphericEffects(PlanetAtmosphere atmosphericEffects) {
            this.atmosphericEffects = atmosphericEffects;
        }

        @Override
        public StarBody build() {
            return new StarBody(this);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public StarBody buildClientSide() {
            return null;
        }
    }
}
