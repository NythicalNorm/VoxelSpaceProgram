package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity;

import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.VSPBlockEntities;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.VSPMultiblockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CommandSeatBE extends VSPMultiblockEntity {
    public CommandSeatBE(BlockPos pPos, BlockState pBlockState) {
        super(VSPBlockEntities.COMMAND_SEAT_BE.get(), pPos, pBlockState);
    }
}
