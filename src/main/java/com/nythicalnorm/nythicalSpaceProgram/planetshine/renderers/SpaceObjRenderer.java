package com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers;

import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.planet.Planets;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.CelestialStateSupplier;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.RenderableObjects;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

import java.util.Arrays;
import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public class SpaceObjRenderer {
    private static final float InWorldPlanetsDistance = 64f;
    private static RenderableObjects[] renderPlanets;

    public static Boolean PopulateRenderPlanets() {
        if (Planets.PLANETARY_BODIES != null) {
            renderPlanets = new RenderableObjects[Planets.PLANETARY_BODIES.size()];
            int i = 0;
            for (PlanetaryBody elementVariable : Planets.PLANETARY_BODIES.values()) {
                renderPlanets[i] = new RenderableObjects(elementVariable);
                i++;
            }
            return true;
        }
        return false;
    }

    public static void renderPlanetaryBodies(PoseStack poseStack, Minecraft mc, CelestialStateSupplier css, Camera camera, Matrix4f projectionMatrix, float partialTick) {
        double currentTimeElapsed = css.UpdatePlanetaryBodies();
        poseStack.pushPose();

        for (RenderableObjects obj : renderPlanets) {
            Vector3d differenceVector = new Vector3d(obj.getBody().getPlanetAbsolutePos());
            differenceVector.sub(css.getPlayerAbsolutePositon());
            obj.setDifferenceVector(differenceVector);
            obj.setDistanceSquared(css.getPlayerAbsolutePositon().distanceSquared(obj.getBody().getPlanetAbsolutePos()));
        }

        Arrays.sort(renderPlanets, Comparator.comparingDouble(RenderableObjects::getDistanceSquared).reversed());

        for (RenderableObjects plnt : renderPlanets) {
            PlanetRenderer.render(plnt, css, poseStack, projectionMatrix, currentTimeElapsed);
        }

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