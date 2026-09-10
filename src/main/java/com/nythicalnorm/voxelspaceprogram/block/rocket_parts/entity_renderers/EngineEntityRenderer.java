package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity_renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.multiblock.MultiblockRocketry;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.models.EngineModel;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.models.EngineModelData;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.PreviewRenderer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class EngineEntityRenderer implements BlockEntityRenderer<EngineEntity>, PreviewRenderer {
    private static final Map<Block, EngineModel> engineModelMap = new Object2ObjectOpenHashMap<>();

    public EngineEntityRenderer(BlockEntityRendererProvider.Context context) {
        engineModelMap.put(NSPBlocks.THREE_KEROLOX.get(), new EngineModel(context, EngineModelData.ThreeKeroloxLayerLoc, VoxelSpaceProgram.rl( "block/three_kerolox"))) ;
        engineModelMap.put(NSPBlocks.TWO_KEROLOX.get(), new EngineModel(context, EngineModelData.TwoKeroloxLayerLoc, VoxelSpaceProgram.rl( "block/two_kerolox"))) ;
        this.registerPreviewRenderer(context, NSPBlocks.THREE_KEROLOX.get(), NSPBlocks.TWO_KEROLOX.get());
    }

    @Override
    public void render(EngineEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack,
                       @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        this.renderEngine(
                pBlockEntity.getBlockState().getBlock(),
                pBlockEntity.getFacingRot(),
                pPoseStack,
                pBuffer,
                pBlockEntity.getGimbalXrot(),
                pBlockEntity.getGimbalYrot(),
                pPackedLight,
                pPackedOverlay,
                this.getMaterial(pBlockEntity.getBlockState().getBlock()),
                RenderType::entitySolid,
                new float[]{1.0f, 1.0f, 1.0f, 1.0f}
        );
    }

    @Override
    public void renderPreview(BlockState blockState, float pPartialTick, PoseStack pPoseStack,
                              MultiBufferSource pBuffer, Material material, float[] color) {
        this.renderEngine(
                blockState.getBlock(),
                blockState.getValue(BlockStateProperties.FACING).getRotation(),
                pPoseStack,
                pBuffer,
                0.0f,
                0.0f,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                material,
                RenderType::entityTranslucent,
                color
        );
    }

    protected void renderEngine(Block block, Quaternionf facingRot, PoseStack pPoseStack,
                                @NotNull MultiBufferSource pBuffer, float xRot, float yRot, int pPackedLight, int pPackedOverlay,
                                Material material, Function<ResourceLocation, RenderType> renderTypeFunction, float[] color
    ) {
        pPoseStack.pushPose();
        EngineModel model = engineModelMap.get(block);
        if (model != null) {
            VertexConsumer vertexconsumer = material.buffer(pBuffer, renderTypeFunction);
            pPoseStack.translate(0.5f, 0.5f, 0.5f);
            pPoseStack.mulPose(facingRot);
            Vector3f postRotOffset = getPostRotOffset(block);
            pPoseStack.translate(postRotOffset.x(), postRotOffset.y(), postRotOffset.z());
            model.getGimbaling().xRot = xRot;
            model.getGimbaling().yRot = yRot;
            model.getFixed().render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay,
                    color[0], color[1], color[2], color[3]);
        }
        pPoseStack.popPose();
    }

    @Override
    public Material getMaterial(Block block) {
        EngineModel model = engineModelMap.get(block);
        if (model != null) {
            return model.getMaterial();
        } else {
            return null;
        }
    }

    public Vector3f getPostRotOffset(Block block) {
        if (block instanceof MultiblockRocketry multiblockRocketry && multiblockRocketry.isEvenBlockSize()) {
            return new Vector3f(0.5f, 0.0f, 0.5f);
        } else {
            return new Vector3f(0f, 1.0f, 0f);
        }
    }
}
