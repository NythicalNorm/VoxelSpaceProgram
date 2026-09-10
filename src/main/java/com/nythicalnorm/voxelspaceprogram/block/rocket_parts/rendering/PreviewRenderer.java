package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public interface PreviewRenderer {
    default void registerPreviewRenderer(BlockEntityRendererProvider.Context context, Block... blocks) {
        ((PreviewRendererDispatcher)context.getBlockEntityRenderDispatcher()).vsp$registerBEPreviewRenderer(this, blocks);
    }

    void renderPreview(BlockState blockState, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, Material material,  float[] color);
    @Nullable Material getMaterial(Block block);
}
