package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
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

    public static void renderPlanet(PoseStack poseStack, Minecraft mc, Camera camera, Matrix4f projectionMatrix) {
        poseStack.pushPose();
        Vec3 PlanetPos = CelestialStateSupplier.getPlanetPositon("nila", mc.getPartialTick());

        //PerspectiveShift((float) CelestialStateSupplier.lastUpdatedTimePassedPerSec, PlanetPos, poseStack);

        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
        RenderSystem.enableDepthTest();

        planetvertex.bind();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Nila_texture);

        ShaderInstance shad = GameRenderer.getPositionTexShader();

        planetvertex.drawWithShader(poseStack.last().pose(), projectionMatrix, shad);
        VertexBuffer.unbind();
        poseStack.popPose();
        RenderSystem.disableDepthTest();
    }

    private static void PerspectiveShift(float PlanetDistance, Vec3 PlanetPos,PoseStack poseStack){
        double magnitude = PlanetPos.distanceTo(new Vec3(0,0,0));
        PlanetPos.normalize();
        Vector3f relativePlanetDir = new Vector3f((float) PlanetPos.x, (float) PlanetPos.y, (float) PlanetPos.z);

        float planetApparentSize = 5f;
        Quaternionf planetDirRotation = new Quaternionf();
        planetDirRotation.rotationTo(new Vector3f(0f,0f,1f), relativePlanetDir);

        poseStack.mulPose(planetDirRotation);
        poseStack.translate(0,0, InWorldPlanetsDistance);
        poseStack.scale(planetApparentSize, planetApparentSize, PlanetDistance);
        planetDirRotation.rotationTo(relativePlanetDir, new Vector3f(0f,0f,1f));
        poseStack.mulPose(planetDirRotation);
    }
}