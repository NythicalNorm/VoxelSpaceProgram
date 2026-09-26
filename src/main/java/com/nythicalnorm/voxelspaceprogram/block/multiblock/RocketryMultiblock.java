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
import org.joml.Vector3f;

import java.util.stream.Stream;

public abstract class RocketryMultiblock extends VSPMultiblock {
    protected static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected final Vector3f renderingOffset;

    public RocketryMultiblock(Properties pProperties, float pPixelHeight, float pPixelXWidth, float pPixelZWidth) {
        super(pProperties, pPixelHeight, pPixelXWidth, pPixelZWidth);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        this.renderingOffset = calculateRenderingOffset(pPixelHeight, pPixelXWidth, pPixelZWidth);
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
    public BlockPos rotateBlockPos(BlockPos pos, Direction direction) {
        return switch (direction) {
            case UP    -> new BlockPos(pos.getX(),  pos.getY(),  pos.getZ());  // Identity
            case DOWN  -> new BlockPos(pos.getX(), -pos.getY(), -pos.getZ());

            // the below stuff is sus, the positions of getX()'s and getZ()'s might need to be switched.
            case NORTH -> new BlockPos(-pos.getX(),  -pos.getZ(), -pos.getY());
            case SOUTH -> new BlockPos(pos.getX(), -pos.getZ(),  pos.getY());
            case EAST  -> new BlockPos(pos.getY(), -pos.getX(), pos.getZ());
            case WEST  -> new BlockPos(-pos.getY(),  -pos.getX(), -pos.getZ());
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

    public Vector3f getRenderingOffset() {
        return renderingOffset;
    }

    private static Vector3f calculateRenderingOffset(float pPixelHeight, float pPixelXWidth, float pPixelZWidth) {
        float xOffset = 0.0f;
        float zOffset = 0.0f;
        float yOffset = 0.0f;

        if (blockLength(pPixelXWidth) % 2 == 0) {
            xOffset = 0.5f;
        }
        if (blockLength(pPixelZWidth) % 2 == 0) {
            zOffset = 0.5f;
        }
        if (blockLength(pPixelHeight) % 2 == 1) {
            yOffset = 1.0f;
        }
        return new Vector3f(xOffset, yOffset, zOffset);
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
