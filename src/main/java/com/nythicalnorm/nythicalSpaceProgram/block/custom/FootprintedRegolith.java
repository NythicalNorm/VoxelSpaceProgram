package com.nythicalnorm.nythicalSpaceProgram.block.custom;

import com.nythicalnorm.nythicalSpaceProgram.util.FootprintedType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class FootprintedRegolith extends Block {
    public static final EnumProperty<FootprintedType> FOOTPRINTTYPE = EnumProperty.<FootprintedType>create("footprinttype", FootprintedType.class);


    public FootprintedRegolith(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FOOTPRINTTYPE, FootprintedType.NOFOOTPRINTS));
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        if (!pLevel.isClientSide()) {
            Enum<FootprintedType> currentState = pState.getValue(FOOTPRINTTYPE);
            if (currentState == FootprintedType.NOFOOTPRINTS) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.TWOBOOTZFACING));
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FOOTPRINTTYPE);
        super.createBlockStateDefinition(pBuilder);
    }
}
