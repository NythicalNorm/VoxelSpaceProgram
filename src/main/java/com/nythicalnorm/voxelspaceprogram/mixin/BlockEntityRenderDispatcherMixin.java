package com.nythicalnorm.voxelspaceprogram.mixin;

import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.PreviewRendererDispatcher;
import com.nythicalnorm.voxelspaceprogram.rendering.MultiBlockPreviewRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin implements PreviewRendererDispatcher {
    @Unique
    MultiBlockPreviewRenderer vsp$multiBlockPreviewRenderer = new MultiBlockPreviewRenderer();

    @Override
    public MultiBlockPreviewRenderer vsp$multiBlockPreviewRenderer() {
        return this.vsp$multiBlockPreviewRenderer;
    }
}
