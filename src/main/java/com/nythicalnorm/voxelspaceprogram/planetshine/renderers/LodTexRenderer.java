package com.nythicalnorm.voxelspaceprogram.planetshine.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.planetshine.textures.LodTexQuad;
import com.nythicalnorm.voxelspaceprogram.planetshine.textures.ClientTexManager;
import com.nythicalnorm.voxelspaceprogram.spacecraft.player.ClientPlayerOrbitBody;
import com.nythicalnorm.voxelspaceprogram.util.LodTexUtils;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

public class LodTexRenderer {
    public static void renderBiomeTex(ClientPlayerOrbitBody playerOrbit, ClientTexManager planetTexManager, PoseStack poseStack, Matrix4f projectionMatrix) {
        LodTexQuad[] biomeTexQuads = planetTexManager.getLodTexQuads();
        RenderSystem.setShaderTexture(0, planetTexManager.getLodTexAtlasID());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        if (biomeTexQuads[0] != null) {
            int totalSize = LodTexUtils.texInOneAxisCount * LodTexUtils.textureResolution;
            renderQuad(biomeTexQuads[0],poseStack, projectionMatrix, playerOrbit, planetTexManager, totalSize);
        }
    }

    private static void renderQuad(LodTexQuad texQuad, PoseStack poseStack, Matrix4f projectionMatrix, ClientPlayerOrbitBody playerOrbit, ClientTexManager planetTexManager, int totalSize) {
        Vector2i atlasCoords = texQuad.getTexAtlasCoords();
        poseStack.pushPose();

        float xOffset = (float) atlasCoords.x / totalSize;
        float yOffset = (float) atlasCoords.y / totalSize;

        float uvSize =  (float) LodTexUtils.textureResolution / totalSize;

        Vector3d relativePos = playerOrbit.getRelativePos();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        float sunSize = 100.0F;
        float sunPosX = 0f;
        float sunPosY = 400f;
        Matrix4f lastPose = poseStack.last().pose();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(lastPose,  sunPosX - sunSize, sunPosY, -sunSize).uv(xOffset, yOffset).endVertex();
        bufferbuilder.vertex(lastPose,  sunPosX + sunSize, sunPosY, -sunSize).uv(xOffset + uvSize, yOffset).endVertex();
        bufferbuilder.vertex(lastPose,  sunPosX + sunSize, sunPosY, sunSize).uv(xOffset + uvSize, yOffset + uvSize).endVertex();
        bufferbuilder.vertex(lastPose,  sunPosX - sunSize, sunPosY, sunSize).uv(xOffset, yOffset + uvSize).endVertex();

        BufferUploader.drawWithShader(bufferbuilder.end());
        poseStack.popPose();

        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR) {
            // Handle error (e.g., throw exception or log)
            VoxelSpaceProgram.logError("OpenGL Error: " + error);
        }
    }
}
