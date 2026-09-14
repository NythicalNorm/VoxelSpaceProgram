package com.nythicalnorm.voxelspaceprogram.block.multiblock;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.stream.Stream;

public abstract class VSPMultiblock extends BaseEntityBlock {
    protected final float pixelHeight;
    protected final float pixelXWidth;
    protected final float pixelZWidth;
    protected final VoxelShape[] SHAPES;

    public VSPMultiblock(Properties pProperties, float pPixelHeight, float pPixelXWidth, float pPixelZWidth) {
        super(pProperties);
        this.pixelHeight = pPixelHeight;
        this.pixelXWidth = pPixelXWidth;
        this.pixelZWidth = pPixelZWidth;
        this.SHAPES = generateShapesForAABB();
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
        removeBoundingBlocks(pLevel, this.getBoundingPositions(pPos, pState));
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

    protected VoxelShape getShapeFromDirection(Direction direction) {
        double halfX = this.pixelXWidth * 0.5d;
        double halfY = this.pixelHeight * 0.5d;
        double halfZ = this.pixelZWidth * 0.5d;

        Vector3d posA = new Vector3d(-halfX, -halfY + 16, -halfZ);
        Vector3d posB = new Vector3d(halfX, halfY + 16, halfZ);

        Quaterniond rotD = direction.getRotation().get(new Quaterniond());
        if (this.isHorizontalOnlyRotation()) {
            rotD.rotateX(Mth.HALF_PI);
        }
        posA.rotate(rotD);
        posB.rotate(rotD);

        Vector3d shapeOffsets = addShapeOffsets(halfX, halfY, halfZ);
        shapeOffsets.rotate(rotD);
        posA.add(shapeOffsets);
        posB.add(shapeOffsets);

        Vector3d minPos = new Vector3d(Math.min(posA.x, posB.x), Math.min(posA.y, posB.y), Math.min(posA.z, posB.z));
        Vector3d maxPos = new Vector3d(Math.max(posA.x, posB.x), Math.max(posA.y, posB.y), Math.max(posA.z, posB.z));

        return Block.box(minPos.x + 8, minPos.y + 8, minPos.z + 8, maxPos.x + 8, maxPos.y + 8, maxPos.z + 8);
    }

    protected Vector3d addShapeOffsets(double halfX, double halfY, double halfZ) {
        double xOffset = isEven(this.pixelXWidth) ? (this.isHorizontalOnlyRotation() ? 8.0d - halfX : 8.0d) : 0.0d;
        double yOffset = isEven(this.pixelXWidth) ? (this.isHorizontalOnlyRotation() ? -8.0d - halfY : -8.0d) : 0.0d;
        double zOffset = isEven(this.pixelXWidth) ? (this.isHorizontalOnlyRotation() ? 8.0d - halfZ : 8.0d) : 0.0d;

        return new Vector3d(xOffset, yOffset, zOffset);
    }

    protected boolean isEven(float axis) {
        return blockLength(axis) % 2 == 0;
    }

    protected int blockLength(float axis) {
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

    protected abstract Stream<BlockPos> getBoundingPositions(BlockPos pPos, BlockState blockState);
    protected abstract boolean isHorizontalOnlyRotation();
    protected abstract BlockState getAnyPlacementDirection(BlockPlaceContext pContext, Direction[] directions);
    protected abstract VoxelShape[] generateShapesForAABB();
    protected abstract VoxelShape getShapeForBlockState(BlockState blockState);
    public abstract Block getBoundingBlock();
    public abstract Stream<BlockPos> getBoundingPositions(BlockPos pPos, Direction placeDir);
    public abstract BlockState getUncheckedStateForPlacement(BlockPlaceContext pContext);
}
