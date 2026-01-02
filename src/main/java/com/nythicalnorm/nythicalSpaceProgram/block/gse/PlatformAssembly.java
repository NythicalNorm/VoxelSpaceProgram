package com.nythicalnorm.nythicalSpaceProgram.block.gse;

import com.nythicalnorm.nythicalSpaceProgram.block.BlockFindingStorage;
import com.nythicalnorm.nythicalSpaceProgram.block.gse.entity.VehicleAssemblerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class PlatformAssembly extends Block {
    public PlatformAssembly(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        if (!pLevel.isClientSide()) {
            reCalculateAssembly(pPos, pLevel);
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        if (!pLevel.isClientSide()) {
            reCalculateAssembly(pPos, pLevel);
        }
    }

    private void reCalculateAssembly(BlockPos pPos, Level pLevel) {
      Optional<BlockPos> posOptional = BlockFindingStorage.findAssemblerBlockInRange(pPos, pLevel);
        posOptional.ifPresent(blockPos -> {
            BlockEntity blockEntity = pLevel.getBlockEntity(blockPos);
            if (blockEntity instanceof VehicleAssemblerEntity vehicleAssembler) {
                vehicleAssembler.setRecalculateBox();
            }
        });
    }
}
