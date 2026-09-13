package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.voxelspaceprogram.Item.MultiBlockItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BEItemRenderer extends BlockEntityWithoutLevelRenderer {

    public BEItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pStack.getItem() instanceof MultiBlockItem blockItem) {
            BlockEntity entity = ((PreviewRendererDispatcher)Minecraft.getInstance().getBlockEntityRenderDispatcher())
                    .vsp$multiBlockPreviewRenderer().getOrCreateBlockEntity((BaseEntityBlock) blockItem.getBlock());
            if (entity != null) {
                Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(entity, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            }
        }
    }
}
