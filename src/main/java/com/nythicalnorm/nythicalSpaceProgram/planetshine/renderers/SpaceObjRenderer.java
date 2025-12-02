package com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers;

import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetAtmosphere;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.planet.Planets;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.CelestialStateSupplier;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.PlanetShine;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.RenderableObjects;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class SpaceObjRenderer {
    private static final float InWorldPlanetsDistance = 64f;
    private static RenderableObjects[] renderPlanets;

    public static void PopulateRenderPlanets(Planets planets) {
        Set<String> planetList = planets.getAllPlanetNames();

        renderPlanets = new RenderableObjects[planetList.size()];
        int i = 0;
        for (String elementVariable : planetList) {
            renderPlanets[i] = new RenderableObjects(planets.getPlanet(elementVariable));
            i++;
        }
    }

    public static void renderPlanetaryBodies(PoseStack poseStack, Minecraft mc, CelestialStateSupplier css, Camera camera, Matrix4f projectionMatrix, float partialTick) {
        poseStack.pushPose();

        for (RenderableObjects obj : renderPlanets) {
            Vector3d differenceVector = obj.getBody().getAbsolutePos();
            differenceVector.sub(css.getPlayerData().getAbsolutePos());
            obj.setDifferenceVector(differenceVector);
            obj.setDistanceSquared(css.getPlayerData().getAbsolutePos().distanceSquared(obj.getBody().getAbsolutePos()));
        }

        Arrays.sort(renderPlanets, Comparator.comparingDouble(RenderableObjects::getDistanceSquared).reversed());

        renderPlanets(renderPlanets, css, poseStack, projectionMatrix, partialTick);

        poseStack.popPose();
    }

    public static void renderPlanets(RenderableObjects[] renderPlanets, CelestialStateSupplier css, PoseStack poseStack, Matrix4f projectionMatrix, float partialTick) {
        Optional<PlanetaryBody> planetOn = css.getCurrentPlanet();
        float currentAlbedo = 1.0f;
        Optional<PlanetAtmosphere> atmosphere = Optional.empty();

        if (planetOn.isPresent()) {
            if (planetOn.get().getAtmoshpere().hasAtmosphere()) {
                currentAlbedo = css.getPlayerData().getSunAngle() * 2;
                atmosphere = Optional.of(planetOn.get().getAtmoshpere());
            }
        } else if (css.weInSpace()) {
            AtmosphereRenderer.renderAtmospheres(renderPlanets, poseStack, projectionMatrix);
        }

        PlanetShine.drawStarBuffer(poseStack, projectionMatrix, css);

        for (RenderableObjects plnt : renderPlanets) {
            double distance = plnt.getDistance();

            if (distance < (plnt.getBody().getRadius() + 320)) {
                continue;
            }

            PlanetRenderer.render(plnt, atmosphere, poseStack, projectionMatrix, distance, currentAlbedo);
        }
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
}