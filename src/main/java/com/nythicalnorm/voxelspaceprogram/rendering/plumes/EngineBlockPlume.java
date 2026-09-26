package com.nythicalnorm.voxelspaceprogram.rendering.plumes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nythicalnorm.planetshine.util.calculations.MiscCalc;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity_renderers.EngineEntityRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.api.ValkyrienSkies;

@OnlyIn(Dist.CLIENT)
public class EngineBlockPlume {
    private final PlumeSettings plumeSettings;
    private final EngineEntity entity;
    private final EngineEntityRenderer engineEntityRenderer;
    private final boolean isOnShip;
    private final Vector3d enginePos;
    private final Quaternionf shipRot;

    public EngineBlockPlume(PlumeSettings plumeSettings, EngineEntity entity, BlockPos blockPos, EngineEntityRenderer engineEntityRenderer) {
        this.plumeSettings = plumeSettings;
        this.entity = entity;
        this.engineEntityRenderer = engineEntityRenderer;
        this.isOnShip = ValkyrienSkies.isBlockInShipyard(entity.getLevel(), entity.getBlockPos());

        this.enginePos = this.isOnShip ? new Vector3d() : new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        this.shipRot = new Quaternionf();
    }

    public void render(@NotNull MultiBufferSource pBuffer, PoseStack poseStack, Camera camera) {
        if (!entity.isEnabled() || entity.getLevel() == null) {
            return;
        }

        if (this.isOnShip) {
            ClientShip clientShip = (ClientShip) ValkyrienSkies.getShipManagingBlock(entity.getLevel(), entity.getBlockPos());
            if (clientShip == null) {
                return;
            }
            clientShip.getRenderTransform().getShipToWorld().transformPosition(
                    entity.getBlockPos().getX(),
                    entity.getBlockPos().getY(),
                    entity.getBlockPos().getZ(),
                    enginePos
            );

            this.shipRot.set(clientShip.getRenderTransform().getRotation());
        }

        poseStack.pushPose();
        Vec3 cameraPos = camera.getPosition();
        poseStack.translate(
                (enginePos.x() - cameraPos.x),
                (enginePos.y() - cameraPos.y),
                (enginePos.z() - cameraPos.z)
        );
        poseStack.mulPose(this.shipRot);
        poseStack.translate(0.5f, 0.5f, 0.5f);

        //actual transformations after ship and camera stuff
        poseStack.mulPose(entity.getFacingRot());
        Vector3d postRotOffset = new Vector3d(engineEntityRenderer.getPostRotOffset(entity.getBlockState().getBlock()));
        poseStack.translate(postRotOffset.x(), postRotOffset.y(), postRotOffset.z());

        this.renderGlowQuad(pBuffer, poseStack.last());
        poseStack.popPose();
    }

    protected void renderGlowQuad(MultiBufferSource pBuffer, PoseStack.Pose pose) {
        VertexConsumer consumer = pBuffer.getBuffer(PlumeRenderTypes.ENGINE_OVERLAY);
//        PoseStack.Pose pose = poseStack.last();
        float alpha = 0.75f;
        float[] color = MiscCalc.getRGBAFloats(0xFEFF9E, alpha);
        float halfHeight = (float) (this.plumeSettings.getStartYPixelOffset()) / 32.0f;
        float halfWidth = (float) (this.plumeSettings.getStartingWidth()) / 2.0f;

        consumer.vertex(pose.pose(), -halfWidth, halfHeight, halfWidth)
                .color(color[0], color[1], color[2], color[3])
                .uv(0, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();
        consumer.vertex(pose.pose(), halfWidth, halfHeight, halfWidth)
                .color(color[0], color[1], color[2], color[3])
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0, halfHeight, 0)
                .endVertex();
        consumer.vertex(pose.pose(), halfWidth, halfHeight, -halfWidth)
                .color(color[0], color[1], color[2], color[3])
                .uv(1, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0, halfHeight, 0)
                .endVertex();

        consumer.vertex(pose.pose(), -halfWidth, halfHeight, -halfWidth)
                .color(color[0], color[1], color[2], color[3])
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0, halfHeight, 0)
                .endVertex();
    }
}
