package com.nythicalnorm.voxelspaceprogram.block.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.PreviewRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class MultiblockBERenderer<T extends BlockEntity> implements BlockEntityRenderer<T>, PreviewRenderer {
    @Override
    public void render(T pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack,
                                @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay
    ) {
        pPoseStack.pushPose();
        Block block = pBlockEntity.getBlockState().getBlock();
        ModelPart model = getMainModel(block);
        if (model != null && pBlockEntity instanceof VSPMultiblockEntity vspMultiblock) {
            VertexConsumer vertexconsumer = getMaterial(block).buffer(pBuffer, RenderType::entitySolid);
            pPoseStack.translate(0.5f, 0.5f, 0.5f);
            pPoseStack.mulPose(vspMultiblock.getFacingRot());
            Vector3f postRotOffset = getPostRotOffset(block);
            pPoseStack.translate(postRotOffset.x(), postRotOffset.y(), postRotOffset.z());
            setModelPositions(pBlockEntity, model, pPartialTick);
            model.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay); //, color[0], color[1], color[2], color[3]);
        }
        pPoseStack.popPose();
    }

    public void renderPreview(BlockState blockState, float pPartialTick, PoseStack pPoseStack,
                              MultiBufferSource pBuffer, Material material, float[] color) {
        pPoseStack.pushPose();
        Block block = blockState.getBlock();
        ModelPart model = getMainModel(block);

        Quaternionf facingRot = new Quaternionf();
        if (block instanceof MachineryMultiblock) {
            Direction dir = blockState.getValue(MachineryMultiblock.FACING_HORIZONTAL);
            facingRot.set(dir.getRotation().rotateX(Mth.HALF_PI));
        } else if (block instanceof RocketryMultiblock) {
            Direction dir = blockState.getValue(RocketryMultiblock.FACING);
            facingRot.set(dir.getRotation());
        }

        if (model != null) {
            VertexConsumer vertexconsumer = material.buffer(pBuffer, RenderType::entityTranslucent);
            pPoseStack.translate(0.5f, 0.5f, 0.5f);
            pPoseStack.mulPose(facingRot);
            Vector3f postRotOffset = getPostRotOffset(block);
            pPoseStack.translate(postRotOffset.x(), postRotOffset.y(), postRotOffset.z());
            model.resetPose();
            model.render(pPoseStack, vertexconsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], color[3]);
        }
        pPoseStack.popPose();

    }

    protected abstract Vector3f getPostRotOffset(Block block);
    protected abstract ModelPart getMainModel(Block block);
    protected abstract void setModelPositions(T blockEntity, ModelPart main, float pPartialTick);
}
