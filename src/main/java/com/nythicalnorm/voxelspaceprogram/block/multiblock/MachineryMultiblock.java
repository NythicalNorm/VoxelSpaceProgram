package com.nythicalnorm.voxelspaceprogram.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public abstract class MachineryMultiblock extends VSPMultiblock{
    public static final DirectionProperty FACING_HORIZONTAL = BlockStateProperties.HORIZONTAL_FACING;

    public MachineryMultiblock(Properties pProperties, float pPixelHeight, float pPixelXWidth, float pPixelZWidth) {
        super(pProperties, pPixelHeight, pPixelXWidth, pPixelZWidth);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING_HORIZONTAL, Direction.NORTH));
    }

    @Override
    protected boolean isHorizontalOnlyRotation() {
        return true;
    }

    @Override
    protected VoxelShape[] generateShapesForAABB() {
        VoxelShape[] voxelShapes = new VoxelShape[4];
        voxelShapes[0] = getShapeFromDirection(Direction.EAST);
        voxelShapes[1] = getShapeFromDirection(Direction.WEST);
        voxelShapes[2] = getShapeFromDirection(Direction.NORTH);
        voxelShapes[3] = getShapeFromDirection(Direction.SOUTH);
        return voxelShapes;
    }

    @Override
    protected VoxelShape getShapeForBlockState(BlockState blockState) {
        switch (blockState.getValue(FACING_HORIZONTAL)) {
            case EAST -> {
                return SHAPES[0];
            } case WEST -> {
                return SHAPES[1];
            } case NORTH -> {
                return SHAPES[2];
            } case SOUTH -> {
                return SHAPES[3];
            }
        }
        return SHAPES[0];
    }

    @Override
    protected Stream<BlockPos> getBoundingPositions(BlockPos pPos, BlockState blockState) {
        return getBoundingPositions(pPos, blockState.getValue(FACING_HORIZONTAL));
    }

    @Override
    public Stream<BlockPos> getBoundingPositions(BlockPos pPos, Direction placeDir) {
        Stream.Builder<BlockPos> builder = Stream.builder();
        int XLength = blockLength(this.pixelXWidth);
        int YLength = blockLength(this.pixelHeight);
        int ZLength = blockLength(this.pixelZWidth);

        BlockPos leftBottomPos = new BlockPos(0, 0, 0);
        BlockPos RightTopPos = new BlockPos(XLength - 1, YLength - 1, ZLength - 1);
        BlockPos centerOffset = new BlockPos(getCenterOffset(XLength), getCenterOffset(YLength), getCenterOffset(ZLength));

        leftBottomPos = rotateBlockPos(leftBottomPos.offset(centerOffset), placeDir);
        RightTopPos = rotateBlockPos(RightTopPos.offset(centerOffset), placeDir);

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

    private int getCenterOffset(int axis) {
        return -(axis - 1) / 2;
    }

    public static BlockPos rotateBlockPos(BlockPos pos, Direction direction) {
        return switch (direction) {
            case NORTH -> new BlockPos( pos.getX(), pos.getY(), pos.getZ());
            case SOUTH -> new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
            case EAST  -> new BlockPos(-pos.getZ(), pos.getY(),  pos.getX());
            case WEST  -> new BlockPos( pos.getZ(), pos.getY(), -pos.getX());
            default -> new BlockPos(0, 0, 0);
        };
    }

    @Override
    protected BlockState getAnyPlacementDirection(BlockPlaceContext pContext, Direction[] directions) {
        Direction actualDirection = pContext.getHorizontalDirection().getOpposite();
        if (checkCanBePlaced(pContext, actualDirection)) {
            return this.defaultBlockState().setValue(FACING_HORIZONTAL, actualDirection);
        }

        for (Direction dir : directions) {
            if (dir.equals(actualDirection)) {
                continue;
            }

            if (dir.getAxis().isHorizontal() && checkCanBePlaced(pContext, dir)) {
                return this.defaultBlockState().setValue(FACING_HORIZONTAL, dir);
            }
        }
        return null;
    }

    @Override
    public BlockState getUncheckedStateForPlacement(BlockPlaceContext pContext) {
        Direction actualDirection = pContext.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING_HORIZONTAL, actualDirection);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING_HORIZONTAL, pRot.rotate(pState.getValue(FACING_HORIZONTAL)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING_HORIZONTAL)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING_HORIZONTAL);
        super.createBlockStateDefinition(pBuilder);
    }
}
