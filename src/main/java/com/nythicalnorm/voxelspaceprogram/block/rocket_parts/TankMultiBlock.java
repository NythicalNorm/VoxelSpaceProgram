package com.nythicalnorm.voxelspaceprogram.block.rocket_parts;

import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.TankMultiBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public abstract class TankMultiBlock extends BaseEntityBlock {
    public static final BooleanProperty ASSEMBLED = BlockStateProperties.ENABLED;

    public TankMultiBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ASSEMBLED, false));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getMainHandItem().isEmpty() && !pState.getValue(ASSEMBLED)) {
            if (!pLevel.isClientSide()) {
                TankMultiBlockEntity tankMultiBlock = (TankMultiBlockEntity) pLevel.getBlockEntity(pPos);
                if (tankMultiBlock != null && tankMultiBlock.tryTankAssembly(pLevel, pPos)) {
                    return InteractionResult.CONSUME;
                } else {
                    // fails to assemble
                    pPlayer.sendSystemMessage(Component.translatable("propellant_tank.voxelspaceprogram.assembly_unable"));
                    return InteractionResult.FAIL;
                }
            } else {
                return InteractionResult.CONSUME;
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pNewState.getBlock().equals(this) && pLevel.getBlockEntity(pPos) instanceof TankMultiBlockEntity tankMultiBlock) {
            if (tankMultiBlock.isTankAssembled() && tankMultiBlock.getHostPos() != null &&
                    pLevel.getBlockEntity(tankMultiBlock.getHostPos()) instanceof TankMultiBlockEntity hostEntity) {
                hostEntity.destroyTank(pLevel, pPos);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ASSEMBLED);
    }
}
