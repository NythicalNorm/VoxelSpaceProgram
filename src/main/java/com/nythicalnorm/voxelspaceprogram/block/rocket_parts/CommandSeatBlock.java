package com.nythicalnorm.voxelspaceprogram.block.rocket_parts;

import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.VSPMultiblock;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.CommandSeatBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CommandSeatBlock extends VSPMultiblock {
    public CommandSeatBlock(Properties pProperties) {
        super(pProperties, 32, 16, 32, true);
    }

    @Override
    protected VoxelShape getDefaultVoxelShape() {
        VoxelShape BASE = Block.box(0.0d, 0.0d, 0.0d, 16.0d, 2.0d, 32.0d);
        VoxelShape CHAIR_STAND = Block.box(3.0d, 2.0d, 3.0d, 13.0d, 12.0d, 11.0d);
        VoxelShape CHAIR_SEAT = Block.box(0.0d, 12.0d, 0.0d, 16.0d, 15.0d, 15.0d);
        VoxelShape CHAIR_BACK = Block.box(0.0d, 15.0d, 0.0d, 16.0d, 32.0d, 4.0d);

        VoxelShape PODIUM_STAND = Block.box(3.0d, 2.0d, 23.0d, 13.0d, 14.0d, 31.0d);
        VoxelShape PODIUM_CONTROLS = Block.box(1.0d, 14.0d, 23.0d, 15.0d, 19.0d, 32.0d);
        VoxelShape PODIUM_MONITOR = Block.box(1.0d, 19.0d, 30.0d, 15.0d, 30.0d, 32.0d);

        return Shapes.or(BASE, CHAIR_STAND, CHAIR_SEAT, CHAIR_BACK, PODIUM_STAND, PODIUM_CONTROLS, PODIUM_MONITOR);
    }

    @Override
    public Block getBoundingBlock() {
        return VSPBlocks.ROCKETRY_BOUNDING_BLOCK.get();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CommandSeatBE(pPos, pState);
    }
}
