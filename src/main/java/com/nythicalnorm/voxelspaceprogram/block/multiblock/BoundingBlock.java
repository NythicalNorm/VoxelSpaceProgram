package com.nythicalnorm.voxelspaceprogram.block.multiblock;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.VSPClientBlockExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

// Got this class mostly off of mekanism so thanks to the devs of mekanism.
public class BoundingBlock extends BaseEntityBlock {
    public static final BooleanProperty PLACE_BY_MAIN = BooleanProperty.create("place_by_main");

    public BoundingBlock(Properties pProperties) {
        super(pProperties.requiresCorrectToolForDrops().dynamicShape().noOcclusion()
                .isViewBlocking((state, world, pos) -> false).pushReaction(PushReaction.BLOCK).forceSolidOn());
        this.registerDefaultState(this.stateDefinition.any().setValue(PLACE_BY_MAIN, false));
    }

    @Nullable
    public static BlockPos getMainBlockPos(BlockGetter world, BlockPos thisPos) {
        BlockEntity blockEntity = world.getBlockEntity(thisPos);
        if (blockEntity instanceof BoundingBlockEntity boundingBlockEntity) {
            if (boundingBlockEntity.hasReceivedCoords() && !thisPos.equals(boundingBlockEntity.getMainPos())) {
                return boundingBlockEntity.getMainPos();
            }
        }
        return null;
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(VSPClientBlockExtensions.INSTANCE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PLACE_BY_MAIN);
        super.createBlockStateDefinition(pBuilder);
    }

    @Override
    @Deprecated
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        // Remove the main block if a bounding block gets broken by being directly replaced
        // Note: We only do this if we don't go from bounding block to bounding block
        if (!state.is(newState.getBlock())) {
            BlockPos mainPos = getMainBlockPos(world, pos);
            if (mainPos != null) {
                BlockState mainState = world.getBlockState(mainPos);
                if (!mainState.isAir()) {
                    //Set the main block to air, which will invalidate the rest of the bounding blocks
                    world.removeBlock(mainPos, false);
                }
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    /**
     * {@inheritDoc} Delegate to main {@link Block#getCloneItemStack(BlockState, HitResult, BlockGetter, BlockPos, Player)}.
     */
    @NotNull
    @Override
    public ItemStack getCloneItemStack(@NotNull BlockState state, HitResult target, @NotNull BlockGetter world, @NotNull BlockPos pos, Player player) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos == null) {
            return ItemStack.EMPTY;
        }
        BlockState mainState = world.getBlockState(mainPos);
        return mainState.getBlock().getCloneItemStack(mainState, target, world, mainPos, player);
    }

    @Override
    public boolean onDestroyedByPlayer(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, boolean willHarvest,
                                       FluidState fluidState) {
        if (willHarvest) {
            return true;
        }
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            if (!mainState.isAir()) {
                //Set the main block to air, which will invalidate the rest of the bounding blocks
                mainState.onDestroyedByPlayer(world, mainPos, player, false, mainState.getFluidState());
            }
        }
        return super.onDestroyedByPlayer(state, world, pos, player, false, fluidState);
    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            if (!mainState.isAir()) {
                //Proxy the explosion to the main block which, will set it to air causing it to invalidate the rest of the bounding blocks
                if (world instanceof ServerLevel serverLevel) {
                    LootParams.Builder lootContextBuilder = new LootParams.Builder(serverLevel)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(mainPos))
                            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, mainState.hasBlockEntity() ? serverLevel.getBlockEntity(mainPos) : null)
                            .withOptionalParameter(LootContextParams.THIS_ENTITY, explosion.getExploder());
//                    if (explosion.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
//                        lootContextBuilder.withParameter(LootContextParams.EXPLOSION_RADIUS, explosion.radius);
//                    }
                    mainState.getDrops(lootContextBuilder).forEach(stack -> Block.popResource(serverLevel, mainPos, stack));
                }
                mainState.onBlockExploded(world, mainPos, explosion);
            }
        }
        super.onBlockExploded(state, world, pos, explosion);
    }

    @Override
    @Deprecated
    public void spawnAfterBreak(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull ItemStack stack, boolean dropExperience) {
        BlockPos mainPos = getMainBlockPos(level, pos);
        if (mainPos != null) {
            BlockState mainState = level.getBlockState(mainPos);
            if (!mainState.isAir()) {
                //Proxy the call to the main block even though none of our blocks currently make use of this method
                mainState.spawnAfterBreak(level, mainPos, stack, dropExperience);
            }
        }
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);
    }

    @Override
    public void playerDestroy(@NotNull Level world, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, BlockEntity te,
                              @NotNull ItemStack stack) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            mainState.getBlock().playerDestroy(world, player, mainPos, mainState, world.getBlockEntity(mainPos), stack);
        } else {
            super.playerDestroy(world, player, pos, state, te, stack);
        }
        world.removeBlock(pos, false);
    }

    @Override
    @Deprecated
    public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos == null) {
            return super.getDestroyProgress(state, player, world, pos);
        }
        return world.getBlockState(mainPos).getDestroyProgress(player, world, mainPos);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos == null) {
            return super.getExplosionResistance(state, world, pos, explosion);
        }
        return world.getBlockState(mainPos).getExplosionResistance(world, mainPos, explosion);
    }

    @NotNull
    @Override
    @Deprecated
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return proxyShape(world, pos, context, BlockStateBase::getShape);
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return proxyShape(world, pos, context, BlockStateBase::getCollisionShape);
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return proxyShape(world, pos, context, BlockStateBase::getVisualShape);
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return proxyShape(world, pos, null, (s, level, p, ctx) -> s.getOcclusionShape(level, p));
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getBlockSupportShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return proxyShape(world, pos, null, (s, level, p, ctx) -> s.getBlockSupportShape(level, p));
    }

    @NotNull
    @Override
    @Deprecated
    public VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return proxyShape(world, pos, null, (s, level, p, ctx) -> s.getInteractionShape(level, p));
    }

    //Context should only be null if there is none, and it isn't used in the shape proxy
    private VoxelShape proxyShape(BlockGetter world, BlockPos pos, @Nullable CollisionContext context, ShapeProxy proxy) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos == null) {
            //If we don't have a main pos, then act as if the block is empty so that we can move into it properly
            BlockState boundingBoxState = world.getBlockState(pos);
            if (boundingBoxState.is(this)) {
                if (boundingBoxState.getValue(PLACE_BY_MAIN)) {
                    return Shapes.empty();
                }
            } else {
                return Shapes.block();
            }
        }
        BlockState mainState;
        try {
            mainState = world.getBlockState(mainPos);
        } catch (ArrayIndexOutOfBoundsException e) {
            VoxelSpaceProgram.logError("Error getting bounding block shape, for position " + pos + ", with main position." + mainPos + " World of type " + world.getClass().getName());
            return Shapes.empty();
        }
        VoxelShape shape = proxy.getShape(mainState, world, mainPos, context);
        BlockPos offset = pos.subtract(mainPos);
        return shape.move(-offset.getX(), -offset.getY(), -offset.getZ());
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull PathComputationType type) {
        return false;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BoundingBlockEntity(pPos, pState);
    }

    private interface ShapeProxy {
        VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);
    }
}
