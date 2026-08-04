package com.nythicalnorm.voxelspaceprogram.block.rocket_parts;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.stream.Stream;

public abstract class MultiblockRocketry extends BaseEntityBlock {
    protected static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected final int blockSize;
    protected final double pixelHeight;
    protected final double pixelWidth;
    protected final VoxelShape[] SHAPES;

    public MultiblockRocketry(Properties pProperties, int pBlockSize, double pPixelHeight, double pPixelWidth) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        this.blockSize = pBlockSize;
        this.pixelHeight = pPixelHeight;
        this.pixelWidth = pPixelWidth;
        this.SHAPES = generateShapesForAABB();
    }

    private VoxelShape[] generateShapesForAABB() {
        VoxelShape[] voxelShapes = new VoxelShape[6];
        double halfHeight = pixelHeight * 0.5d;
        double halfWidth = pixelWidth * 0.5d;
//        voxelShapes[0] = Block.box(-(halfWidth - 8), 0, -(halfWidth - 8), halfWidth + 8, pPixelHeight, halfWidth + 8);
//        voxelShapes[1] = Block.box(-(halfWidth - 8), -(pixelHeight + 16), -(halfWidth - 8), halfWidth + 8, 16, halfWidth + 8);
        voxelShapes[0] = getShapeFromDirection(Direction.UP, halfWidth, halfHeight);
        voxelShapes[1] = getShapeFromDirection(Direction.DOWN, halfWidth, halfHeight);
        voxelShapes[2] = getShapeFromDirection(Direction.EAST, halfWidth, halfHeight);
        voxelShapes[3] = getShapeFromDirection(Direction.WEST, halfWidth, halfHeight);
        voxelShapes[4] = getShapeFromDirection(Direction.NORTH, halfWidth, halfHeight);
        voxelShapes[5] = getShapeFromDirection(Direction.SOUTH, halfWidth, halfHeight);

        return voxelShapes;
    }

    private static VoxelShape getShapeFromDirection(Direction direction, double halfWidth, double halfHeight) {
        Vector3d posA = new Vector3d(-halfWidth, -halfHeight + 16, -halfWidth);
        Vector3d posB = new Vector3d(halfWidth, halfHeight + 16, halfWidth);
        Quaterniond rotD = new Quaterniond();
        direction.getRotation().get(rotD);
        posA.rotate(rotD);
        posB.rotate(rotD);

        Vector3d minPos = new Vector3d(Math.min(posA.x, posB.x), Math.min(posA.y, posB.y), Math.min(posA.z, posB.z));
        Vector3d maxPos = new Vector3d(Math.max(posA.x, posB.x), Math.max(posA.y, posB.y), Math.max(posA.z, posB.z));

        return Block.box(minPos.x + 8, minPos.y + 8, minPos.z + 8, maxPos.x + 8, maxPos.y + 8, maxPos.z + 8);
    }

    private VoxelShape getShapeForBlockState(BlockState blockState) {
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

    protected boolean checkCanBePlaced(BlockPlaceContext pContext, Direction placeDir) {
        return getPositions(pContext.getClickedPos(), placeDir).allMatch(searchPos -> {
            BlockState state = pContext.getLevel().getBlockState(searchPos);
            return state.canBeReplaced(pContext);
        });
    }

    private BlockState getAnyPlacementDirection(BlockPlaceContext pContext, Direction[] directions) {
        for (Direction dir : directions) {
            if (checkCanBePlaced(pContext, dir)) {
                return this.defaultBlockState().setValue(FACING, dir);
            }
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        placeBoundingBlocks(pLevel, pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        removeBoundingBlocks(pLevel, pPos, pState);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    public void removeBoundingBlocks(Level level, BlockPos pos, BlockState state) {
        getPositions(pos, state.getValue(FACING)).forEach(p -> {
            BlockState boundingState = level.getBlockState(p);
            if (!boundingState.isAir()) {
                //The state might be air if we broke a bounding block first
                if (boundingState.is(getBoundingBlock())) {
                    level.removeBlock(p, false);
                } else {
                    VoxelSpaceProgram.logWarn("Skipping removing block, expected bounding block but the block at "+ p + " in " + level.dimension().location());
                }
            }
        });
    }

    public void placeBoundingBlocks(Level level, BlockPos orig, BlockState state) {
        getPositions(orig, state.getValue(FACING)).forEach(boundingLocation -> {
                Block boundingBlock = getBoundingBlock();
                BlockState newState = boundingBlock.defaultBlockState().setValue(BoundingBlock.PLACE_BY_MAIN, true);
                level.setBlock(boundingLocation, newState, Block.UPDATE_ALL);
                if (!level.isClientSide()) {
                    BlockEntity blockEntity = level.getBlockEntity(boundingLocation);
                    if (blockEntity instanceof BoundingBlockEntity boundingBlockEntity) {
                        boundingBlockEntity.setMainLocation(orig);
                    } else {
                        VoxelSpaceProgram.logWarn("Unable to find Bounding Block Tile at: " + boundingLocation);
                    }
                }
        });
    }

    public Stream<BlockPos> getPositions(BlockPos pPos,  Direction placeDir) {
        Stream.Builder<BlockPos> builder = Stream.builder();
        BlockPos cubeCenter = pPos.offset(placeDir.getNormal());
        int minBlockSearch = -(blockSize - 1) / 2;
        int maxBlockSearch = blockSize / 2;

        for (int x = minBlockSearch; x <= maxBlockSearch; x++) {
            for (int y = minBlockSearch; y <= maxBlockSearch; y++) {
                for (int z = minBlockSearch; z <= maxBlockSearch; z++) {
                    BlockPos searchPos = new BlockPos(cubeCenter.getX() + x,cubeCenter.getY() + y, cubeCenter.getZ() + z);
                    if (!searchPos.equals(pPos)) {
                        builder.add(searchPos);
                    }
                }
            }
        }

        return builder.build();
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction[] nearestLookingDirections = pContext.getNearestLookingDirections();
        return getAnyPlacementDirection(pContext, nearestLookingDirections);
    }

    @Override
    protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
        int minBlockPos = -(blockSize - 1) / 2;
        int maxBlockPos = blockSize / 2;
        BlockPos correctedBlockPos = new BlockPos(pPos.getX() + maxBlockPos, pPos.getY() + maxBlockPos, pPos.getZ() + maxBlockPos);

        for (int x = minBlockPos; x <= maxBlockPos; x++) {
            for (int y = minBlockPos; y <= maxBlockPos; y++) {
                for (int z = minBlockPos; z <= maxBlockPos; z++) {
                    BlockPos newPos = new BlockPos(correctedBlockPos.getX() + x, correctedBlockPos.getY()+y, correctedBlockPos.getZ() + z);
                    super.spawnDestroyParticles(pLevel, pPlayer, newPos, pState);
                }
            }
        }

//        getPositions(pPos, pState).forEach(boundingLocation -> {
//            super.spawnDestroyParticles(pLevel, pPlayer, boundingLocation, pState);
//        });
    }

    public abstract Block getBoundingBlock();

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShapeForBlockState(pState);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShapeForBlockState(pState);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return getShapeForBlockState(pState);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return getShapeForBlockState(pState);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState pState) {
        return false;
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
