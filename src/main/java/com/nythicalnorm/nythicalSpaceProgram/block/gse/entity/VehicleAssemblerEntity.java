package com.nythicalnorm.nythicalSpaceProgram.block.gse.entity;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.BlockFindingStorage;
import com.nythicalnorm.nythicalSpaceProgram.block.gse.AssemblerUtil;
import com.nythicalnorm.nythicalSpaceProgram.block.NSPBlocks;
import com.nythicalnorm.nythicalSpaceProgram.block.gse.screen.VehicleAssemblerMenu;
import com.nythicalnorm.nythicalSpaceProgram.block.manufacturing.entity.NSPBlockEntities;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VehicleAssemblerEntity extends BlockEntity implements MenuProvider {
    public static final int MaxPlatformSize = 64;
    public static final int MaxPlatformHeight = 128;

    private boolean recalculateBox = false;
    BoundingBox assemblyBoundingBox = null;


    public VehicleAssemblerEntity( BlockPos pPos, BlockState pBlockState) {
        super(NSPBlockEntities.VEHICLE_ASSEMBLER_BE.get(), pPos, pBlockState);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.nythicalspaceprogram.vehicle_assembler");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new VehicleAssemblerMenu(pContainerId,this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null) {
            if (!this.level.isClientSide()) {
                BlockFindingStorage.makeBlockEntityFindable(getBlockPos(), level);
            }
        }
        recalculateBoundingBox();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (this.level != null) {
            if (!this.level.isClientSide()) {
                BlockFindingStorage.destroyBlockEntityFindable(getBlockPos(), level);
            }
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState state) {
        if (pLevel.getGameTime() % 20L == 0L) {
            if(recalculateBox) {
                recalculateBoundingBox();
                recalculateBox = false;
            }
        }
    }

    public void setRecalculateBox() {
        recalculateBox = true;
    }

    public void recalculateBoundingBox() {
        if (getLevel().isClientSide()) {
            return;
        }
        BlockPos startingPos = AssemblerUtil.getBlockAroundMeHorizontal(getBlockPos(), NSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get(), level);
        if (startingPos != null) {
            long beforeTimes = Util.getNanos();

            BlockState blockState = level.getBlockState(startingPos);
           this.assemblyBoundingBox = AssemblerUtil.calculateBoundingBox(blockState.getBlock(), NSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get(), startingPos, MaxPlatformSize, getLevel());

           long diff = Util.getNanos() - beforeTimes;
           NythicalSpaceProgram.log("flood fill time: " + diff);
        }
    }


//           org.valkyrienskies.mod.common.config.MassDatapackResolver resolver = MassDatapackResolver.INSTANCE;
//           if (resolver != null) {
//            //level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("mass of __ is").append(" " + resolver.getBlockStateMass(blockState)), true);
//           }
}
