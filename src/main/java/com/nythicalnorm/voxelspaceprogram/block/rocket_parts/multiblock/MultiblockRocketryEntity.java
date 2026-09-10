package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

public class MultiblockRocketryEntity extends BlockEntity {
    private final Direction Facing;

    public MultiblockRocketryEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        Facing = pBlockState.getValue(BlockStateProperties.FACING);
    }

    public Quaternionf getFacingRot() {
        return Facing.getRotation();
    }
}
