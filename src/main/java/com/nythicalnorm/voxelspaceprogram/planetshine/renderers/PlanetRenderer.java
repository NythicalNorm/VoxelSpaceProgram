package com.nythicalnorm.voxelspaceprogram.planetshine.renderers;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetAtmosphere;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.StarBody;
import com.nythicalnorm.voxelspaceprogram.planetshine.PlanetShine;
import com.nythicalnorm.voxelspaceprogram.planetshine.generators.QuadSphereModelGenerator;
import com.nythicalnorm.voxelspaceprogram.planetshine.shaders.ModShaders;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class PlanetRenderer {
    private static Supplier<ShaderInstance> planetShader;
    private static Uniform sunDirUniform;
    private static Uniform AtmoFilterColorUniform;

    public static void setupShader() {
        planetShader = ModShaders.getPlanetShaderInstance();
        if (planetShader.get() != null) {
            sunDirUniform = planetShader.get().getUniform("SunDirection");
            AtmoFilterColorUniform = planetShader.get().getUniform("AtmoFilterColor");
        }
        else {
            VoxelSpaceProgram.logError("Shader not loading");
        }
    }

    //for rendering in the map screen
    public static void render(CelestialBody planet, PoseStack poseStack, Matrix4f projectionMatrix) {
        render(planet, Optional.empty(), poseStack, projectionMatrix, 0, 1.0f, null);
    }

    public static void render(CelestialBody planet, Optional<PlanetAtmosphere> currentPlanetAtmosphere, PoseStack poseStack,
                              Matrix4f projectionMatrix, double distance, float currentAlbedo, @Nullable Vector3d differenceVector) {

        if ((differenceVector != null) && (distance < (planet.getRadius() + 320))) {
            return;
        }

        poseStack.pushPose();
        Quaternionf planetRot = planet.getRotation();
        RenderSystem.enableBlend();

        if (currentPlanetAtmosphere.isPresent()) {
                //AtmosphereRenderer.render(obj,atmosphere, poseStack, projectionMatrix, partialTick);
            PlanetAtmosphere bodyAtmos = planet.getAtmosphere();
            float renderOpacity = (currentAlbedo * (bodyAtmos.getAlphaNight() - bodyAtmos.getAlphaDay())) + bodyAtmos.getAlphaDay();
            RenderSystem.setShaderColor(1.0f,1.0f,1.0f, renderOpacity);
            Vec3 skyColor = PlanetShine.getLatestSkyColor();
            AtmoFilterColorUniform.set((float) skyColor.x,(float) skyColor.y,(float) skyColor.z, 1.0f);
        } else {
            AtmoFilterColorUniform.set(0f,0f, 0f,1.0f);
        }

        if (differenceVector != null) {
            SpaceObjRenderer.PerspectiveShift(distance, differenceVector, planetRot,
                    planet.getRadius(), poseStack);
        }

        QuadSphereModelGenerator.getSphereBuffer().bind();

        Optional<ResourceLocation> planetTex = CelestialStateSupplier.getInstance().get().getPlanetTexManager().getTextureForPlanet(planet.getOrbitId());
        planetTex.ifPresentOrElse(tex -> RenderSystem.setShaderTexture(0, tex),
                () -> RenderSystem.setShaderTexture(0, MissingTextureAtlasSprite.getLocation()));

        Vector3d absoluteDir = planet.getAbsolutePos().normalize();
        Vector3f lightDir = new Vector3f((float) absoluteDir.x,(float) absoluteDir.y,(float) absoluteDir.z);
        lightDir.rotate(planetRot.invert());
        lightDir.normalize();

        sunDirUniform.set(lightDir);
        ShaderInstance shad = planetShader.get();

        if (planet instanceof StarBody) { // || obj.getBody() == css.getDimPlanet()) {
            shad = GameRenderer.getPositionTexShader();
        }

        QuadSphereModelGenerator.getSphereBuffer().drawWithShader(poseStack.last().pose(), projectionMatrix, shad);
        VertexBuffer.unbind();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        poseStack.popPose();
    }
}
