package com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity;

import com.nythicalnorm.voxelspaceprogram.block.multiblock.VSPMultiblockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SheetMetalRollerEntity extends VSPMultiblockEntity {
    public SheetMetalRollerEntity(BlockPos pPos, BlockState pBlockState) {
        super(VSPBlockEntities.SHEET_METAL_ROLLER_BE.get(), pPos, pBlockState);
    }
}
