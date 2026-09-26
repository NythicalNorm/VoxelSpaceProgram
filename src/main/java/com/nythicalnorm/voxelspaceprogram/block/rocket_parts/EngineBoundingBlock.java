package com.nythicalnorm.voxelspaceprogram.block.rocket_parts;

import com.nythicalnorm.voxelspaceprogram.block.multiblock.BoundingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class EngineBoundingBlock extends BoundingBlock {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public EngineBoundingBlock(Properties pProperties) {
        super(pProperties.lightLevel(state -> state.getValue(ENABLED) ? 15 : 0));
        this.registerDefaultState(this.defaultBlockState().setValue(ENABLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ENABLED);
        super.createBlockStateDefinition(pBuilder);
    }
}
