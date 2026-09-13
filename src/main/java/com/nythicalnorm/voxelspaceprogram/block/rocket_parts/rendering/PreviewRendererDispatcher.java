package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering;

import com.nythicalnorm.voxelspaceprogram.rendering.MultiBlockPreviewRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface PreviewRendererDispatcher {
    MultiBlockPreviewRenderer vsp$multiBlockPreviewRenderer();
}
