package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.multiblock;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.VSPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoundingBlockEntity extends BlockEntity {
    private BlockPos mainPos = BlockPos.ZERO;
    private boolean receivedCoords;

    public BoundingBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(VSPBlockEntities.BOUNDING_BLOCK_BE.get(), pPos, pBlockState);
    }

    public void setMainLocation(BlockPos pos) {
        receivedCoords = pos != null;
        if (!level.isClientSide()) {
            mainPos = pos;
            updateBlock();
        }
    }

    public boolean hasReceivedCoords() {
        return receivedCoords;
    }

    public BlockPos getMainPos() {
        if (mainPos == null) {
            mainPos = BlockPos.ZERO;
        }
        return mainPos;
    }

    @Nullable
    public BlockEntity getMainTile() {
        return receivedCoords ? level.getBlockEntity(getMainPos()) : null;
    }

    @Nullable
    private MultiblockRocketryEntity getMain() {
        // Return the main tile; note that it's possible, esp. when chunks are
        // loading that the main tile has not yet loaded and thus is null.
        BlockEntity tile = getMainTile();
        if (!(tile instanceof MultiblockRocketryEntity)) {
            // On the off chance that another block got placed there (which seems only likely with corruption, go ahead and log what we found.)
            VoxelSpaceProgram.logError("Found tile " + tile + " instead of an IBoundingBlock, at " +  getMainPos() + " Multiblock cannot function");
            return null;
        }
        return (MultiblockRocketryEntity) tile;
    }


    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        mainPos = NbtUtils.readBlockPos(nbt.getCompound("vsp.main_pos"));
        receivedCoords = nbt.getBoolean("vsp.receivedCoords");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        if (receivedCoords) {
            nbtTags.put("vsp.main_pos", NbtUtils.writeBlockPos(getMainPos()));
        }
        nbtTags.putBoolean("vsp.receivedCoords", receivedCoords);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    //handler during chunk load.
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }

    public void updateBlock(){
        if (level != null) {
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0b111);
            }
        }
    }
}
