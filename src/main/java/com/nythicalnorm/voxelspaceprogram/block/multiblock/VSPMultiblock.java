package com.nythicalnorm.voxelspaceprogram.block.multiblock;

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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.stream.Stream;

public abstract class VSPMultiblock extends BaseEntityBlock {
    protected static final DirectionProperty FACING = BlockStateProperties.FACING;

    protected final boolean isHorizontalOnlyRotation;
    protected final float pixelHeight;
    protected final float pixelXWidth;
    protected final float pixelZWidth;
    protected final VoxelShape[] SHAPES;
    protected final Vector3f renderingOffset;

    public VSPMultiblock(Properties pProperties, float pPixelHeight, float pPixelXWidth, float pPixelZWidth, boolean isHorizontalOnlyRotation) {
        super(pProperties);
        this.pixelHeight = pPixelHeight;
        this.pixelXWidth = pPixelXWidth;
        this.pixelZWidth = pPixelZWidth;
        this.isHorizontalOnlyRotation = isHorizontalOnlyRotation;
        this.SHAPES = generateShapesForAABB();
        this.renderingOffset = calculateRenderingOffset(pPixelHeight, pPixelXWidth, pPixelZWidth);
        if (this.isHorizontalOnlyRotation()) {
            this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        } else {
            this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.DOWN));
        }
    }

    protected boolean isHorizontalOnlyRotation() {
        return this.isHorizontalOnlyRotation;
    }

    protected VoxelShape[] generateShapesForAABB() {
        VoxelShape[] voxelShapes = this.isHorizontalOnlyRotation ? new VoxelShape[4] : new VoxelShape[6];

        voxelShapes[0] = getShapeFromDirection(Direction.NORTH);
        voxelShapes[1] = getShapeFromDirection(Direction.SOUTH);
        voxelShapes[2] = getShapeFromDirection(Direction.EAST);
        voxelShapes[3] = getShapeFromDirection(Direction.WEST);

        if (!this.isHorizontalOnlyRotation) {
            voxelShapes[4] = getShapeFromDirection(Direction.UP);
            voxelShapes[5] = getShapeFromDirection(Direction.DOWN);
        }
        return voxelShapes;
    }

    protected VoxelShape getShapeForBlockState(BlockState blockState) {
        switch (blockState.getValue(FACING)) {
            case NORTH -> {
                return SHAPES[0];
            } case SOUTH -> {
                return SHAPES[1];
            } case EAST -> {
                return SHAPES[2];
            } case WEST -> {
                return SHAPES[3];
            } case UP -> {
                return this.isHorizontalOnlyRotation ? SHAPES[0] : SHAPES[4];
            } case DOWN -> {
                return this.isHorizontalOnlyRotation ? SHAPES[0] : SHAPES[5];
            }
        }
        return SHAPES[0];
    }

    public BlockPos rotateBlockPos(BlockPos pos, Direction direction) {
        if (this.isHorizontalOnlyRotation()) {
            return switch (direction) {
                case NORTH -> new BlockPos( pos.getX(), pos.getY(), pos.getZ());
                case SOUTH -> new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
                case EAST  -> new BlockPos(-pos.getZ(), pos.getY(),  pos.getX());
                case WEST  -> new BlockPos( pos.getZ(), pos.getY(), -pos.getX());
                default -> new BlockPos(0, 0, 0);
            };
        } else {
            return switch (direction) {
                case UP -> new BlockPos(pos.getX(), pos.getY(), pos.getZ());  // Identity
                case DOWN -> new BlockPos(pos.getX(), -pos.getY(), -pos.getZ());

                // the below stuff is sus, the positions of getX()'s and getZ()'s might need to be switched.
                case NORTH -> new BlockPos(-pos.getX(), -pos.getZ(), -pos.getY());
                case SOUTH -> new BlockPos(pos.getX(), -pos.getZ(), pos.getY());
                case EAST -> new BlockPos(pos.getY(), -pos.getX(), -pos.getZ());
                case WEST -> new BlockPos(-pos.getY(), -pos.getX(), pos.getZ());
            };
        }
    }

    protected Stream<BlockPos> getBoundingPositions(BlockPos pPos, BlockState blockState) {
        return getBoundingPositions(pPos, blockState.getValue(FACING));
    }

    protected BlockState getAnyPlacementDirection(BlockPlaceContext pContext, Direction[] directions) {
        Direction actualDirection = this.isHorizontalOnlyRotation() ? pContext.getHorizontalDirection().getOpposite() :
                pContext.getNearestLookingDirection().getOpposite();

        if (checkCanBePlaced(pContext, actualDirection)) {
            return this.defaultBlockState().setValue(FACING, actualDirection);
        }

        for (Direction dir : directions) {
            if (dir.equals(actualDirection) || (this.isHorizontalOnlyRotation() && !dir.getAxis().isHorizontal())) {
                continue;
            }

            if (checkCanBePlaced(pContext, dir)) {
                return this.defaultBlockState().setValue(FACING, dir);
            }
        }
        return null;
    }

    private static Vector3f calculateRenderingOffset(float pPixelHeight, float pPixelXWidth, float pPixelZWidth) {
        float xOffset = 0.0f;
        float zOffset = 0.0f;
        float yOffset = 0.0f;

        if (isEven(pPixelXWidth)) {
            xOffset = 0.5f;
        }
        if (isEven(pPixelZWidth)) {
            zOffset = 0.5f;
        }
        if (!isEven(pPixelHeight)) {
            yOffset = 1.0f;
        }
        return new Vector3f(xOffset, yOffset, zOffset);
    }

    public Vector3f getRenderingOffset() {
        return renderingOffset;
    }

    protected boolean checkCanBePlaced(BlockPlaceContext pContext, Direction placeDir) {
        return getBoundingPositions(pContext.getClickedPos(), placeDir).allMatch(searchPos -> {
            BlockState state = pContext.getLevel().getBlockState(searchPos);
            return state.canBeReplaced(pContext);
        });
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        placeBoundingBlocks(pLevel, pPos, this.getBoundingPositions(pPos, pState));
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pNewState.getBlock().equals(this)) {
            removeBoundingBlocks(pLevel, this.getBoundingPositions(pPos, pState));
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    public void removeBoundingBlocks(Level level, Stream<BlockPos> boundaryBlocks) {
        boundaryBlocks.forEach(p -> {
            BlockState boundingState = level.getBlockState(p);
            if (!boundingState.isAir()) {
                //The state might be air if we broke a bounding block first
                if (boundingState.getBlock() instanceof BoundingBlock) {
                    level.removeBlock(p, false);
                } else {
                    VoxelSpaceProgram.logWarn("Skipping removing block, expected bounding block but the block at "+ p + " in " + level.dimension().location());
                }
            }
        });
    }

    public void placeBoundingBlocks(Level level, BlockPos orig, Stream<BlockPos> boundaryBlocks) {
        boundaryBlocks.forEach(boundingLocation -> {
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

    public Stream<BlockPos> getBoundingPositions(BlockPos pPos, Direction placeDir) {
        Stream.Builder<BlockPos> builder = Stream.builder();
        int XLength = blockLength(this.pixelXWidth);
        int YLength = blockLength(this.pixelHeight);
        int ZLength = blockLength(this.pixelZWidth);

        BlockPos leftBottomPos = new BlockPos(0, 0, 0);
        BlockPos RightTopPos = new BlockPos(XLength - 1, YLength - 1, ZLength - 1);
        BlockPos centerOffset = new BlockPos(getCenterOffset(XLength), 0, getCenterOffset(ZLength));

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

    protected VoxelShape getShapeFromDirection(Direction direction) {
        return this.isHorizontalOnlyRotation() ? horizontalRotateVoxelShape(getDefaultVoxelShape(), direction) :
                rotateVoxelShape(getDefaultVoxelShape(), direction);
    }

    protected VoxelShape getDefaultVoxelShape() {
        Vector3d posA = new Vector3d(0.0d, 0.0d, 0.0d);
        Vector3d posB = new Vector3d(this.pixelXWidth, this.pixelHeight, this.pixelZWidth);
        Vector3d centerOffset = this.getCenterOffset();

        return Block.box(
            posA.x + centerOffset.x(),posA.y + centerOffset.y(),posA.z + centerOffset.z(),
            posB.x + centerOffset.x(),posB.y + centerOffset.y(),posB.z + centerOffset.z()
        );
    }

    public static VoxelShape horizontalRotateVoxelShape(VoxelShape shape, Direction direction) {
        if (direction == Direction.NORTH) {
            return shape;
        }

        VoxelShape result = Shapes.empty();

        for (AABB box : shape.toAabbs()) {
            double minX = box.minX;
            double minY = box.minY;
            double minZ = box.minZ;
            double maxX = box.maxX;
            double maxY = box.maxY;
            double maxZ = box.maxZ;

            AABB rotated;

            switch (direction) {
                case EAST -> rotated = new AABB(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX);
                case SOUTH -> rotated = new AABB(1.0 - maxX, minY, 1.0 - maxZ, 1.0 - minX, maxY, 1.0 - minZ);
                case WEST -> rotated = new AABB(minZ, minY, 1.0 - maxX,maxZ, maxY, 1.0 - minX);
                default -> rotated = box;
            }

            result = Shapes.or(result, Shapes.create(rotated));
        }

        return result.optimize();
    }

    public static VoxelShape rotateVoxelShape(VoxelShape shape, Direction direction) {
        if (direction == Direction.UP) {
            return shape;
        }

        VoxelShape result = Shapes.empty();

        for (AABB box : shape.toAabbs()) {
            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double minZ = Double.POSITIVE_INFINITY;

            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            double maxZ = Double.NEGATIVE_INFINITY;

            // Transform all 8 corners of the AABB
            for (int x = 0; x <= 1; x++) {
                for (int y = 0; y <= 1; y++) {
                    for (int z = 0; z <= 1; z++) {
                        double px = x == 0 ? box.minX : box.maxX;
                        double py = y == 0 ? box.minY : box.maxY;
                        double pz = z == 0 ? box.minZ : box.maxZ;

                        Vector3d rotated = rotatePoint(px, py, pz, direction);

                        minX = Math.min(minX, rotated.x);
                        minY = Math.min(minY, rotated.y);
                        minZ = Math.min(minZ, rotated.z);

                        maxX = Math.max(maxX, rotated.x);
                        maxY = Math.max(maxY, rotated.y);
                        maxZ = Math.max(maxZ, rotated.z);
                    }
                }
            }

            result = Shapes.or(
                    result,
                    Shapes.create(new AABB(
                            minX, minY, minZ,
                            maxX, maxY, maxZ
                    ))
            );
        }

        return result.optimize();
    }

    private static Vector3d rotatePoint(
            double x,
            double y,
            double z,
            Direction direction
    ) {
        // Move origin to the center of the block
        x -= 0.5;
        y -= 0.5;
        z -= 0.5;

        Vector3d point = new Vector3d();

        switch (direction) {
            case DOWN -> point.set(x, -y, -z);
            case NORTH -> point.set(-x, -z, -y);
            case SOUTH -> point.set(x, -z, y);
            case EAST -> point.set(y, -x, -z);
            case WEST -> point.set(-y, -x, z);
            default -> point.set(x, y, z);
        }

        // Move origin back to the block corner
        return point.add(0.5, 0.5, 0.5);
    }

    public Vector3d getCenterOffset() {
        int xLength = blockLength(this.pixelXWidth);
        int zLength = blockLength(this.pixelZWidth);

        Vector3d offset = new Vector3d(
                getCenterOffset(xLength) * 16.0d,
                0.0d,
                getCenterOffset(zLength) * 16.0d
        );

        if (!this.isHorizontalOnlyRotation()) {
            double xAdd = ((xLength * 16) - this.pixelXWidth) * 0.5d;
            double zAdd = ((zLength * 16) - this.pixelZWidth) * 0.5d;
            offset.add(xAdd, 0.0d, zAdd);
        }

        return offset;
    }

    protected static boolean isEven(float axis) {
        return blockLength(axis) % 2 == 0;
    }

    protected static int blockLength(float axis) {
        return (int) Math.ceil((axis - 1.0f)/16.0f);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction[] nearestLookingDirections = pContext.getNearestLookingDirections();
        return getAnyPlacementDirection(pContext, nearestLookingDirections);
    }

    @Override
    protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
        this.getBoundingPositions(pPos, pState).forEach(blockPos ->
                super.spawnDestroyParticles(pLevel, pPlayer, blockPos, pState)
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

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

    public int getMaxBlockSize() {
        return this.blockLength(Math.max(this.pixelHeight, Math.max(this.pixelXWidth, this.pixelZWidth)));
    }

    public float getPixelHeight() {
        return pixelHeight;
    }

    public float getPixelXWidth() {
        return pixelXWidth;
    }

    public float getPixelZWidth() {
        return pixelZWidth;
    }

    //for preview drawing purposes
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

    public abstract Block getBoundingBlock();
}
