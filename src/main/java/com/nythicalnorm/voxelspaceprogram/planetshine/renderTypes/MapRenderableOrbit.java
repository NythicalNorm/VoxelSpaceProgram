package com.nythicalnorm.voxelspaceprogram.planetshine.renderTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.planetshine.map.MapRenderer;
import com.nythicalnorm.voxelspaceprogram.planetshine.map.OrbitDrawer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MapRenderableOrbit extends MapRenderable {
    OrbitalBody orbitOf;

    public MapRenderableOrbit(MapRelativeState mapRelativeState, OrbitalBody orbitOf, OrbitalBody parentBody) {
        super(mapRelativeState, parentBody);
        this.orbitOf = orbitOf;
    }

    @Override
    public Vector3f render(PoseStack poseStack, Matrix4f projectionMatrix) {
        OrbitDrawer.drawOrbit(orbitOf, MapRenderer.SCALE_FACTOR, poseStack, projectionMatrix);
        return null;
    }
}
