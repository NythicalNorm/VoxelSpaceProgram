package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity;

import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.NSPBlockEntities;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.multiblock.MultiblockRocketryEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EngineEntity extends MultiblockRocketryEntity {

    public EngineEntity(BlockPos pPos, BlockState pBlockState) {
        super(NSPBlockEntities.ENGINE_BE.get(), pPos, pBlockState);
    }
}
