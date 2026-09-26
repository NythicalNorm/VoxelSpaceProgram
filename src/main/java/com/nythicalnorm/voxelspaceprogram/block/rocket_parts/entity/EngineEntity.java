package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity;

import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.VSPBlockEntities;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.RocketryMultiblockEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.EngineBlock;
import com.nythicalnorm.voxelspaceprogram.rendering.plumes.ClientPlumeHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EngineEntity extends RocketryMultiblockEntity {

    public EngineEntity(BlockPos pPos, BlockState pBlockState) {
        super(VSPBlockEntities.ENGINE_BE.get(), pPos, pBlockState);
    }

    protected EngineBlock getEngineBlock() {
        return ((EngineBlock) this.getBlockState().getBlock());
    }

    public float getGimbalXrot() {
        return 0.0f;
    }

    public float getGimbalYrot() {
        return 0.0f;
    }

    public boolean isEnabled() {
        return this.getBlockState().getValue(EngineBlock.ENABLED);
    }

    public void startEngine() {
        this.getEngineBlock().setEnabledState(this.getLevel(), this.getBlockPos(), true);
    }

    public void stopEngine() {
        this.getEngineBlock().setEnabledState(this.getLevel(), this.getBlockPos(), false);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!this.isEnabled()) {
            return;
        }
    }

    @Override
    public void onLoad() {
        if (this.getLevel() != null && this.getLevel().isClientSide()) {
            ClientPlumeHandler.addEngineBlockPlume(this, this.getBlockPos());
        }
        super.onLoad();
    }

    @Override
    public void setRemoved() {
        if (this.getLevel() != null && this.getLevel().isClientSide()) {
            ClientPlumeHandler.removeEngineBlockPlume(this.getBlockPos());
        }
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        if (this.getLevel() != null && this.getLevel().isClientSide()) {
            ClientPlumeHandler.removeEngineBlockPlume(this.getBlockPos());
        }
        super.onChunkUnloaded();
    }
}
