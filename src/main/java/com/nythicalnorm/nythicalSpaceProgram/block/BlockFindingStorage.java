package com.nythicalnorm.nythicalSpaceProgram.block;

import com.nythicalnorm.nythicalSpaceProgram.block.gse.entity.VehicleAssemblerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockFindingStorage {
    private static final List<BlockEntityLoc> assemblerBlockLocation = new ArrayList<>();

    private static class BlockEntityLoc {
        private final BlockPos pos;
        private final ResourceKey<Level> dimensionKey;

        public BlockEntityLoc(BlockPos pos, ResourceKey<Level> dimensionKey) {
            this.pos = pos;
            this.dimensionKey = dimensionKey;
        }
    }

    public static void makeBlockEntityFindable(BlockPos pos, Level level) {
        assemblerBlockLocation.add(new BlockEntityLoc(pos, level.dimension()));
    }

    public static void destroyBlockEntityFindable(BlockPos pos, Level level) {
        assemblerBlockLocation.removeIf(blockEntityLoc -> blockEntityLoc.pos == pos &&
                level.dimension() == blockEntityLoc.dimensionKey);
    }

    public static Optional<BlockPos> findAssemblerBlockInRange(BlockPos pos, Level pLevel) {
        Optional<BlockPos> assemblerBlockPos = Optional.empty();
        int MaxDistanceToSearch = VehicleAssemblerEntity.MaxPlatformSize;

        for (BlockEntityLoc loc : assemblerBlockLocation) {
            if (loc.dimensionKey == pLevel.dimension()) {
                if (Math.abs(loc.pos.getX() - pos.getX()) <= MaxDistanceToSearch && Math.abs(loc.pos.getZ() - pos.getZ()) <= MaxDistanceToSearch
                        && loc.pos.getY() == pos.getY()) {
                    assemblerBlockPos = Optional.of(loc.pos);
                }
            }
        }

        return assemblerBlockPos;
    }
}
