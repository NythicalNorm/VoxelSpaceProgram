package com.nythicalnorm.nythicalSpaceProgram.planetshine.map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.orbit.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.PlanetShine;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.RenderableObjects;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.AtmosphereRenderer;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.PlanetRenderer;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.renderers.SpaceObjRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.*;

import java.util.Optional;

public class MapRenderer {
    Quaternionf currentRotation;
    private static final float SCALE_FACTOR = 1/1000000000f;
    private static VertexBuffer playerHeadBillBoard;
    private static VertexBuffer arrow = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);

    public static void initModel() {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        playerHeadBillBoard = new VertexBuffer(VertexBuffer.Usage.STATIC);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float size = 0.5f;
        bufferbuilder.vertex(-size, -size, 0).uv(0.125F, 0.25F).endVertex();
        bufferbuilder.vertex(+size, -size, 0).uv(0.25F, 0.25F).endVertex();
        bufferbuilder.vertex(+size, +size, 0).uv(0.25F, 0.125F).endVertex();
        bufferbuilder.vertex(-size, +size, 0).uv(0.125F, 0.125F).endVertex();

        playerHeadBillBoard.bind();
        playerHeadBillBoard.upload(bufferbuilder.end());
        VertexBuffer.unbind();
    }

    public static void renderSkybox(PoseStack mapPosestack, Matrix4f projectionMatrix) {
        AtmosphereRenderer.renderSpaceSky(mapPosestack, projectionMatrix);
        PlanetShine.drawStarBuffer(mapPosestack, projectionMatrix, 1.0f);
    }

    public static void renderMapObjects(PoseStack poseStack, Matrix4f projectionMatrix, Vector3d cameraPos, PlanetaryBody currentFocus) {
        RenderableObjects[] renderableObjects = SpaceObjRenderer.getRenderPlanets();

        Vector3f mapCameraPos = toMapCoordinate(cameraPos);
        poseStack.translate(-mapCameraPos.x, -mapCameraPos.y, -mapCameraPos.z);

        for (RenderableObjects plnt : renderableObjects) {
            if (plnt.getBody() == currentFocus) {
                renderPlanetAt(new Vector3f(0f,0f,0f), plnt, poseStack, projectionMatrix);
            } else if (currentFocus.hasChild(plnt.getBody())) {
                renderPlanetAt(toMapCoordinate(plnt.getBody().getRelativePos()), plnt, poseStack, projectionMatrix);
            } else {
                Vector3d differenceVector = plnt.getBody().getAbsolutePos();
                differenceVector.sub(currentFocus.getAbsolutePos());

                renderPlanetAt(toMapCoordinate(differenceVector), plnt, poseStack, projectionMatrix);
            }
        }

        NythicalSpaceProgram.getCelestialStateSupplier().get().getCurrentPlanet().ifPresent(currentplanet -> {
            Vector3f planetRelativePos = toMapCoordinate(NythicalSpaceProgram.getCelestialStateSupplier().get().getPlayerData().getRelativePos());
            renderPlayerHeadOnPlanet(planetRelativePos, mapCameraPos, poseStack, projectionMatrix);
        });
    }

    private static void renderPlanetAt(Vector3f mapCoordinate, RenderableObjects plnt, PoseStack poseStack, Matrix4f projectionMatrix) {
        poseStack.pushPose();
        poseStack.translate(mapCoordinate.x, mapCoordinate.y, mapCoordinate.z);

        //test Velocity Lines
        drawDebugLines(plnt.getBody().getRelativeVelocity().normalize(), poseStack, projectionMatrix);

        float PlanetSize = (float) (2f*SCALE_FACTOR*plnt.getBody().getRadius());
        poseStack.scale(PlanetSize, PlanetSize, PlanetSize);
        poseStack.mulPose(plnt.getBody().getRotation());

        PlanetRenderer.render(plnt,false, Optional.empty(), poseStack, projectionMatrix, 0, 1.0f);
        poseStack.popPose();
    }

    //draw test lines
    private static void drawDebugLines(Vector3d dir, PoseStack pose, Matrix4f projectionMatrix) {
        Vector3f smallDir = new Vector3f((float) dir.x,(float) dir.y,(float) dir.z);
        smallDir.mul(0.1f);

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(0f,0f,0f).color(255,0,0,255).endVertex();
        bufferbuilder.vertex(smallDir.x,smallDir.y,smallDir.z).color(255,0,0,255).endVertex();
        arrow.bind();
        arrow.upload(bufferbuilder.end());
        arrow.drawWithShader(pose.last().pose(), projectionMatrix, GameRenderer.getPositionColorShader());
        VertexBuffer.unbind();
    }

    private static void renderPlayerHeadOnPlanet(Vector3f PlayerRelativePos, Vector3f cameraPos, PoseStack poseStack, Matrix4f projectionMatrix) {
        poseStack.pushPose();
        RenderSystem.disableDepthTest();

        poseStack.translate(PlayerRelativePos.x, PlayerRelativePos.y, PlayerRelativePos.z);
        poseStack.scale(0.001f, 0.001f, 0.001f);
        poseStack.mulPose(new Quaternionf().lookAlong(cameraPos.x, cameraPos.y, cameraPos.z, 0f,1f,0f));

        ResourceLocation playerTexture = Minecraft.getInstance().player.getSkinTextureLocation();
        RenderSystem.setShaderTexture(0, playerTexture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        playerHeadBillBoard.bind();
        playerHeadBillBoard.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionTexShader());
        VertexBuffer.unbind();

        RenderSystem.enableDepthTest();
        poseStack.popPose();
    }


    public static Vector3f toMapCoordinate(Vector3d position) {
        position.mul(SCALE_FACTOR);
        Vector3f mapCoord = new Vector3f((float) position.x, (float) position.y, (float) position.z);
        return mapCoord;
    }


//    private void renderPlayerHead(GuiGraphics graphics, int posX, int posY, float textureSize) {
//        ResourceLocation playerTexture = Minecraft.getInstance().player.getSkinTextureLocation();
//        float relativeHeadSize = textureSize/8;
//
//        graphics.blit(playerTexture, (int) (posX - relativeHeadSize*0.5f), (int) (posY - relativeHeadSize*0.5f), (int) relativeHeadSize,
//                (int) relativeHeadSize,(int) relativeHeadSize, (int) relativeHeadSize,  (int) textureSize, (int) textureSize);
//    }
}
