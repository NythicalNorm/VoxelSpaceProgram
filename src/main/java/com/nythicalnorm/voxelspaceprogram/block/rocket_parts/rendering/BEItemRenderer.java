package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.voxelspaceprogram.Item.RocketryBlockItem;
import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineEntity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BEItemRenderer extends BlockEntityWithoutLevelRenderer {
    private Map<Block, BlockEntity> blockEntityRelation;

    public BEItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (blockEntityRelation == null) {
            populateBlockEntitesToRender();
        }
        if (pStack.getItem() instanceof RocketryBlockItem blockItem) {
            BlockEntity entity =  blockEntityRelation.get(blockItem.getBlock());
            if (entity != null) {
                Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(entity, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            }
        }
    }

    private void populateBlockEntitesToRender() {
        blockEntityRelation = new Object2ObjectOpenHashMap<>();
        blockEntityRelation.put(VSPBlocks.THREE_KEROLOX.get(), new EngineEntity(BlockPos.ZERO, VSPBlocks.THREE_KEROLOX.get().defaultBlockState()));
        blockEntityRelation.put(VSPBlocks.TWO_KEROLOX.get(), new EngineEntity(BlockPos.ZERO, VSPBlocks.TWO_KEROLOX.get().defaultBlockState()));
    }
}
