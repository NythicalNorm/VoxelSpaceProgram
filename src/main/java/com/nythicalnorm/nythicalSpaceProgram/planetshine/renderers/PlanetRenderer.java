package com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.planet.Planets;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.generators.QuadSphereModelGenerator;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.shaders.ModShaders;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class PlanetRenderer {
    private static Supplier<ShaderInstance> planetShader;
    private static Uniform sunDirUniform;

    public static void render(String name,PoseStack poseStack, Matrix4f projectionMatrix, double currentTimeElapsed) {
        poseStack.pushPose();
        PlanetaryBody renderPlanet = Planets.getPlanet(name);

        Vector3d PlanetPos = renderPlanet.CalculateCartesianPosition(currentTimeElapsed);
        Quaternionf PlanetRot = renderPlanet.getRotationAt(currentTimeElapsed);

        SpaceObjRenderer.PerspectiveShift(PlanetPos.distance(new Vector3d()), PlanetPos, PlanetRot, renderPlanet.getRadius(), poseStack);

        QuadSphereModelGenerator.getSphereBuffer().bind();
        RenderSystem.setShaderTexture(0, renderPlanet.texture);

        Vector3f lightDir = new Vector3f(0f,0f,1f);
        lightDir.rotate(PlanetRot.invert());
        lightDir.normalize();

        planetShader = ModShaders.getPlanetShaderInstance();
        sunDirUniform = planetShader.get().getUniform("SunDirection");
        sunDirUniform.set(lightDir);
        QuadSphereModelGenerator.getSphereBuffer().drawWithShader(poseStack.last().pose(), projectionMatrix, planetShader.get());
        VertexBuffer.unbind();

        poseStack.popPose();
    }
}
