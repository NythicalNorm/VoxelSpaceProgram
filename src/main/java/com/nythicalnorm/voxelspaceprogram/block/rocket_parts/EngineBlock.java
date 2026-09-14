package com.nythicalnorm.voxelspaceprogram.block.rocket_parts;

import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineProperties;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.RocketryMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EngineBlock extends RocketryMultiblock {
    private final EngineProperties engineProperties;

    public EngineBlock(Properties pProperties, EngineProperties pEngineProperties) {
        super(pProperties, pEngineProperties.getBlockSize(), pEngineProperties.getPixelHeight(), pEngineProperties.getPixelWidth());
        this.engineProperties = pEngineProperties;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EngineEntity(pPos, pState);
    }

    @Override
    public Block getBoundingBlock() {
        return VSPBlocks.ROCKETRY_BOUNDING_BLOCK.get();
    }
}
