package com.nythicalnorm.voxelspaceprogram.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public abstract class RocketryMultiblock extends VSPMultiblock {
    protected static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected final int blockSize;

    public RocketryMultiblock(Properties pProperties, int pBlockSize, float pPixelHeight, float pPixelWidth) {
        super(pProperties, pPixelHeight, pPixelWidth, pPixelWidth);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        this.blockSize = pBlockSize;
    }

    @Override
    protected boolean isHorizontalOnlyRotation() {
        return false;
    }

    @Override
    protected VoxelShape[] generateShapesForAABB() {
        VoxelShape[] voxelShapes = new VoxelShape[6];

        voxelShapes[0] = getShapeFromDirection(Direction.UP);
        voxelShapes[1] = getShapeFromDirection(Direction.DOWN);
        voxelShapes[2] = getShapeFromDirection(Direction.EAST);
        voxelShapes[3] = getShapeFromDirection(Direction.WEST);
        voxelShapes[4] = getShapeFromDirection(Direction.NORTH);
        voxelShapes[5] = getShapeFromDirection(Direction.SOUTH);
        return voxelShapes;
    }

    @Override
    protected VoxelShape getShapeForBlockState(BlockState blockState) {
        switch (blockState.getValue(FACING)) {
            case UP -> {
                return SHAPES[0];
            } case DOWN -> {
                return SHAPES[1];
            } case EAST -> {
                return SHAPES[2];
            } case WEST -> {
                return SHAPES[3];
            } case NORTH -> {
                return SHAPES[4];
            } case SOUTH -> {
                return SHAPES[5];
            }
        }
        return SHAPES[0];
    }

    @Override
    protected Stream<BlockPos> getBoundingPositions(BlockPos pPos, BlockState blockState) {
        return getBoundingPositions(pPos, blockState.getValue(FACING));
    }

    @Override
    public Stream<BlockPos> getBoundingPositions(BlockPos pPos, Direction placeDir) {
        if (isEvenBlockSize()) {
            return getEvenPositions(pPos, placeDir);
        } else {
            return getOddPositions(pPos, placeDir);
        }
    }

    public Stream<BlockPos> getEvenPositions(BlockPos pPos, Direction placeDir) {
        Stream.Builder<BlockPos> builder = Stream.builder();
        int minBlockSearch = -(blockSize - 1) / 2;
        int maxBlockSearch = blockSize / 2;

        BlockPos leftBottomPos = new BlockPos(minBlockSearch, 0, minBlockSearch);
        BlockPos RightTopPos = new BlockPos(maxBlockSearch, maxBlockSearch, maxBlockSearch);
        leftBottomPos = rotateBlockPos(leftBottomPos, placeDir);
        RightTopPos = rotateBlockPos(RightTopPos, placeDir);

        int minX = Math.min(leftBottomPos.getX(), RightTopPos.getX());
        int minY = Math.min(leftBottomPos.getY(), RightTopPos.getY());
        int minZ = Math.min(leftBottomPos.getZ(), RightTopPos.getZ());

        int maxX = Math.max(leftBottomPos.getX(), RightTopPos.getX());
        int maxY = Math.max(leftBottomPos.getY(), RightTopPos.getY());
        int maxZ = Math.max(leftBottomPos.getZ(), RightTopPos.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos searchPos = new BlockPos(pPos.getX() + x,pPos.getY() + y, pPos.getZ() + z);
                    if (!searchPos.equals(pPos)) {
                        builder.add(searchPos);
                    }
                }
            }
        }

        return builder.build();
    }

    public Stream<BlockPos> getOddPositions(BlockPos pPos, Direction placeDir) {
        Stream.Builder<BlockPos> builder = Stream.builder();
        int blockCenter = (this.blockSize - 1) / 2;
        BlockPos cubeCenter = pPos.offset(placeDir.getNormal().multiply(blockCenter));

        int blockSearch = (blockSize / 2);

        for (int x = -blockSearch; x <= blockSearch; x++) {
            for (int y = -blockSearch; y <= blockSearch; y++) {
                for (int z = -blockSearch; z <= blockSearch; z++) {
                    BlockPos searchPos = new BlockPos(cubeCenter.getX() + x,cubeCenter.getY() + y, cubeCenter.getZ() + z);
                    if (!searchPos.equals(pPos)) {
                        builder.add(searchPos);
                    }
                }
            }
        }

        return builder.build();
    }

    public boolean isEvenBlockSize() {
        return this.blockSize % 2 == 0;
    }

    public static BlockPos rotateBlockPos(BlockPos pos, Direction direction) {
        return switch (direction) {
            case UP    -> new BlockPos(pos.getX(),  pos.getY(),  pos.getZ());  // Identity
            case DOWN  -> new BlockPos(pos.getX(), -pos.getY(), -pos.getZ());

            case NORTH -> new BlockPos(-pos.getX(),  -pos.getZ(), -pos.getY());
            case SOUTH -> new BlockPos(pos.getX(), -pos.getZ(),  pos.getY());

            case EAST  -> new BlockPos(pos.getZ(),  -pos.getY(), -pos.getX());
            case WEST  -> new BlockPos(-pos.getZ(),  -pos.getY(),  pos.getX());
        };
    }

    protected BlockState getAnyPlacementDirection(BlockPlaceContext pContext, Direction[] directions) {
        Direction actualDirection = pContext.getNearestLookingDirection().getOpposite();
        if (checkCanBePlaced(pContext, actualDirection)) {
            return this.defaultBlockState().setValue(FACING, actualDirection);
        }

        for (Direction dir : directions) {
            if (dir.equals(actualDirection)) {
                continue;
            }

            if (checkCanBePlaced(pContext, dir)) {
                return this.defaultBlockState().setValue(FACING, dir);
            }
        }
        return null;
    }

    //for preview drawing purposes
    @Override
    public BlockState getUncheckedStateForPlacement(BlockPlaceContext pContext) {
        Direction actualDirection = pContext.getNearestLookingDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, actualDirection);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        super.createBlockStateDefinition(pBuilder);
    }
}
