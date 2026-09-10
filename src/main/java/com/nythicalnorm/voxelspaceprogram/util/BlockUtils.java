package com.nythicalnorm.voxelspaceprogram.util;

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;

import java.util.function.Predicate;

public class BlockUtils {
    public static LongArrayList floodFill(Level level, BlockPos start, int maxBlocks, Predicate<BlockState> predicate) {
        return floodFillWithHashSet(level, start, maxBlocks, predicate, new LongOpenHashSet());
    }

    public static LongArrayList floodFillWithHashSet(Level level, BlockPos start, int maxBlocks,
                                                     Predicate<BlockState> predicate, LongOpenHashSet visited) {
        LongArrayList result = new LongArrayList();
        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();

        long startLong = start.asLong();

        // Make sure the starting block matches
        if (!predicate.test(level.getBlockState(start))) {
            return result;
        }

        visited.add(startLong);
        queue.enqueue(startLong);

        BlockPos.MutableBlockPos current = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();

        while (!queue.isEmpty() && result.size() < maxBlocks) {
            long currentLong = queue.dequeueLong();
            current.set(currentLong);
            result.add(currentLong);
            for (Direction direction : Direction.values()) {
                neighbor.setWithOffset(current, direction);

                // Don't traverse into unloaded chunks
                if (!level.hasChunkAt(neighbor)) {
                    continue;
                }

                long neighborLong = neighbor.asLong();
                if (!visited.add(neighborLong)) {
                    continue;
                }

                if (!predicate.test(level.getBlockState(neighbor))) {
                    continue;
                }

                queue.enqueue(neighborLong);
            }
        }

        return result;
    }

    public static boolean isEnclosedCuboid(LongArrayList positions, LongOpenHashSet set, Vector3i tankSize) {
        if (positions.isEmpty()) {
            return false;
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        // Find bounding box
        for (int i = 0; i < positions.size(); i++) {
            long packed = positions.getLong(i);

            int x = BlockPos.getX(packed);
            int y = BlockPos.getY(packed);
            int z = BlockPos.getZ(packed);

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;

            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }

        long sizeX = maxX - minX + 1L;
        long sizeY = maxY - minY + 1L;
        long sizeZ = maxZ - minZ + 1L;
        tankSize.set((int) sizeX, (int) sizeY, (int) sizeZ);

        /*
         * If any dimension is less than 3,
         * there cannot be a hollow interior.
         *
         * Therefore require the entire bounding box
         * to be filled.
         */
        if (sizeX < 3 || sizeY < 3 || sizeZ < 3) {

            long expectedVolume = sizeX * sizeY * sizeZ;

            if (positions.size() != expectedVolume) {
                return false;
            }

            // Verify every position in the bounding box exists
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        if (!set.contains(BlockPos.asLong(x, y, z))) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }

        /*
         * All dimensions are >= 3,
         * so require a hollow cuboid shell.
         */
        long expectedSurface = sizeX * sizeY * sizeZ - ((sizeX - 2) * (sizeY - 2) * (sizeZ - 2));

        // Must contain exactly the shell
        if (positions.size() != expectedSurface) {
            return false;
        }

        // X faces
        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!set.contains(BlockPos.asLong(minX, y, z))) {
                    return false;
                }

                if (!set.contains(BlockPos.asLong(maxX, y, z))) {
                    return false;
                }
            }
        }

        // Y faces
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!set.contains(BlockPos.asLong(x, minY, z))) {
                    return false;
                }

                if (!set.contains(BlockPos.asLong(x, maxY, z))) {
                    return false;
                }
            }
        }

        // Z faces
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (!set.contains(BlockPos.asLong(x, y, minZ))) {
                    return false;
                }

                if (!set.contains(BlockPos.asLong(x, y, maxZ))) {
                    return false;
                }
            }
        }

        return true;
    }
}
