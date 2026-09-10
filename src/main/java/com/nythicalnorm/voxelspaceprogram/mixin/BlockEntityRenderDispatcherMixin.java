package com.nythicalnorm.voxelspaceprogram.mixin;

import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.PreviewRenderer;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.PreviewRendererDispatcher;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin implements PreviewRendererDispatcher {
    @Unique
    Map<Block, PreviewRenderer> vsp$previewRenderers = new Object2ObjectOpenHashMap<>();

    @Override
    public void vsp$registerPreviewRenderer(PreviewRenderer previewRenderer, Block[] rendererForBlocks) {
        for (Block block : rendererForBlocks) {
            this.vsp$previewRenderers.put(block, previewRenderer);
        }
    }

    @Override
    public PreviewRenderer vsp$getPreviewRenderer(Block block) {
        return this.vsp$previewRenderers.get(block);
    }

}
