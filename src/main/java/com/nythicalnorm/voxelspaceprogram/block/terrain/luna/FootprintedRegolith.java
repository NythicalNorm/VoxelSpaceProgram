package com.nythicalnorm.voxelspaceprogram.block.terrain.luna;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class FootprintedRegolith extends FallingBlock {
    public static final EnumProperty<FootprintedType> FOOTPRINTTYPE = EnumProperty.create("footprinttype", FootprintedType.class);


    public FootprintedRegolith(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FOOTPRINTTYPE, FootprintedType.NOFOOTPRINTS));
    }

    @Override
    public void stepOn(Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull Entity pEntity) {
        if (!pLevel.isClientSide() && pEntity instanceof LivingEntity) {
            Enum<FootprintedType> currentState = pState.getValue(FOOTPRINTTYPE);
            if (currentState == FootprintedType.NOFOOTPRINTS) {

                float entityLookdir = pEntity.getRotationVector().y;
                boolean isRightFoot = pPos.getX() % 2 == 0;

                if (pPos.getZ() % 2 == 0) {
                    isRightFoot = !isRightFoot;
                }
                if (entityLookdir > 120 || entityLookdir < -120 || (entityLookdir < 30 && entityLookdir > -30)) {
                    if (pEntity.isSprinting()) {
                        if (isRightFoot){
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.RIGHTBOOTZFACING));
                        }
                        else {
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.LEFTBOOTZFACING));
                        }
                    }
                    else {
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.TWOBOOTZFACING));
                    }
                }
                else {
                    if (pEntity.isSprinting()) {
                        if (isRightFoot){
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.RIGHTBOOTXFACING));
                        }
                        else {
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.LEFTBOOTXFACING));
                        }
                    }
                    else {
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.TWOBOOTXFACING));
                    }
                }

            }
        }
        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FOOTPRINTTYPE);
        super.createBlockStateDefinition(pBuilder);
    }

    @Override
    public int getDustColor(@NotNull BlockState p_53238_, @NotNull BlockGetter p_53239_, @NotNull BlockPos p_53240_) {
        return 0xc0c0c0;
    }
}
