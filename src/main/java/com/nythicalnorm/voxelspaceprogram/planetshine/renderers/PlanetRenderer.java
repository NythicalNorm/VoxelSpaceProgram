package com.nythicalnorm.voxelspaceprogram.planetshine.renderers;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ClientCelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.planet.PlanetAtmosphere;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.star.StarBody;
import com.nythicalnorm.voxelspaceprogram.planetshine.PlanetShine;
import com.nythicalnorm.voxelspaceprogram.planetshine.generators.QuadSphereModelGenerator;
import com.nythicalnorm.voxelspaceprogram.planetshine.shaders.VSPShaders;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class PlanetRenderer {
    private static ShaderInstance planetShader;
    private static Uniform sunDirUniform;
    private static Uniform AtmoFilterColorUniform;

    public static void setupShader() {
        planetShader = VSPShaders.getPlanetShader();
        if (planetShader != null) {
            sunDirUniform = planetShader.getUniform("SunDirection");
            AtmoFilterColorUniform = planetShader.getUniform("AtmoFilterColor");
        }
        else {
            VoxelSpaceProgram.logError("Shader not loading");
        }
    }

    //for rendering in the map screen
    public static void render(CelestialBody planet, PoseStack poseStack, Matrix4f projectionMatrix) {
        render(planet, Optional.empty(), poseStack, projectionMatrix, 1.0f, false, 1.0f);
    }

    public static void render(CelestialBody planet, Optional<PlanetAtmosphere> currentPlanetAtmosphere, PoseStack poseStack, Matrix4f projectionMatrix, float currentAlbedo, boolean isCurrentPlanetOn, float opacityEasing) {
        Quaternionf planetRot = planet.getRotation();

        if (currentPlanetAtmosphere.isPresent() && !isCurrentPlanetOn) {
                //AtmosphereRenderer.render(obj,atmosphere, poseStack, projectionMatrix, partialTick);
            PlanetAtmosphere bodyAtmos = planet.getAtmosphere();
            opacityEasing = (currentAlbedo * (bodyAtmos.getAlphaNight() - bodyAtmos.getAlphaDay())) + bodyAtmos.getAlphaDay();
            Vec3 skyColor = PlanetShine.getLatestSkyColor();
            AtmoFilterColorUniform.set((float) skyColor.x,(float) skyColor.y,(float) skyColor.z, 1.0f);
        } else {
            AtmoFilterColorUniform.set(0f,0f, 0f, 1.0f);
        }

        RenderSystem.setShaderColor(1.0f,1.0f,1.0f, opacityEasing);

        QuadSphereModelGenerator.getSphereBuffer().bind();
        ResourceLocation planetTex = ((ClientCelestialBody) planet).getMainTexture();
        RenderSystem.setShaderTexture(0, Objects.requireNonNullElseGet(planetTex, MissingTextureAtlasSprite::getLocation));

        Vector3d absoluteDir = planet.getAbsolutePos().normalize();
        Vector3f lightDir = new Vector3f((float) absoluteDir.x,(float) absoluteDir.y,(float) absoluteDir.z);
        lightDir.rotate(planetRot.invert());
        lightDir.normalize();

        sunDirUniform.set(lightDir);
        ShaderInstance shad = planet instanceof StarBody ? GameRenderer.getPositionTexShader() : planetShader;

        QuadSphereModelGenerator.getSphereBuffer().drawWithShader(poseStack.last().pose(), projectionMatrix, shad);
        VertexBuffer.unbind();

        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
    }
}
