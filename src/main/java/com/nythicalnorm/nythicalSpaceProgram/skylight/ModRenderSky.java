package com.nythicalnorm.nythicalSpaceProgram.skylight;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = NythicalSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModRenderSky {
    private VertexBuffer skyBGBuffer;

    @SubscribeEvent
    public void RenderLevelStageEvent(RenderLevelStageEvent.Stage stage, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum)
    {
        if (stage == RenderLevelStageEvent.Stage.AFTER_SKY) {
            Vec3 vecColor = new Vec3(0f,0f,0f);
            RenderSystem.setShaderColor(1f,1f,1f, 1.0f);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();

            BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer = this.DosomeVertex(bufferbuilder);
            skyBGBuffer.bind();
            skyBGBuffer.upload(bufferbuilder$renderedbuffer);
            VertexBuffer.unbind();
        }
    }

    private BufferBuilder.RenderedBuffer DosomeVertex(BufferBuilder pBuilder) {
        pBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        pBuilder.vertex(5d, 4d, 2d).endVertex();
        pBuilder.vertex(10d, 50d, 100d).endVertex();

        return pBuilder.end();
    }
}
