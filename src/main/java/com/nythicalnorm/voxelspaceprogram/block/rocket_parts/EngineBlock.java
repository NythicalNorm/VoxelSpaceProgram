package com.nythicalnorm.voxelspaceprogram.block.rocket_parts;

import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.VSPBlockEntities;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineProperties;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.RocketryMultiblock;
import com.nythicalnorm.voxelspaceprogram.rendering.plumes.PlumeSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class EngineBlock extends RocketryMultiblock {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    private final EngineProperties engineProperties;
    private final PlumeSettings plumeSettings;

    public EngineBlock(Properties pProperties, EngineProperties pEngineProperties, PlumeSettings plumeSettings) {
        super(pProperties.lightLevel(state -> state.getValue(ENABLED) ? 15 : 0),
                pEngineProperties.getBlockSize(),
                pEngineProperties.getPixelHeight(),
                pEngineProperties.getPixelWidth()
        );
        this.plumeSettings = plumeSettings;
        this.registerDefaultState(this.defaultBlockState().setValue(ENABLED, false));

        this.engineProperties = pEngineProperties;
    }

    public EngineProperties getEngineProperties() {
        return engineProperties;
    }

    public PlumeSettings getPlumeSettings() {
        return plumeSettings;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            boolean isEnabled = pState.getValue(ENABLED);
            EngineEntity engineEntity = (EngineEntity) pLevel.getBlockEntity(pPos);
            if (isEnabled) {
                engineEntity.stopEngine();
            } else {
                engineEntity.startEngine();
            }
        }
        return InteractionResult.SUCCESS;
    }

    public void setEnabledState(Level level, BlockPos blockPos,  boolean isEnabled) {
        BlockState oldState = level.getBlockState(blockPos);
        BlockState newState = oldState.setValue(ENABLED, isEnabled);
        level.setBlock(blockPos, newState, Block.UPDATE_ALL);

        this.getBoundingPositions(blockPos, oldState.getValue(FACING)).forEach(boundingPos -> {
            BlockState boundingNewState =
                    level.getBlockState(boundingPos).setValue(EngineBoundingBlock.ENABLED, isEnabled);

            level.setBlock(boundingPos, boundingNewState, Block.UPDATE_ALL);
        });

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ENABLED);
        super.createBlockStateDefinition(pBuilder);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EngineEntity(pPos, pState);
    }

    @Override
    public Block getBoundingBlock() {
        return VSPBlocks.ENGINE_BOUNDING_BLOCK.get();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(pBlockEntityType, VSPBlockEntities.ENGINE_BE.get(),
                (pLevel, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel, pPos, pState1));
    }
}
