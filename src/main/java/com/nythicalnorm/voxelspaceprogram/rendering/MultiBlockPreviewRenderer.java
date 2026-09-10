package com.nythicalnorm.voxelspaceprogram.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.voxelspaceprogram.Item.RocketryBlockItem;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.multiblock.MultiblockRocketry;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.PreviewRenderer;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.PreviewRendererDispatcher;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiBlockPreviewRenderer {
    private Block lastRenderedBlock;
    private Material lastRenderedMaterial;

    public MultiBlockPreviewRenderer() {
        this.lastRenderedBlock = null;
        this.lastRenderedMaterial = null;
    }

    public void renderBlockItemPreview(PoseStack poseStack, Minecraft mc,
                                       RocketryBlockItem rocketryBlockItem, MultiblockRocketry rocketryBlock
    ) {
        if (mc.hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType().equals(HitResult.Type.BLOCK)) {
            UseOnContext useOnContext = new UseOnContext(mc.player, InteractionHand.MAIN_HAND, blockHitResult);
            BlockPlaceContext blockPlaceContext = new BlockPlaceContext(useOnContext);
            BlockState blockstate = rocketryBlock.getStateForPlacement(blockPlaceContext);
            if (blockstate != null) {
                renderBlockPreview(mc, poseStack, blockstate, blockPlaceContext.getClickedPos(),
                        rocketryBlockItem.canBePlacedWithPlayer(blockPlaceContext, blockstate));
                return;
            }
            BlockState defaultStateForPlacement = rocketryBlock.getUncheckedStateForPlacement(blockPlaceContext);
            if (defaultStateForPlacement != null) {
                renderBlockPreview(mc, poseStack, defaultStateForPlacement, blockPlaceContext.getClickedPos(), false);
            }
        }
   }

    public void renderBlockPreview(Minecraft mc, PoseStack poseStack, BlockState state, BlockPos pos, boolean success) {
        if (state.getBlock() instanceof BaseEntityBlock) {
            PreviewRenderer renderer = ((PreviewRendererDispatcher)mc.getBlockEntityRenderDispatcher()).vsp$getBEPreviewRenderer(state.getBlock());
            if (renderer == null) {
                return;
            }

            if (!state.getBlock().equals(this.lastRenderedBlock) || this.lastRenderedMaterial == null) {
                this.lastRenderedBlock = state.getBlock();
                Material actualMaterial = renderer.getMaterial(this.lastRenderedBlock);
                this.lastRenderedMaterial = new Material(actualMaterial.atlasLocation(), actualMaterial.texture());
            }

            Camera camera = mc.gameRenderer.getMainCamera();
            Vec3 cameraPos = camera.getPosition();

            poseStack.pushPose();
            poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
            float[] green = {0.0f, 1.0f, 0.0f, 0.25f};
            float[] red = {1.0f, 0.0f, 0.0f, 0.25f};

            renderer.renderPreview(state, 0.0f, poseStack, mc.renderBuffers().bufferSource(), lastRenderedMaterial, success ? green : red);
            poseStack.popPose();
        }
    }
}
