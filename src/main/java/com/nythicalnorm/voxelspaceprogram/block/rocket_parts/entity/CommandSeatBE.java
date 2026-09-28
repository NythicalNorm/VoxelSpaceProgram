package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity;

import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.VSPBlockEntities;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.VSPMultiblockEntity;
import com.nythicalnorm.voxelspaceprogram.entities.CommandSeatEntity;
import com.nythicalnorm.voxelspaceprogram.entities.VSPEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandSeatBE extends VSPMultiblockEntity {
    private UUID seatEntityUUID;

    public CommandSeatBE(BlockPos pPos, BlockState pBlockState) {
        super(VSPBlockEntities.COMMAND_SEAT_BE.get(), pPos, pBlockState);
    }

    public InteractionResult trySitDown(ServerPlayer player, ServerLevel level, BlockPos pos) {
        if (this.seatEntityUUID != null && level.getEntity(this.seatEntityUUID) == null) {
            this.seatEntityUUID = null;
            return InteractionResult.FAIL;
        }

        CommandSeatEntity seat = VSPEntities.COMMAND_SEAT_ENTITY.get().create(level);
        seat.setSeatBlockPos(this.getBlockPos());
        seat.setPos(pos.getX() + 0.5d, pos.getY() + 0.4d, pos.getZ() + 0.5d);
        level.addFreshEntity(seat);
        player.startRiding(seat, true);

        this.setSeatID(seat.getUUID());
        return InteractionResult.SUCCESS;
    }

    public void setSeatID(UUID uuid) {
        this.seatEntityUUID = uuid;
        this.updateBEState();
    }

    public void seatRemoved() {
        this.setSeatID(null);
    }

    public void blockRemoved() {
        if (this.seatEntityUUID != null) {
            CommandSeatEntity entity = (CommandSeatEntity) ((ServerLevel)this.level).getEntity(this.seatEntityUUID);
            if (entity != null) {
                this.seatRemoved();
                entity.removeSeat();
            }
        }
    }

    protected void updateBEState() {
        this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
    }

    // loading and saving Server Side
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        if (this.seatEntityUUID != null) {
            tag.putUUID("vsp_seat_entity_uuid", this.seatEntityUUID);
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        if (tag.contains("vsp_seat_entity_uuid")) {
            this.seatEntityUUID = tag.getUUID("vsp_seat_entity_uuid");
        }
    }
}
