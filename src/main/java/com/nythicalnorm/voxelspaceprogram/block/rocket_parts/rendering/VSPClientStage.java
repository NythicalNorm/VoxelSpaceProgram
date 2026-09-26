package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering;

import com.nythicalnorm.voxelspaceprogram.rendering.MultiBlockPreviewRenderer;
import com.nythicalnorm.voxelspaceprogram.rendering.plumes.PlumeRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface VSPClientStage {
    PlumeRenderer vsp$getPlumeManager();

    MultiBlockPreviewRenderer vsp$multiBlockPreviewRenderer();
}
