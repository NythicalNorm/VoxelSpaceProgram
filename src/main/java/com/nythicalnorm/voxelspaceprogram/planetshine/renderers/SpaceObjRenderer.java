package com.nythicalnorm.voxelspaceprogram.planetshine.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.star.StarBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.planet.PlanetAtmosphere;
import com.nythicalnorm.voxelspaceprogram.planetshine.renderTypes.SpaceRenderable;
import com.nythicalnorm.voxelspaceprogram.planetshine.renderTypes.RenderablePlanet;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.planetshine.PlanetShine;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class SpaceObjRenderer {
    private static SpaceRenderable[] renderPlanets;

    public static void PopulateRenderPlanets(PlanetsProvider planets) {
        List<CelestialBody> planetList = planets.getAllPlanetOrbitsList();

        renderPlanets = new SpaceRenderable[planetList.size()];
        int i = 0;

        for (CelestialBody planet : planetList) {
            renderPlanets[i] = new RenderablePlanet(planet);
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
        Optional<CelestialBody> planetOn = css.getCurrentPlanet();
        float currentAlbedo = 1.0f;
        float starAlpha = 1.0f;
        CelestialBody currentPlanetIn = null;
        Optional<PlanetAtmosphere> atmosphere = Optional.empty();

        if (planetOn.isPresent()) {
            currentPlanetIn = planetOn.get();

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
            if (plnt instanceof RenderablePlanet renPlanet) {
                if (plnt.getDistance() < renPlanet.getBody().getRadius() + OPACITY_EASING_MIN) {
                    break;
                }

                boolean isCurrentPlanetOn = Objects.equals(renPlanet.getBody(), currentPlanetIn);
                poseStack.pushPose();
                RenderSystem.enableBlend();
                float opacityEasing = planetOnOpacity(renPlanet.getBody(), renPlanet.getDistance());

                PerspectiveShift(renPlanet.getDistance(), renPlanet.getDifferenceVector(), renPlanet.getBody().getRotation(),
                        renPlanet.getBody().getRadius(), poseStack);
                plnt.render(atmosphere, poseStack, projectionMatrix, currentAlbedo, isCurrentPlanetOn, opacityEasing);

                if (isCurrentPlanetOn) {
                    LodTexRenderer.renderLODs(poseStack, projectionMatrix, css.getPlanetTexManager().getLodTexAtlasID(),
                            opacityEasing, css.getPlanetTexManager().getLodTexBuffer());
                }

                RenderSystem.disableBlend();
                poseStack.popPose();

                //rendering only the sun's atmosphere for now
                if (renPlanet.getBody() instanceof StarBody) {
                    AtmosphereRenderer.render(renPlanet.getBody(), renPlanet.getNormalizedDiffVectorf(), renPlanet.getDistance(), renPlanet.getBody().getAtmosphere(), poseStack, projectionMatrix);
                }
            }
        }
    }

    public static void PerspectiveShift(double PlanetDistance, Vector3d PlanetPos, Quaternionf planetRot, double bodyRadius,PoseStack poseStack){
        float inWorldPlanetsDistance = Minecraft.getInstance().gameRenderer.getDepthFar() * 0.5f;
        //tan amd atan cancel each other out.
        float planetApparentSize = (float) (inWorldPlanetsDistance * 2 * bodyRadius/PlanetDistance);
        PlanetPos.normalize();
        PlanetPos.mul(inWorldPlanetsDistance);
        poseStack.translate(PlanetPos.x,PlanetPos.y, PlanetPos.z);
        poseStack.scale(planetApparentSize, planetApparentSize, planetApparentSize);
        poseStack.mulPose(planetRot);
    }

    private static final float OPACITY_EASING_MIN = 320;
    private static final float OPACITY_EASING_MAX = 576;

    public static float planetOnOpacity(CelestialBody celestialBody, double distance) {
        double altitude = distance - celestialBody.getRadius();
        float opacity = 1f;
        if (altitude <= OPACITY_EASING_MAX && altitude >= OPACITY_EASING_MIN) {
            float diff = OPACITY_EASING_MAX - OPACITY_EASING_MIN;
            opacity = ((float) altitude - OPACITY_EASING_MIN) / diff;
        }
        return opacity;
    }
}