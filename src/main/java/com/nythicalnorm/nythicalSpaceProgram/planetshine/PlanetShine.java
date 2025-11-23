package com.nythicalnorm.nythicalSpaceProgram.planetshine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class PlanetShine {
    private static final ResourceLocation SUN_LOCATION = ResourceLocation.parse("textures/item/axolotl_bucket.png");
    private static VertexBuffer Star_Buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

    public static void setupBuffers() {
        BufferBuilder bufferbuilder =  Tesselator.getInstance().getBuilder();
        Star_Buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer = drawStars(bufferbuilder);
        Star_Buffer.bind();
        Star_Buffer.upload(bufferbuilder$renderedbuffer);
        VertexBuffer.unbind();
    }

    public static void renderSkybox(Minecraft mc, LevelRenderer levelRenderer, PoseStack poseStack,
                                      Matrix4f projectionMatrix, float partialTick, Camera camera)
    {
        Matrix4f matrix4f1 = poseStack.last().pose();
        FogRenderer.levelFogColor();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.depthMask(false);
        //RenderSystem.setShaderColor(1f, 1f, 1f, 1.0F);

        Star_Buffer.bind();
        Star_Buffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionColorShader());
        VertexBuffer.unbind();

        float sunSize = 100.0F;
        float sunPosX = 0f;
        float sunPosY = 500f;
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, sunPosX - sunSize, sunPosY, -sunSize).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, sunPosX + sunSize, sunPosY, -sunSize).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, sunPosX + sunSize, sunPosY, sunSize).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, sunPosX - sunSize, sunPosY, sunSize).uv(0.0F, 1.0F).endVertex();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        BufferUploader.drawWithShader(bufferbuilder.end());

        VertexBuffer.unbind();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        PlanetRenderer.renderPlanetaryBodies(poseStack, mc, camera, projectionMatrix, partialTick);
    }

//    private static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder pBuilder, float pY) {
//        float f = Math.signum(pY) * 512.0F;
//        float f1 = 512.0F;
//        RenderSystem.setShader(GameRenderer::getPositionShader);
//        pBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
//        pBuilder.vertex(0.0D, (double)pY, 0.0D).endVertex();
//
//        for(int i = -180; i <= 180; i += 45) {
//            pBuilder.vertex((double)(f * Mth.cos((float)i * ((float)Math.PI / 180F))), (double)pY, (double)(512.0F * Mth.sin((float)i * ((float)Math.PI / 180F)))).endVertex();
//        }
//
//        return pBuilder.end();
//    }

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
