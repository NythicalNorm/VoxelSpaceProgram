package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering;

import com.nythicalnorm.voxelspaceprogram.rendering.MultiBlockPreviewRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public interface PreviewRendererDispatcher {
    void vsp$registerBEPreviewRenderer(PreviewRenderer previewRenderer, Block[] blocks);
    @Nullable PreviewRenderer vsp$getBEPreviewRenderer(Block block);
    MultiBlockPreviewRenderer vsp$multiBlockPreviewRenderer();
}
