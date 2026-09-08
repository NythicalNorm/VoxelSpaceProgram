package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.models.EngineModel;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.models.EngineModelData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class EngineEntityRenderer implements BlockEntityRenderer<EngineEntity> {
    private static final Map<Block, EngineModel> engineModelMap = new Object2ObjectOpenHashMap<>();

    public EngineEntityRenderer(BlockEntityRendererProvider.Context context) {
        engineModelMap.put(NSPBlocks.THREE_KEROLOX.get(), new EngineModel(context, EngineModelData.ThreeKeroloxLayerLoc, VoxelSpaceProgram.rl( "block/three_kerolox"))) ;
        engineModelMap.put(NSPBlocks.TWO_KEROLOX.get(), new EngineModel(context, EngineModelData.TwoKeroloxLayerLoc, VoxelSpaceProgram.rl( "block/two_kerolox"))) ;
    }

    @Override
    public void render(EngineEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        EngineModel model = engineModelMap.get(pBlockEntity.getBlockState().getBlock());
        if (model != null) {
            VertexConsumer vertexconsumer = model.getTexture_Location().buffer(pBuffer, RenderType::entitySolid);
            pPoseStack.translate(0.5f, 0.5f, 0.5f);
            pPoseStack.mulPose(pBlockEntity.getFacingRot());
            pPoseStack.translate(0f, 1.0f, 0f);

            model.getFixed().render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
        }
        pPoseStack.popPose();
    }
}
