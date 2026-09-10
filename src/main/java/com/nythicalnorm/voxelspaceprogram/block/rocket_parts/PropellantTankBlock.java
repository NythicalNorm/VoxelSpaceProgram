package com.nythicalnorm.voxelspaceprogram.block.rocket_parts;

import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.PropellantTankEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PropellantTankBlock extends TankMultiBlock{
    public PropellantTankBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PropellantTankEntity(pPos, pState);
    }
}
