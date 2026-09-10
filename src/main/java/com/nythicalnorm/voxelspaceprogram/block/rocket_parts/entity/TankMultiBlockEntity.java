package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.TankMultiBlock;
import com.nythicalnorm.voxelspaceprogram.util.BlockUtils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

public abstract class TankMultiBlockEntity extends BlockEntity {
    private boolean tankAssembled = false;
    private boolean isHost = false;
    private BlockPos hostPos = null;
    private LongArrayList tankPositions = null;

    public TankMultiBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public boolean isTankAssembled() {
        return tankAssembled;
    }

    public boolean isHost() {
        return isHost;
    }

    public @Nullable TankMultiBlockEntity getHostBE(Level level) {
        if (this.isHost()) {
            return this;
        } else if (this.hostPos != null){
            return (TankMultiBlockEntity)level.getBlockEntity(this.hostPos);
        }
        return null;
    }

    public BlockPos getHostPos() {
        return hostPos;
    }

    protected abstract Vector3i getMaxTankSize();

    public void blockAssembled(BlockPos hostPos) {
        this.isHost = false;
        this.hostPos = hostPos;
        this.tankAssembled = true;
        this.tankPositions = null;
    }

    protected void initializeHost(LongArrayList connectedBlocks) {
        this.isHost = true;
        this.hostPos = this.getBlockPos();
        this.tankAssembled = true;
        this.tankPositions = connectedBlocks;
    }

    protected void disassembleTank() {
        this.isHost = false;
        this.hostPos = null;
        this.tankAssembled = false;
        this.tankPositions = null;
    }

    public boolean tryTankAssembly(Level level, BlockPos pos) {
        LongOpenHashSet includedBlocks = new LongOpenHashSet();
        LongArrayList connectedBlocks = BlockUtils.floodFillWithHashSet(
                level, pos, 20000,
                state -> state.getBlock().equals(this.getBlockState().getBlock()),
                includedBlocks
        );
        Vector3i tankSize = new Vector3i();
        if (BlockUtils.isEnclosedCuboid(connectedBlocks, includedBlocks, tankSize)) {
            if (tankSize.x() > this.getMaxTankSize().x() ||
                tankSize.y() > this.getMaxTankSize().y() ||
                tankSize.z() > this.getMaxTankSize().z()) {
                return false;
            }
            this.initializeHost(connectedBlocks);
            this.setAssembledBlockStates(level, connectedBlocks);
            return true;
        }

        return false;
    }

    private void setAssembledBlockStates(Level level, LongArrayList connectedBlocks) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (Long longPos : connectedBlocks) {
            pos.set(longPos);
            if (level.getBlockEntity(pos) instanceof TankMultiBlockEntity tankMultiBlock) {
                if (!this.equals(tankMultiBlock)) {
                    tankMultiBlock.blockAssembled(this.getBlockPos());
                }
            }
            BlockState newState = level.getBlockState(pos).setValue(TankMultiBlock.ASSEMBLED, true);
            level.setBlock(pos, newState, 3);
        }
    }

//    private BlockState getTankBlockState(LongOpenHashSet includedBlocks,
//                                         BlockPos.MutableBlockPos neighborPos, BlockPos.MutableBlockPos blockPos
//    ) {
//        BlockState blockState = level.getBlockState(blockPos).setValue(TankMultiBlock.ASSEMBLED, true);
//        for (Direction direction : Direction.values()) {
//            neighborPos.setWithOffset(blockPos, direction);
//            if (includedBlocks.contains(neighborPos.asLong())) {
//                blockState = blockState.setValue(TankMultiBlock.PROPERTY_BY_DIRECTION.get(direction), true);
//            } else {
//                blockState = blockState.setValue(TankMultiBlock.PROPERTY_BY_DIRECTION.get(direction), false);
//            }
//        }
//        return blockState;
//    }

    public void destroyTank(Level level, BlockPos destructOrigin) {
        BlockPos.MutableBlockPos iterPos = new BlockPos.MutableBlockPos();
        BlockState defaultBlockState = this.getBlockState().getBlock().defaultBlockState();

        for (Long pos : this.tankPositions) {
            iterPos.set(pos);

            if (level.getBlockEntity(iterPos) instanceof TankMultiBlockEntity blockEntity) {
                blockEntity.disassembleTank();
                if (!iterPos.equals(destructOrigin)) {
                    level.setBlock(iterPos.immutable(), defaultBlockState, 3);
                }
            } else {
                VoxelSpaceProgram.logError("tank blocks were removed by unknown means.");
            }
        }
    }

    // loading and saving Server Side
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putBoolean("vsp_tank_assembled", this.tankAssembled);
        tag.putBoolean("vsp_is_host", this.isHost);

        if (this.hostPos != null) {
            tag.putInt("vsp_host_pos_x", this.hostPos.getX());
            tag.putInt("vsp_host_pos_y", this.hostPos.getY());
            tag.putInt("vsp_host_pos_z", this.hostPos.getZ());
        }
        if (tankPositions != null) {
            tag.putLongArray("vsp_tank_positions", this.tankPositions.elements());
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.tankAssembled = tag.getBoolean("vsp_tank_assembled");
        this.isHost = tag.getBoolean("vsp_is_host");

        if (tag.contains("vsp_host_pos_x")) {
            this.hostPos = new BlockPos(
                    tag.getInt("vsp_host_pos_x"),
                    tag.getInt("vsp_host_pos_y"),
                    tag.getInt("vsp_host_pos_z")
            );
        } else {
            this.hostPos = null;
        }

        if (tag.contains("vsp_tank_positions")) {
            this.tankPositions = LongArrayList.wrap(tag.getLongArray("vsp_tank_positions"));
        } else {
            this.tankPositions = null;
        }
    }

    // client side sync
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putBoolean("vsp_tank_assembled", this.tankAssembled);
        tag.putBoolean("vsp_is_host", this.isHost);

        if (this.hostPos != null) {
            tag.putInt("vsp_host_pos_x", this.hostPos.getX());
            tag.putInt("vsp_host_pos_y", this.hostPos.getY());
            tag.putInt("vsp_host_pos_z", this.hostPos.getZ());
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.tankAssembled = tag.getBoolean("vsp_tank_assembled");
        this.isHost = tag.getBoolean("vsp_is_host");

        if (tag.contains("vsp_host_pos_x")) {
            this.hostPos = new BlockPos(
                    tag.getInt("vsp_host_pos_x"),
                    tag.getInt("vsp_host_pos_y"),
                    tag.getInt("vsp_host_pos_z")
            );
        } else {
            this.hostPos = null;
        }
    }
}
