package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity;

import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.NSPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;

public class PropellantTankEntity extends TankMultiBlockEntity{
    private static final Vector3i maxTankSize = new Vector3i(32, 120, 32);

    public PropellantTankEntity(BlockPos pPos, BlockState pBlockState) {
        super(NSPBlockEntities.PROPELLANT_TANK_BE.get(), pPos, pBlockState);
    }

    @Override
    protected Vector3i getMaxTankSize() {
        return maxTankSize;
    }

    @Override
    public void destroyTank(Level level, BlockPos destructOrigin) {
        super.destroyTank(level, destructOrigin);
        // do some particle effects or something idk...
    }
}
