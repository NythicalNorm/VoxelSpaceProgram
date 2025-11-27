package com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planet.Star;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.CelestialStateSupplier;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.RenderableObjects;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.generators.QuadSphereModelGenerator;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.shaders.ModShaders;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class PlanetRenderer {
    private static Supplier<ShaderInstance> planetShader;
    private static Uniform sunDirUniform;

    public static void setupShader() {
        planetShader = ModShaders.getPlanetShaderInstance();
        if (planetShader.get() != null) {
            sunDirUniform = planetShader.get().getUniform("SunDirection");
        }
        else {
            NythicalSpaceProgram.logError("Shader not loading");
        }
    }

    public static void render(RenderableObjects obj, CelestialStateSupplier css, PoseStack poseStack, Matrix4f projectionMatrix, double currentTimeElapsed) {
        poseStack.pushPose();
        Quaternionf planetRot =  obj.getBody().getPlanetRotation();

        SpaceObjRenderer.PerspectiveShift(obj.getDistance(), obj.getDifferenceVector(), planetRot,
                obj.getBody().getRadius(), poseStack);

        QuadSphereModelGenerator.getSphereBuffer().bind();
        RenderSystem.setShaderTexture(0, obj.getBody().texture);

        Vector3d absoluteDir = new Vector3d(obj.getBody().getPlanetAbsolutePos()).normalize();
        Vector3f lightDir = new Vector3f((float) absoluteDir.x,(float) absoluteDir.y,(float) absoluteDir.z);
        lightDir.rotate(planetRot.invert());
        lightDir.normalize();

        sunDirUniform.set(lightDir);
        ShaderInstance shad = planetShader.get();
        if (obj.getBody() instanceof Star || obj.getBody() == css.getDimPlanet()) {
            shad = GameRenderer.getPositionTexShader();
        }

        QuadSphereModelGenerator.getSphereBuffer().drawWithShader(poseStack.last().pose(), projectionMatrix, shad);
        VertexBuffer.unbind();

        poseStack.popPose();
    }
}
