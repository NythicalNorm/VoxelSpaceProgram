package com.nythicalnorm.voxelspaceprogram.block;

import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.multiblock.BoundingBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

public class VSPClientBlockExtensions implements IClientBlockExtensions {
    public static VSPClientBlockExtensions INSTANCE = new VSPClientBlockExtensions();

    @Override
    public boolean addHitEffects(BlockState state, Level world, HitResult target, ParticleEngine manager) {
        if (target.getType() == HitResult.Type.BLOCK && target instanceof BlockHitResult blockTarget) {
            BlockPos pos = blockTarget.getBlockPos();
            BlockPos mainPos = BoundingBlock.getMainBlockPos(world, pos);
            if (mainPos != null) {
                BlockState mainState = world.getBlockState(mainPos);
                if (!mainState.isAir()) {
                    //Copy of ParticleManager#addBlockHitEffects except using the block state for the main position
                    AABB axisalignedbb = state.getShape(world, pos).bounds();
                    double x = pos.getX() + world.random.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2) + 0.1 + axisalignedbb.minX;
                    double y = pos.getY() + world.random.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2) + 0.1 + axisalignedbb.minY;
                    double z = pos.getZ() + world.random.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2) + 0.1 + axisalignedbb.minZ;
                    Direction side = blockTarget.getDirection();
                    switch (side) {
                        case DOWN -> y = pos.getY() + axisalignedbb.minY - 0.1;
                        case UP -> y = pos.getY() + axisalignedbb.maxY + 0.1;
                        case NORTH -> z = pos.getZ() + axisalignedbb.minZ - 0.1;
                        case SOUTH -> z = pos.getZ() + axisalignedbb.maxZ + 0.1;
                        case WEST -> x = pos.getX() + axisalignedbb.minX - 0.1;
                        case EAST -> x = pos.getX() + axisalignedbb.maxX + 0.1;
                    }
                    manager.add(new TerrainParticle((ClientLevel) world, x, y, z, 0, 0, 0, mainState)
                            .updateSprite(mainState, mainPos).setPower(0.2F).scale(0.6F));
                    return true;
                }
            }
        }
        return false;
    }
}
