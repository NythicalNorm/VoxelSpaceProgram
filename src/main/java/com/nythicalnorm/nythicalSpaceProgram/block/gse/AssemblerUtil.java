package com.nythicalnorm.nythicalSpaceProgram.block.gse;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.gse.entity.VehicleAssemblerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AssemblerUtil {
    public static BlockPos getBlockAroundMeHorizontal(BlockPos pos, Block blockType, Level level) {
        for (int x = -1; x <= 1; x ++) {
            for (int z = -1; z <= 1; z ++) {
                BlockPos searchPos = new BlockPos(pos.getX()+x, pos.getY(), pos.getZ()+z);
                BlockState blockState = level.getBlockState(searchPos);
                if (blockState.is(blockType)) {
                    return searchPos;
                }
            }
        }
        return null;
    }


    public static BoundingBox calculateBoundingBox(Block platformBlock, Block scaffoldBlock, BlockPos startingPos, int maxDistance, Level level) {
        int yHeight = startingPos.getY();

        Stack<int[]> fillStack = new Stack<>();
        fillStack.push(new int[]{startingPos.getX(), startingPos.getZ()});

        int MaxX = startingPos.getX() + maxDistance;
        int MinX = startingPos.getX() - maxDistance;

        int MaxZ = startingPos.getZ() + maxDistance;
        int MinZ = startingPos.getZ() - maxDistance;

        int MaxXFound = startingPos.getX();
        int MinXFound = startingPos.getX();
        int MaxZFound = startingPos.getZ();
        int MinZFound = startingPos.getZ();

        List<BlockPos> listOfTraversedPoints = new ArrayList<>();

        while (!fillStack.empty()) {
            int[] current = fillStack.pop();

            int x = current[0];
            int z = current[1];

            BlockPos candidatePos = new BlockPos(x, yHeight, z);
            BlockState candidateBlockState = level.getBlockState(candidatePos);

            if (candidateBlockState.is(platformBlock) && !listOfTraversedPoints.contains(candidatePos)) {
                listOfTraversedPoints.add(candidatePos);

                if (x < MinXFound) {
                    MinXFound = x;
                }

                else if (x > MaxXFound) {
                    MaxXFound = x;
                }

                if (z < MinZFound) {
                    MinZFound = z;
                }

                else if (z > MaxZFound) {
                    MaxZFound = z;
                }

                if (x + 1 < MaxX) {
                    fillStack.push(new int[]{x + 1 , z});
                }

                if (x - 1 > MinX) {
                    fillStack.push(new int[]{x - 1 , z});
                }

                if (z + 1 < MaxZ) {
                    fillStack.push(new int[]{x, z + 1});
                }

                if (z - 1 > MinZ) {
                    fillStack.push(new int[]{x, z - 1});
                }
            }
        }
        NythicalSpaceProgram.log("found: " + listOfTraversedPoints.size());

        int xDiff = (MaxXFound - MinXFound) + 1;
        int zDiff = (MaxZFound - MinZFound) + 1;

//        NythicalSpaceProgram.log("Xdiff: " + xDiff);
//        NythicalSpaceProgram.log("Zsidd: " + zDiff);

        if (xDiff == zDiff && (xDiff*zDiff == listOfTraversedPoints.size())) {
            NythicalSpaceProgram.log("Square");
        } else {
            NythicalSpaceProgram.log("No Square");
        }

        int[] scaffoldHeights = new int[4];
        int foundHeight = 0;

        scaffoldHeights[0] = getScaffoldHeight(MaxXFound, MaxZFound, yHeight + 1, scaffoldBlock, level);
        scaffoldHeights[1] = getScaffoldHeight(MaxXFound, MinZFound, yHeight + 1, scaffoldBlock, level);
        scaffoldHeights[2] = getScaffoldHeight(MinXFound, MinZFound, yHeight + 1, scaffoldBlock, level);
        scaffoldHeights[3] = getScaffoldHeight(MinXFound, MaxZFound, yHeight + 1, scaffoldBlock, level);

        if (scaffoldHeights[0] == scaffoldHeights[1] && scaffoldHeights[2] == scaffoldHeights[3] && scaffoldHeights[1] == scaffoldHeights[2]) {
            if (scaffoldHeights[0] >= 1) {
                foundHeight = scaffoldHeights[0];
            }
        }
        NythicalSpaceProgram.log("height" + foundHeight);

        return new BoundingBox(MinXFound + 1, yHeight + 1, MinZFound + 1, MaxXFound - 1, foundHeight - 1, MaxZFound - 1);
    }

    private static int getScaffoldHeight(int x, int z, int yStartPoint, Block scaffoldBlock,Level pLevel) {
        for (int i = 0; i < VehicleAssemblerEntity.MaxPlatformHeight; i++) {
            BlockPos candidatePos = new BlockPos(x, yStartPoint + i, z);
            BlockState candidateBlockState = pLevel.getBlockState(candidatePos);

            if (!candidateBlockState.is(scaffoldBlock)) {
                return yStartPoint + i - 1;
            }
        }
        return -1;
    }

    public static double getTotalMass(BlockState[][][] alreadySeearchedblockPos) {
//        MassDatapackResolver resolver = MassDatapackResolver.INSTANCE;
//        double totalMass = 0;
//
//        if (resolver == null) {
//            return totalMass;
//        }
//        for (int y = 0; y < alreadySeearchedblockPos.length; y++) {
//            for (int x = 0; x < alreadySeearchedblockPos[y].length; x++) {
//                for (int z = 0; z < alreadySeearchedblockPos[y][x].length; z++) {
//                    Double mass = resolver.getBlockStateMass(alreadySeearchedblockPos[x][y][z]);
//                    if (mass != null) {
//                        totalMass += mass;
//                    }
//                }
//            }
//        }
//        return totalMass;
        return 0;
    }
}
