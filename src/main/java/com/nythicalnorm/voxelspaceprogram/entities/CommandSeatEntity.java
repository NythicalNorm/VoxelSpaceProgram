package com.nythicalnorm.voxelspaceprogram.entities;

import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.CommandSeatBE;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

public class CommandSeatEntity extends Entity {
    private static final EntityDataAccessor<BlockPos> SEAT_BLOCK_POS = define(EntityDataSerializers.BLOCK_POS);

    private static <T> EntityDataAccessor<T> define(EntityDataSerializer<T> dataSerializer) {
        return SynchedEntityData.defineId(CommandSeatEntity.class, dataSerializer);
    }

    public CommandSeatEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    public BlockPos getSeatBlockPos() {
        return this.entityData.get(SEAT_BLOCK_POS);
    }

    public void setSeatBlockPos(BlockPos seatBlockPos) {
        this.entityData.set(SEAT_BLOCK_POS, seatBlockPos);
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return false;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!this.level().isClientSide()) { //  && this.position().distanceToSqr(this.getSeatBlockPos().getCenter()) > 1.0d
            this.discardIfPassengerless();
        }
    }

    @Override
    public void onAddedToWorld() {
        BlockPos pos = getSeatBlockPos();
        if (pos != null && this.level().getBlockEntity(pos) instanceof CommandSeatBE commandSeatBE) {
            commandSeatBE.setSeatID(this.getUUID());
        }
    }

    @Override
    public void setDeltaMovement(Vec3 pDeltaMovement) { }

    @Override
    protected boolean canRide(Entity entity) {
        return entity instanceof Player && !(entity instanceof FakePlayer);
    }

    @Override
    public void dismountTo(double pX, double pY, double pZ) {
        super.dismountTo(pX, pY + 0.25d, pZ);
    }

    protected void discardIfPassengerless() {
        if (this.getPassengers().isEmpty()) {
            if (this.level().getBlockEntity(this.getSeatBlockPos()) instanceof CommandSeatBE commandSeatBE) {
                commandSeatBE.seatRemoved();
            }
            this.unRide();
            this.discard();
        }
    }

    public void removeSeat() {
        this.unRide();
        this.discard();
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SEAT_BLOCK_POS, new BlockPos(0, 0,0));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
}
