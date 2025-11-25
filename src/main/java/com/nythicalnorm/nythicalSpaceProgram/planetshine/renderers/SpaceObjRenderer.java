package com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers;

import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.CelestialStateSupplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

@OnlyIn(Dist.CLIENT)
public class SpaceObjRenderer {
    private static final float InWorldPlanetsDistance = 64f;

    public static void renderPlanetaryBodies(PoseStack poseStack, Minecraft mc, Camera camera, Matrix4f projectionMatrix, float partialTick) {
        double currentTimeElapsed = CelestialStateSupplier.getCurrentTimeElapsed();
        poseStack.pushPose();

        PlanetRenderer.render("bumi", poseStack, projectionMatrix, currentTimeElapsed);

        poseStack.popPose();
    }

    public static void PerspectiveShift(double PlanetDistance, Vector3d PlanetPos, Quaternionf planetRot, double bodyRadius,PoseStack poseStack){
        //tan amd atan cancel each other out.
        float planetApparentSize = (float) (InWorldPlanetsDistance * 2 * bodyRadius/PlanetDistance);
        PlanetPos.normalize();
        PlanetPos.mul(InWorldPlanetsDistance);
        poseStack.translate(PlanetPos.x,PlanetPos.y, PlanetPos.z);
        poseStack.scale(planetApparentSize, planetApparentSize, planetApparentSize);
        poseStack.mulPose(planetRot);
    }

//    private static void PerspectiveShiftZscaleSeparate(double PlanetDistance, Vector3d PlanetPos, double bodyRadius,PoseStack poseStack){
//        //double PlanetDistance = PlanetPos.distance(new Vector3d());
//        PlanetPos.normalize();
//        Vector3f relativePlanetDir = new Vector3f((float) PlanetPos.x, (float) PlanetPos.y, (float) PlanetPos.z);
//
//        //tan amd atan cancel each other out.
//        float planetApparentSize = (float) (InWorldPlanetsDistance*2*bodyRadius/(bodyRadius+PlanetDistance));
//        Quaternionf planetDirRotation = new Quaternionf();
//        planetDirRotation.rotationTo(new Vector3f(0f,0f,1f), relativePlanetDir);
//
//        poseStack.mulPose(planetDirRotation);
//        poseStack.translate(0,0, InWorldPlanetsDistance);
//        poseStack.scale(planetApparentSize, planetApparentSize, planetApparentSize);
//        planetDirRotation.rotationTo(relativePlanetDir, new Vector3f(0f,0f,1f));
//        poseStack.mulPose(planetDirRotation);
//    }
}