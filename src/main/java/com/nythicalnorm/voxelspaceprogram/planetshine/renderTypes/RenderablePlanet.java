package com.nythicalnorm.voxelspaceprogram.planetshine.renderTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.planet.PlanetAtmosphere;
import com.nythicalnorm.voxelspaceprogram.planetshine.renderers.PlanetRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.Optional;

public class RenderablePlanet extends SpaceRenderable {
    private final CelestialBody body;

    public RenderablePlanet(CelestialBody body) {
        super();
        this.body = body;
    }

    public CelestialBody getBody() {
        return body;
    }

    @Override
    public void calculatePos(OrbitalBody relativeTo) {
        Vector3d differenceVector = this.body.getAbsolutePos();
        differenceVector.sub(relativeTo.getAbsolutePos());
        setDifferenceVector(differenceVector);
        setDistance(relativeTo.getAbsolutePos().distance(this.body.getAbsolutePos()));
    }

    @Override
    public void render(Optional<PlanetAtmosphere> currentPlanetAtmosphere, PoseStack poseStack, Matrix4f projectionMatrix, float currentAlbedo) {
        PlanetRenderer.render(body, currentPlanetAtmosphere, poseStack, projectionMatrix, this.distance, currentAlbedo, this.differenceVector);
    }
}
