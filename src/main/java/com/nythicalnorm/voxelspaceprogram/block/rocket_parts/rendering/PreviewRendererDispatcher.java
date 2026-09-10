package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public interface PreviewRendererDispatcher {
    void vsp$registerPreviewRenderer(PreviewRenderer previewRenderer, Block[] blocks);
    @Nullable PreviewRenderer vsp$getPreviewRenderer(Block block);
}
