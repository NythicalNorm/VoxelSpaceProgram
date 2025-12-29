package com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers;

import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.nythicalSpaceProgram.orbit.Star;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetAtmosphere;
import com.nythicalnorm.nythicalSpaceProgram.orbit.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderTypes.SpaceRenderable;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderTypes.RenderablePlanet;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.Planets;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.CelestialStateSupplier;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.PlanetShine;
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
    private static SpaceRenderable[] renderPlanets;

    public static void PopulateRenderPlanets(Planets planets) {
        Set<String> planetList = planets.getAllPlanetNames();

        renderPlanets = new SpaceRenderable[planetList.size()];
        int i = 0;
        for (String elementVariable : planetList) {
            renderPlanets[i] = new RenderablePlanet(planets.getPlanet(elementVariable));
            i++;
        }
    }

    public static void renderPlanetaryBodies(PoseStack poseStack, Minecraft mc, CelestialStateSupplier css, Camera camera, Matrix4f projectionMatrix, float partialTick) {
        poseStack.pushPose();

        for (SpaceRenderable obj : renderPlanets) {
            obj.calculatePos(css.getPlayerOrbit());
        }

        Arrays.sort(renderPlanets, Comparator.comparingDouble(SpaceRenderable::getDistance).reversed());

        renderPlanets(renderPlanets, css, poseStack, projectionMatrix, partialTick);

        poseStack.popPose();
    }

    public static void renderPlanets(SpaceRenderable[] renderPlanets, CelestialStateSupplier css, PoseStack poseStack, Matrix4f projectionMatrix, float partialTick) {
        Optional<PlanetaryBody> planetOn = css.getCurrentPlanet();
        float currentAlbedo = 1.0f;
        float starAlpha = 1.0f;
        Optional<PlanetAtmosphere> atmosphere = Optional.empty();

        if (planetOn.isPresent()) {
            if (planetOn.get().getAtmosphere().hasAtmosphere()) {
                currentAlbedo = css.getPlayerOrbit().getSunAngle() * 2;
                atmosphere = Optional.of(planetOn.get().getAtmosphere());
                starAlpha = 2*css.getPlayerOrbit().getSunAngle();
            }
        } else {
            AtmosphereRenderer.renderSpaceSky(poseStack, projectionMatrix);
        }

        PlanetShine.drawStarBuffer(poseStack, projectionMatrix, starAlpha);

        for (SpaceRenderable plnt : renderPlanets) {
            plnt.render(atmosphere, poseStack, projectionMatrix, currentAlbedo);
            //rendering only the sun's atmosphere for now
            if (plnt instanceof RenderablePlanet renPlanet) {
                if (renPlanet.getBody() instanceof Star) {
                    AtmosphereRenderer.render(renPlanet.getBody(), renPlanet.getNormalizedDiffVectorf(), renPlanet.getDistance(), renPlanet.getBody().getAtmosphere(), poseStack, projectionMatrix);
                }
            }
        }

        //AtmosphereRenderer.renderAtmospheres(renderPlanets, poseStack, projectionMatrix, atmosphere);
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