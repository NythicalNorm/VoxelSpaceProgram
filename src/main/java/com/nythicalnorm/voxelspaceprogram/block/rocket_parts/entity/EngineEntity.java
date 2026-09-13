package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity;

import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.VSPBlockEntities;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.RocketryMultiblockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EngineEntity extends RocketryMultiblockEntity {

    public EngineEntity(BlockPos pPos, BlockState pBlockState) {
        super(VSPBlockEntities.ENGINE_BE.get(), pPos, pBlockState);
    }

    public float getGimbalXrot() {
        return 0.0f;
    }

    public float getGimbalYrot() {
        return 0.0f;
    }
}
