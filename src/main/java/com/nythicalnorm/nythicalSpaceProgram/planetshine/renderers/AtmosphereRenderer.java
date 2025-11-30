package com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetAtmosphere;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.RenderableObjects;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.shaders.ModShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import org.joml.*;

import java.lang.Math;
import java.util.function.Supplier;

public class AtmosphereRenderer {
    private static Supplier<ShaderInstance> skyboxShader;
    private static VertexBuffer skyboxBuffer;
    private static Uniform BottomColor;
    private static Uniform TopColor;
    private static Uniform TransitionPoint;
    private static Uniform Opacity;

    private static final PlanetAtmosphere EmptySpaceAtmo = new PlanetAtmosphere(false, new float[]{0,0,0,1}, new float[]{0,0,0,1}, Double.POSITIVE_INFINITY,0,0);

    public static void setupShader(VertexBuffer skyBuffer) {
        skyboxShader = ModShaders.getSkyboxShaderInstance();
        skyboxBuffer = skyBuffer;
        if (skyboxShader.get() != null) {
            BottomColor = skyboxShader.get().getUniform("nspBottomColor");
            TopColor = skyboxShader.get().getUniform("nspTopColor");
            TransitionPoint = skyboxShader.get().getUniform("nspTransitionPoint");
            Opacity = skyboxShader.get().getUniform("nspOpacity");
        }
        else {
            NythicalSpaceProgram.logError("Shader not loading");
        }
    }

    public static void render(RenderableObjects renBody, double distance, PlanetAtmosphere atmosphere, PoseStack poseStack, Matrix4f projectionMatrix) {
        poseStack.pushPose();

        RenderSystem.enableBlend();

        Vector3f relativeDir = new Vector3f(0f, -1f, 0f);

        if (renBody != null) {
            relativeDir = renBody.getNormalizedDiffVectorf();
        }

        poseStack.mulPose(new Quaternionf().rotateTo(new Vector3f(0f,-1f,0f), relativeDir));
        float[] ColorOne = atmosphere.getColorTransitionOne();
        float[] ColorTwo = atmosphere.getColorTransitionTwo();

        float distDiffAtmo =  1f - (float)(distance/atmosphere.getAtmosphereHeight());
        float howDark = Mth.clamp(distDiffAtmo, 0f, 1f);
        ColorTwo[0] = ColorTwo[0]*howDark;
        ColorTwo[1] = ColorTwo[1]*howDark;
        ColorTwo[2] = ColorTwo[2]*howDark;

        ColorOne[0] = ColorOne[0]*howDark;
        ColorOne[1] = ColorOne[1]*howDark;
        ColorOne[2] = ColorOne[2]*howDark;

        BottomColor.set(ColorOne);
        TopColor.set(ColorTwo);
        TransitionPoint.set(0.52777777777f);
        //Opacity.set(1.0f);

        skyboxBuffer.bind();
        skyboxBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, skyboxShader.get());
        VertexBuffer.unbind();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    public static void renderAtmospheres(RenderableObjects[] renBody, PoseStack poseStack, Matrix4f projectionMatrix) {
        boolean alreadyRendered = false;

        for (RenderableObjects ren : renBody) {
            PlanetaryBody plnt = ren.getBody();
            double distance = (ren.getDistance() - plnt.getRadius());

            if (ren.getBody().getAtmoshpere().hasAtmosphere() && distance < plnt.getAtmoshpere().getAtmosphereHeight()) {
                alreadyRendered = true;
                render(ren, distance, plnt.getAtmoshpere(), poseStack, projectionMatrix);
            }
        }
        if (!alreadyRendered) {
            render(null, 0, EmptySpaceAtmo, poseStack, projectionMatrix);
        }
    }
}
