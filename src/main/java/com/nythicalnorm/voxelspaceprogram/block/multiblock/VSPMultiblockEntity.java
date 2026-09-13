package com.nythicalnorm.voxelspaceprogram.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public abstract class VSPMultiblockEntity extends BlockEntity {
    protected final Direction facing;

    public VSPMultiblockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, Direction direction) {
        super(pType, pPos, pBlockState);
        this.facing = direction;
    }

    public abstract Quaternionf getFacingRot();
}
