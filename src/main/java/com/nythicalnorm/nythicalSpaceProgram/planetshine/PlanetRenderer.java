package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.shaders.ModShaders;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.Planets;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PlanetRenderer {
    private static final VertexBuffer planetvertex = new VertexBuffer(VertexBuffer.Usage.STATIC);
    private static final ResourceLocation Nila_texture =  ResourceLocation.parse("nythicalspaceprogram:textures/planets/moon_axis.png");
    private static final float InWorldPlanetsDistance = 64f;

    public static void setupModels() {
        PoseStack spherePose = new PoseStack();
        spherePose.setIdentity();
        List<BakedQuad> planetquads = SphereModelGenerator.getsphereQuads(); //planetModel.getQuads(null,null, RandomSource.create(), ModelData.builder().build(), RenderType.solid());
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for(BakedQuad bakedquad : planetquads) {
            bufferbuilder.putBulkData(spherePose.last(), bakedquad, 1f, 1f, 1f, 10, 10);
        }
        planetvertex.bind();
        planetvertex.upload(bufferbuilder.end());
        VertexBuffer.unbind();

    }

    public static void renderPlanetaryBodies(PoseStack poseStack, Minecraft mc, Camera camera, Matrix4f projectionMatrix, float partialTick) {
        double currentTimeElapsed = CelestialStateSupplier.getCurrentTimeElapsed();

        poseStack.pushPose();
        PlanetaryBody renderPlanet = Planets.getPlanet("nila");

        Vector3d PlanetPos = renderPlanet.CalculateCartesianPosition(currentTimeElapsed);
        Quaternionf PlanetRot = renderPlanet.getRotationAt(currentTimeElapsed);

        PerspectiveShift(PlanetPos.distance(new Vector3d()), PlanetPos, renderPlanet.getRadius(), poseStack);
        poseStack.mulPose(PlanetRot);

        planetvertex.bind();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Nila_texture);

        Vector3f lights0 = new Vector3f(1f,0f,0f);
        lights0.rotate(PlanetRot.invert());
        lights0.normalize();

        ShaderInstance shad = ModShaders.getPlanetShaderInstance();
        RenderSystem.setShaderLights(lights0, new Vector3f());

        planetvertex.drawWithShader(poseStack.last().pose(), projectionMatrix, shad);
        VertexBuffer.unbind();
        poseStack.popPose();
    }

    private static void PerspectiveShift(double PlanetDistance, Vector3d PlanetPos, double bodyRadius,PoseStack poseStack){
        //double PlanetDistance = PlanetPos.distance(new Vector3d());

        //tan amd atan cancel each other out.
        float planetApparentSize = (float) (InWorldPlanetsDistance * 2 * bodyRadius/PlanetDistance);
        PlanetPos.normalize();
        PlanetPos.mul(InWorldPlanetsDistance);
        poseStack.translate(PlanetPos.x,PlanetPos.y, PlanetPos.z);
        poseStack.scale(planetApparentSize, planetApparentSize, planetApparentSize);
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