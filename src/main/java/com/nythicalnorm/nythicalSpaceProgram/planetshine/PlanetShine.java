package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.generators.SkyboxCubeGen;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.AtmosphereRenderer;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.PlanetRenderer;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.SpaceObjRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PlanetShine {
    private static VertexBuffer Star_Buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
    private static VertexBuffer Skybox_Buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);


    private static boolean isFirstTime = true;

    public static void setupBuffers() {
        BufferBuilder bufferbuilder =  Tesselator.getInstance().getBuilder();
        Star_Buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder.RenderedBuffer starsRendered = drawStars(bufferbuilder);
        Star_Buffer.bind();
        Star_Buffer.upload(starsRendered);
        VertexBuffer.unbind();

        Skybox_Buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder.RenderedBuffer skyboxRendered = buildSkyBox(bufferbuilder);
        Skybox_Buffer.bind();
        Skybox_Buffer.upload(skyboxRendered);
        VertexBuffer.unbind();
    }

    private static void setupShaders() {
        PlanetRenderer.setupShader();
        AtmosphereRenderer.setupShader(Skybox_Buffer);
        //SpaceObjRenderer.PopulateRenderPlanets();
    }

    public static void renderSkybox(Minecraft mc, LevelRenderer levelRenderer, PoseStack poseStack,
    Matrix4f projectionMatrix, float partialTick, Camera camera, VertexBuffer sky_Buffer, CelestialStateSupplier css)
    {
        if (isFirstTime) {
            setupShaders();
            isFirstTime = false;
        }
        FogRenderer.levelFogColor();
        if (mc.player.getEyePosition(partialTick).y < mc.level.getMinBuildHeight()) {
            return;
        }

        css.UpdateOrbitalBodies();
        RenderSystem.depthMask(false);

        if (css.isOnPlanet()) {
            if (css.getCurrentPlanet().get().getAtmoshpere().hasAtmosphere()) {
                Vec3 skyColor = Minecraft.getInstance().level.getSkyColor(camera.getPosition(), partialTick);

                RenderSystem.setShaderColor((float) skyColor.x, (float) skyColor.y, (float) skyColor.z, 1.0F);
                ShaderInstance posShad = RenderSystem.getShader();
                sky_Buffer.bind();
                sky_Buffer.drawWithShader(poseStack.last().pose(), projectionMatrix, posShad);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        poseStack.pushPose();
        //Vector3d PlanetSurfaceDir = Calcs.planetDimPosToNormalizedVector(Minecraft.getInstance().player.position(), NythicalSpaceProgram.getCelestialStateSupplier().getCurrentPlanetWithinSOI());
        poseStack.mulPose(css.getPlayerData().getRotation());

        SpaceObjRenderer.renderPlanetaryBodies(poseStack, mc, css, camera, projectionMatrix, partialTick);
        RenderSystem.depthMask(true);
        poseStack.popPose();
    }

    public static void drawStarBuffer(PoseStack poseStack, Matrix4f projectionMatrix, CelestialStateSupplier css) {
        float alpha = 1.0f;

        if (css.isOnPlanet()) {
            if (css.getCurrentPlanet().get().getAtmoshpere().hasAtmosphere()) {
                alpha = 2*css.getPlayerData().getSunAngle();
            }
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        Star_Buffer.bind();
        Star_Buffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionColorShader());
        VertexBuffer.unbind();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
    }

    private static BufferBuilder.RenderedBuffer buildSkyBox(BufferBuilder pBuilder) {
        Vector3f[] cubeVertecies = SkyboxCubeGen.getCubeVertexes();
        pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (Vector3f vertex : cubeVertecies) {
            pBuilder.vertex(vertex.x, vertex.y, vertex.z).color(10, 11, 20, 255).endVertex();
        }

        return pBuilder.end();
    }

    private static BufferBuilder.RenderedBuffer drawStars(BufferBuilder pBuilder) {
        RandomSource randomsource = RandomSource.create(1000L);
        pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for(int i = 0; i < 700; ++i) {
            double d0 = (double)(randomsource.nextFloat() * 2.0F - 1.0F);
            double d1 = (double)(randomsource.nextFloat() * 2.0F - 1.0F);
            double d2 = (double)(randomsource.nextFloat() * 2.0F - 1.0F);
            double d3 = (double)(0.15F + randomsource.nextFloat() * 0.1F);
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                d0 *= d4;
                d1 *= d4;
                d2 *= d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = randomsource.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                float starColor = randomsource.nextFloat();
                float redness = 1f ;
                float blueness = 1f;
                float greenness = 1f;
                float alpha = 0.0f + (randomsource.nextFloat() * 0.4f);
                if (starColor < 0.25f) {
                    greenness = greenness - starColor;
                    blueness = blueness - starColor;
                } else if (starColor < 0.6f) {
                    greenness = greenness - starColor *0.8f;
                    redness = redness - starColor *0.8f;
                }
                for(int j = 0; j < 4; ++j) {
                    //double d17 = 0.0D;
                    double d18 = (double)((j & 2) - 1) * d3;
                    double d19 = (double)((j + 1 & 2) - 1) * d3;
                    //double d20 = 0.0D;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    pBuilder.vertex(d5 + d25, d6 + d23, d7 + d26).color(redness,greenness,blueness,alpha).endVertex();
                }
            }
        }

        return pBuilder.end();
    }
}
