package com.nythicalnorm.voxelspaceprogram.block.manufacturing;

import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.SheetMetalRollerEntity;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.MachineryMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SheetMetalRollerBlock extends MachineryMultiblock {
    public SheetMetalRollerBlock(Properties pProperties) {
        super(pProperties, 20, 32, 16);
    }

    @Override
    public Block getBoundingBlock() {
        return VSPBlocks.MACHINERY_BOUNDING_BLOCK.get();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SheetMetalRollerEntity(pPos, pState);
    }
}
