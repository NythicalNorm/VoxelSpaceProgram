package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

import java.lang.Math;
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
        //Vec3 PlanetPos = new Vec3(0,0,0); //CelestialStateSupplier.getPlanetPositon("nila", mc.getPartialTick());
        Vector3f RelativePlanetDir = new Vector3f(0,0,1);

        Matrix4f PlanetProjection = PerspectiveShift(projectionMatrix, (float) CelestialStateSupplier.lastUpdatedTimePassedPerSec,
                RelativePlanetDir, poseStack, camera);

        planetvertex.bind();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Nila_texture);
        ShaderInstance shad = GameRenderer.getPositionTexShader();

        planetvertex.drawWithShader(poseStack.last().pose(), PlanetProjection, shad);
        VertexBuffer.unbind();
        poseStack.popPose();
    }

    private static Matrix4f PerspectiveShift(Matrix4f projectionMatrix, float PlanetDistance, Vector3f relativePlanetDir,
                                             PoseStack poseStack, Camera camera){
        Matrix4f returnMatrix = new Matrix4f(projectionMatrix);
        float PlanetAngularSize = (float) Math.atan(PlanetDistance);
        float theta = (float) (2f*Math.atan((1/returnMatrix.m11())));
        float m00prefix = returnMatrix.m00()/returnMatrix.m11();
        float newVal = (float) Math.abs(1/Math.tan(PlanetAngularSize/2f));
        returnMatrix.m00(m00prefix*newVal);
        returnMatrix.m11(newVal);

        float ScalediffwithResize = Math.abs(PlanetAngularSize/theta); //(float) Math.abs(Math.tan(theta)/Math.tan(PlanetAngularSize));

        Vector3f CameraAngle = camera.getLookVector();

        float scaleFactor = (float) (Math.tan(theta/2) / Math.tan(PlanetAngularSize / 2));
        //float AngleMultiplicationFactor = Math.abs(theta-PlanetAngularSize);// -  Math.abs(theta/PlanetAngularSize);//Math.abs(theta-PlanetAngularSize);///35.2340425512F;//(float) (Math.atan(theta)/(PlanetDistance));  //1f/(float)Math.PI*ScalediffwithResize;
        //CameraAngle.mul(AngleMultiplicationFactor);
        //scaleFactor = 1-scaleFactor;

        Quaternionf rotationBetween = getRotationBetween(CameraAngle, relativePlanetDir);
        AxisAngle4f Diffangle = new AxisAngle4f(rotationBetween.normalize());
        Diffangle.angle = (Diffangle.angle/scaleFactor);
        rotationBetween = new Quaternionf(Diffangle);
        poseStack.rotateAround(rotationBetween,0,0,0);

        relativePlanetDir.mul(InWorldPlanetsDistance);
        poseStack.translate(relativePlanetDir.x, relativePlanetDir.y, relativePlanetDir.z);
        poseStack.scale(ScalediffwithResize,ScalediffwithResize,ScalediffwithResize);
        poseStack.scale(3,3,3);
        return returnMatrix;
    }

    private static Quaternionf getRotationBetween(Vector3f u, Vector3f v)
    {
        Quaternionf q = new Quaternionf();
        Vector3f u1 = new Vector3f(u);
        Vector3f v1 = new Vector3f(v);

        Vector3f a = u1.cross(v1);
        q.x = a.x;
        q.y = a.y;
        q.z = a.z;

        q.w = 1 + u.dot(v);
        return q;
    }
}