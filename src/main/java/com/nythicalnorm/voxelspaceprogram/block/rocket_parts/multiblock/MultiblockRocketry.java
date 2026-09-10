package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.multiblock;

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
        boolean isWidthEven = Math.floorDiv((int) pixelWidth, 16) % 2 == 0;
        double halfHeight = pixelHeight * 0.5d;
        double halfWidth = pixelWidth * 0.5d;
        voxelShapes[0] = getShapeFromDirection(Direction.UP, halfWidth, halfHeight, isWidthEven);
        voxelShapes[1] = getShapeFromDirection(Direction.DOWN, halfWidth, halfHeight, isWidthEven);
        voxelShapes[2] = getShapeFromDirection(Direction.EAST, halfWidth, halfHeight, isWidthEven);
        voxelShapes[3] = getShapeFromDirection(Direction.WEST, halfWidth, halfHeight, isWidthEven);
        voxelShapes[4] = getShapeFromDirection(Direction.NORTH, halfWidth, halfHeight, isWidthEven);
        voxelShapes[5] = getShapeFromDirection(Direction.SOUTH, halfWidth, halfHeight, isWidthEven);

        return voxelShapes;
    }

    private static VoxelShape getShapeFromDirection(Direction direction, double halfWidth, double halfHeight, boolean isWidthEven) {
        Vector3d posA = new Vector3d(-halfWidth, -halfHeight + 16, -halfWidth);
        Vector3d posB = new Vector3d(halfWidth, halfHeight + 16, halfWidth);

        Quaterniond rotD = direction.getRotation().get(new Quaterniond());
        posA.rotate(rotD);
        posB.rotate(rotD);

        if (!isWidthEven) {
            Vector3d oddExtraPos = new Vector3d(8.0d, -8.0d, 8.0d);
            oddExtraPos.rotate(rotD);
            posA.add(oddExtraPos);
            posB.add(oddExtraPos);
        }

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

    public Stream<BlockPos> getPositions(BlockPos pPos, Direction placeDir) {
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

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction[] nearestLookingDirections = pContext.getNearestLookingDirections();
        return getAnyPlacementDirection(pContext, nearestLookingDirections);
    }

    //for preview drawing purposes
    public BlockState getUncheckedStateForPlacement(BlockPlaceContext pContext) {
        Direction actualDirection = pContext.getNearestLookingDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, actualDirection);
    }

    @Override
    protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
        this.getPositions(pPos, pState.getValue(FACING)).forEach(blockPos ->
                super.spawnDestroyParticles(pLevel, pPlayer, blockPos, pState)
        );
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
