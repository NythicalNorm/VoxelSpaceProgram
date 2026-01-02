package com.nythicalnorm.nythicalSpaceProgram.block.gse.entity;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.BlockFindingStorage;
import com.nythicalnorm.nythicalSpaceProgram.block.gse.AssemblerUtil;
import com.nythicalnorm.nythicalSpaceProgram.block.NSPBlocks;
import com.nythicalnorm.nythicalSpaceProgram.block.gse.screen.VehicleAssemblerMenu;
import com.nythicalnorm.nythicalSpaceProgram.block.manufacturing.entity.NSPBlockEntities;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

    int lastYIndexBlockPositions;
    BlockState[][][] alreadySeearchedblockPos;
    private boolean ongoingSearch = false;
    private boolean blockPosFinished = false;

    public VehicleAssemblerEntity( BlockPos pPos, BlockState pBlockState) {
        super(NSPBlockEntities.VEHICLE_ASSEMBLER_BE.get(), pPos, pBlockState);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.nythicalspaceprogram.vehicle_assembler");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if(recalculateBox) {
            recalculateBoundingBox();
            recalculateBox = false;
        }
        if (assemblyBoundingBox != null && !blockPosFinished) {
            startBoundingBoxSearch();
            //long diff = Util.getNanos() - beforeTimes;
            //NythicalSpaceProgram.log("All Block Search Time: " + diff);
        }
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
        if (assemblyBoundingBox == null) {
            recalculateBoundingBox();
        }
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
        if (pLevel.getGameTime() % 2L == 0L) {
            if (ongoingSearch) {
                if (lazyGetAllBlocks(lastYIndexBlockPositions)) {
                    ongoingSearch = false;
                    blockPosFinished = true;
                    double mass = AssemblerUtil.getTotalMass(alreadySeearchedblockPos);

                    level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Total Mass is").append(" " + mass), true);
                    // do stuff here
                }
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

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("nsp.bounding_box_min_x", this.assemblyBoundingBox.minX());
        pTag.putInt("nsp.bounding_box_min_y", this.assemblyBoundingBox.minY());
        pTag.putInt("nsp.bounding_box_min_z", this.assemblyBoundingBox.minZ());
        pTag.putInt("nsp.bounding_box_max_x", this.assemblyBoundingBox.maxX());
        pTag.putInt("nsp.bounding_box_max_y", this.assemblyBoundingBox.maxY());
        pTag.putInt("nsp.bounding_box_max_z", this.assemblyBoundingBox.maxZ());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        if (pTag.contains("nsp.bounding_box_min_x")) {
            this.assemblyBoundingBox = new BoundingBox(
                    pTag.getInt("nsp.bounding_box_min_x"),
                    pTag.getInt("nsp.bounding_box_min_y"),
                    pTag.getInt("nsp.bounding_box_min_z"),
                    pTag.getInt("nsp.bounding_box_max_x"),
                    pTag.getInt("nsp.bounding_box_max_y"),
                    pTag.getInt("nsp.bounding_box_max_z")
            );
        }
        super.load(pTag);
    }

    private void startBoundingBoxSearch() {
        this.alreadySeearchedblockPos = new BlockState[assemblyBoundingBox.getXSpan()][assemblyBoundingBox.getYSpan()][assemblyBoundingBox.getZSpan()];
        ongoingSearch = true;
        lazyGetAllBlocks(assemblyBoundingBox.minY());
    }

    public boolean lazyGetAllBlocks(int startYIndex) {
        long startTime = Util.getNanos();

        for (int y = startYIndex; y <= assemblyBoundingBox.maxY(); y++) {
            for (int x = assemblyBoundingBox.minX(); x <= assemblyBoundingBox.maxX(); x++) {
                for (int z = assemblyBoundingBox.minZ(); z <= assemblyBoundingBox.maxZ(); z++) {
                    int xIndex = x - assemblyBoundingBox.minX();
                    int yIndex = y - assemblyBoundingBox.minY();
                    int zIndex = z - assemblyBoundingBox.minZ();
                    if (alreadySeearchedblockPos[xIndex][yIndex][zIndex] == null) {
                        alreadySeearchedblockPos[xIndex][yIndex][zIndex] =
                        getLevel().getBlockState(new BlockPos(x, y, z));
                        if (Util.getNanos() - startTime > 80000) {
                            this.lastYIndexBlockPositions = y;
                            NythicalSpaceProgram.log("Next");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

//           org.valkyrienskies.mod.common.config.MassDatapackResolver resolver = MassDatapackResolver.INSTANCE;
//           if (resolver != null) {
//            //level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("mass of __ is").append(" " + resolver.getBlockStateMass(blockState)), true);
//           }
}
