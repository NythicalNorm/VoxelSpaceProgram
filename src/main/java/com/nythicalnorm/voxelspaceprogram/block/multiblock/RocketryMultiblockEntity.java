package com.nythicalnorm.voxelspaceprogram.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

public abstract class RocketryMultiblockEntity extends VSPMultiblockEntity {
    public RocketryMultiblockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState, pBlockState.getValue(BlockStateProperties.FACING));
    }

    @Override
    public Quaternionf getFacingRot() {
        return this.facing.getRotation();
    }
}