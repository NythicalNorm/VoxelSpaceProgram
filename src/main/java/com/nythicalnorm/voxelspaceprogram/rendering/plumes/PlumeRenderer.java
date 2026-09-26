package com.nythicalnorm.voxelspaceprogram.rendering.plumes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.EngineBlock;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity_renderers.EngineEntityRenderer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class PlumeRenderer {
    private final Map<BlockPos, EngineBlockPlume> plumeRenderList;
    private final Minecraft mc;

    public PlumeRenderer(Minecraft mc) {
        this.mc = mc;
        this.plumeRenderList = new Object2ObjectOpenHashMap<>();
    }

    public void renderPlumes(Camera camera, PoseStack poseStack) {
        MultiBufferSource multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        this.plumeRenderList.forEach((blockPos, engineBlockPlume) -> {
            if (isChunkLoaded(blockPos)) {
                engineBlockPlume.render(multiBufferSource, poseStack, camera);
            }
        });
    }

    private boolean isChunkLoaded(BlockPos blockPos) {
        if (mc.level != null) {
            return mc.level.getChunkSource().getChunk(
                    SectionPos.blockToSectionCoord(blockPos.getX()),
                    SectionPos.blockToSectionCoord(blockPos.getZ()),
                    ChunkStatus.FULL, false) != null;
        } else {
            return false;
        }
    }

    public void addEngineBlockPlume(EngineEntity engineEntity, BlockPos blockPos) {
        EngineBlock engineBlock = ((EngineBlock)engineEntity.getBlockState().getBlock());
        EngineEntityRenderer engineEntityRenderer = (EngineEntityRenderer) mc.getBlockEntityRenderDispatcher().getRenderer(engineEntity);
        this.plumeRenderList.put(blockPos,
                new EngineBlockPlume(
                        engineBlock.getPlumeSettings(), engineEntity, blockPos, engineEntityRenderer
                )
        );
    }

    public void removeEngineBlockPlume(BlockPos blockPos) {
        this.plumeRenderList.remove(blockPos);
    }
}
