package com.nythicalnorm.nythicalSpaceProgram.planetshine.map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.PlanetShine;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.RenderableObjects;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.AtmosphereRenderer;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.PlanetRenderer;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.SpaceObjRenderer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.Optional;

public class MapRenderer {
    Quaternionf currentRotation;

    public static void renderSkybox(PoseStack mapPosestack, Matrix4f projectionMatrix) {
        AtmosphereRenderer.renderSpaceSky(mapPosestack, projectionMatrix);
        PlanetShine.drawStarBuffer(mapPosestack, projectionMatrix, 1.0f);
    }

    public static void renderPlanets(PoseStack poseStack, Matrix4f projectionMatrix) {
        RenderableObjects obj = SpaceObjRenderer.getRenderPlanets()[2];
        poseStack.translate(0f, 0.0f, -1.5f);
        PlanetRenderer.render(obj,false, Optional.empty(), poseStack, projectionMatrix, 0, 1.0f);

    }

}
