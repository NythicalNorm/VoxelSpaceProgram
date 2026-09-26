package com.nythicalnorm.voxelspaceprogram.block.gse.wires;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class WireBlock extends Block {
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    private static final VoxelShape CENTER = Block.box(5.0d, 5.0d, 5.0d, 11.0d, 11.0d, 11.0d);
    private static final VoxelShape UP_SHAPE = Block.box(5.0d, 11.0d, 5.0d, 11.0d, 16.0d, 11.0d);
    private static final VoxelShape DOWN_SHAPE = Block.box(5.0d, 0.0d, 5.0d, 11.0d, 5.0d, 11.0d);
    private static final VoxelShape NORTH_SHAPE = Block.box(5.0d, 5.0d, 0.0d, 11.0d, 11.0d, 5.0d);
    private static final VoxelShape SOUTH_SHAPE = Block.box(5.0d, 5.0d, 11.0d, 11.0d, 11.0d, 16.0d);
    private static final VoxelShape EAST_SHAPE = Block.box(11.0d, 5.0d, 5.0d, 16.0d, 11.0d, 11.0d);
    private static final VoxelShape WEST_SHAPE = Block.box(0.0d, 5.0d, 5.0d, 5.0d, 11.0d, 11.0d);

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), (enumMap) -> {
        enumMap.put(Direction.NORTH, NORTH);
        enumMap.put(Direction.EAST, EAST);
        enumMap.put(Direction.SOUTH, SOUTH);
        enumMap.put(Direction.WEST, WEST);
        enumMap.put(Direction.UP, UP);
        enumMap.put(Direction.DOWN, DOWN);
    }));

    public WireBlock(Properties pProperties) {
        super(pProperties.noOcclusion().pushReaction(PushReaction.BLOCK).dynamicShape());
        this.registerDefaultState(this.defaultBlockState()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
        super.createBlockStateDefinition(pBuilder);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CENTER;
        shape = state.getValue(UP) ? Shapes.or(shape, UP_SHAPE) : shape;
        shape = state.getValue(DOWN) ? Shapes.or(shape, DOWN_SHAPE) : shape;
        shape = state.getValue(NORTH) ? Shapes.or(shape, NORTH_SHAPE) : shape;
        shape = state.getValue(SOUTH) ? Shapes.or(shape, SOUTH_SHAPE) : shape;
        shape = state.getValue(EAST) ? Shapes.or(shape, EAST_SHAPE) : shape;
        shape = state.getValue(WEST) ? Shapes.or(shape, WEST_SHAPE) : shape;
        return shape;
    }
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.getConnectionState(pContext.getLevel(), this.defaultBlockState(), pContext.getClickedPos());
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return this.getConnectionState(pLevel, pState, pPos);
    }

    protected BlockState getConnectionState(BlockGetter level, BlockState pState, BlockPos pos) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockState resultState = this.defaultBlockState();
        for (Direction direction : Direction.values()) {
            mutableBlockPos.set(pos).move(direction);
            if (canConnect(level, pState, mutableBlockPos)) {
                resultState = resultState.setValue(PROPERTY_BY_DIRECTION.get(direction), true);
            }
        }
        return resultState;
    }

    protected boolean canConnect(BlockGetter level, BlockState currentState, BlockPos blockPos) {
        return level.getBlockState(blockPos).getBlock().equals(this);
    }
}
