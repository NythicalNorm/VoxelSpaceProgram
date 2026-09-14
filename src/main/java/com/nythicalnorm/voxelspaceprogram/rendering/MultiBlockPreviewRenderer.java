package com.nythicalnorm.voxelspaceprogram.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.voxelspaceprogram.Item.MultiBlockItem;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.VSPMultiblock;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.PreviewRenderer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.api.ValkyrienSkies;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MultiBlockPreviewRenderer {
    private Block lastRenderedBlock;
    private Material lastRenderedMaterial;
    private final Map<BaseEntityBlock, BlockEntity> blockEntityCache;

    public MultiBlockPreviewRenderer() {
        this.lastRenderedBlock = null;
        this.lastRenderedMaterial = null;
        this.blockEntityCache = new Object2ObjectOpenHashMap<>();
    }

    public BlockEntity getOrCreateBlockEntity(BaseEntityBlock entityBlock) {
        return blockEntityCache.computeIfAbsent(entityBlock, block ->
                block.newBlockEntity(BlockPos.ZERO, block.defaultBlockState())
        );
    }

    public void renderBlockItemPreview(PoseStack poseStack, Minecraft mc,
                                       MultiBlockItem multiBlockItem, VSPMultiblock multiblock
    ) {
        if (mc.hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType().equals(HitResult.Type.BLOCK)) {
            UseOnContext useOnContext = new UseOnContext(mc.player, InteractionHand.MAIN_HAND, blockHitResult);
            BlockPlaceContext blockPlaceContext = new BlockPlaceContext(useOnContext);
            BlockState blockstate = multiblock.getStateForPlacement(blockPlaceContext);
            if (blockstate != null) {
                renderBlockPreview(mc, poseStack, blockstate, blockPlaceContext.getClickedPos(),
                        multiBlockItem.canBePlacedWithPlayer(blockPlaceContext, blockstate));
                return;
            }
            BlockState defaultStateForPlacement = multiblock.getUncheckedStateForPlacement(blockPlaceContext);
            if (defaultStateForPlacement != null) {
                renderBlockPreview(mc, poseStack, defaultStateForPlacement, blockPlaceContext.getClickedPos(), false);
            }
        }
   }

    public void renderBlockPreview(Minecraft mc, PoseStack poseStack, BlockState state, BlockPos pos, boolean success) {
        if (state.getBlock() instanceof BaseEntityBlock entityBlock) {
            PreviewRenderer renderer = (PreviewRenderer) mc.getBlockEntityRenderDispatcher().getRenderer(getOrCreateBlockEntity(entityBlock));
            if (renderer == null) {
                return;
            }

            if (!entityBlock.equals(this.lastRenderedBlock) || this.lastRenderedMaterial == null) {
                this.lastRenderedBlock = entityBlock;
                Material actualMaterial = renderer.getMaterial(this.lastRenderedBlock);
                this.lastRenderedMaterial = new Material(actualMaterial.atlasLocation(), actualMaterial.texture());
            }

            Camera camera = mc.gameRenderer.getMainCamera();
            Vec3 cameraPos = camera.getPosition();

            poseStack.pushPose();
            Ship ship = ValkyrienSkies.getShipManagingBlock(mc.level, pos);
             if (ship != null) {
                Vector3d worldPos = ship.getTransform().getShipToWorld().transformPosition(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
                poseStack.translate(worldPos.x() - cameraPos.x, worldPos.y() - cameraPos.y, worldPos.z() - cameraPos.z);
                poseStack.mulPose(new Quaternionf(ship.getTransform().getRotation()));
             } else {
                poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
            }

            float[] green = {0.0f, 1.0f, 0.0f, 0.25f};
            float[] red = {1.0f, 0.0f, 0.0f, 0.25f};

            renderer.renderPreview(state, 0.0f, poseStack, mc.renderBuffers().bufferSource(), lastRenderedMaterial, success ? green : red);
            poseStack.popPose();
        }
    }
}
