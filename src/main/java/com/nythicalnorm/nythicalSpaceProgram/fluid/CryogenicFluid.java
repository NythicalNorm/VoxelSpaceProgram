package com.nythicalnorm.nythicalSpaceProgram.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class CryogenicFluid extends LiquidBlock {

    public CryogenicFluid(FlowingFluid pFluid, Properties pProperties) {
        super(pFluid, pProperties);
    }

    public CryogenicFluid(java.util.function.Supplier<? extends FlowingFluid> pFluid, BlockBehaviour.Properties pProperties) {
        super(pFluid, pProperties);
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.randomTick(pState, pLevel, pPos, pRandom);
        if (!pLevel.getFluidState(pPos.above()).is(this.getFluid())) {
            evaporate(pLevel, pPos);
        }
    }

    private void evaporate(ServerLevel pLevel, BlockPos pPos) {
        pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
        //to play the fizz effect
        pLevel.levelEvent(1501, pPos, 0);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        if (!pLevel.isClientSide() && pLevel.dimensionType().ultraWarm()){
            evaporate((ServerLevel) pLevel, pPos);
        }

    }

    private void placeSnow(Level pLevel, BlockPos pPos) {
        int i1 = pLevel.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT);
        if (i1 > 0) {
            BlockState blockstate = pLevel.getBlockState(pPos);
            if (blockstate.is(Blocks.SNOW)) {
                int k = blockstate.getValue(SnowLayerBlock.LAYERS);
                if (k < Math.min(i1, 8)) {
                    BlockState blockstate1 = blockstate.setValue(SnowLayerBlock.LAYERS, k + 1);
                    Block.pushEntitiesUp(blockstate, blockstate1, pLevel, pPos);
                    pLevel.setBlockAndUpdate(pPos, blockstate1);
                }
            } else {
                pLevel.setBlockAndUpdate(pPos, Blocks.SNOW.defaultBlockState());
            }
        }
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pLevel.isClientSide()) {
            return;
        }
        pEntity.hurt(pLevel.damageSources().freeze(), 5);
        pEntity.setIsInPowderSnow(true);
        super.entityInside(pState, pLevel, pPos, pEntity);
    }
}
