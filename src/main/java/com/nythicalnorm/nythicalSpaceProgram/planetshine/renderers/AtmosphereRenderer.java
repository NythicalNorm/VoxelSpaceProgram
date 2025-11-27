package com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetAtmosphere;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.shaders.ModShaders;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

import java.util.function.Supplier;

public class AtmosphereRenderer {
    private static Supplier<ShaderInstance> skyboxShader;
    private static Uniform BottomColor;
    private static Uniform TopColor;
    private static Uniform TransitionPoint;
    private static Uniform Opacity;

    public static void setupShader() {
        skyboxShader = ModShaders.getSkyboxShaderInstance();
        if (skyboxShader.get() != null) {
            BottomColor = skyboxShader.get().getUniform("BottomColor");
            TopColor = skyboxShader.get().getUniform("TopColor");
            TransitionPoint = skyboxShader.get().getUniform("TransitionPoint");
            Opacity = skyboxShader.get().getUniform("Opacity");
        }
        else {
            NythicalSpaceProgram.logError("Shader not loading");
        }
    }

    public static void render(VertexBuffer skyboxBuffer, PoseStack poseStack, Matrix4f projectionMatrix) {
        if (!NythicalSpaceProgram.getCelestialStateSupplier().isOnPlanet())
        {
            return;
        }
        PlanetAtmosphere atmosphere = NythicalSpaceProgram.getCelestialStateSupplier().getDimPlanet().getAtmoshpere();
        BottomColor.set(atmosphere.getColorTransitionOne());
        TopColor.set(atmosphere.getColorTransitionTwo());
        TransitionPoint.set(0.52777777777f);

        skyboxBuffer.bind();
        skyboxBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, skyboxShader.get());
        VertexBuffer.unbind();
    }
}
