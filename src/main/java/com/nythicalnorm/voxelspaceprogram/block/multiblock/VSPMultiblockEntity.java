package com.nythicalnorm.voxelspaceprogram.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

public abstract class VSPMultiblockEntity extends BlockEntity {
    protected final Direction facing;

    public VSPMultiblockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.facing = pBlockState.getValue(BlockStateProperties.FACING);
    }

    public Quaternionf getFacingRot() {
        VSPMultiblock vspMultiblock = (VSPMultiblock) this.getBlockState().getBlock();
        if (vspMultiblock.isHorizontalOnlyRotation()) {
            return this.facing.getRotation().rotateX(Mth.HALF_PI);
        } else {
            return this.facing.getRotation();
        }
    }
}
