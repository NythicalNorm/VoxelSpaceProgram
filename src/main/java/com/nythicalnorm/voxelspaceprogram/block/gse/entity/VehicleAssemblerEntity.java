package com.nythicalnorm.voxelspaceprogram.block.gse.entity;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.BlockFindingStorage;
import com.nythicalnorm.voxelspaceprogram.block.gse.AssemblerState;
import com.nythicalnorm.voxelspaceprogram.block.gse.AssemblerUtil;
import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.gse.screen.VehicleAssemblerMenu;
import com.nythicalnorm.voxelspaceprogram.block.gse.warnings.ProblemsMgr;
import com.nythicalnorm.voxelspaceprogram.block.gse.warnings.ProblemsStorage;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.NSPBlockEntities;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class VehicleAssemblerEntity extends BlockEntity implements MenuProvider {
    public static final int MaxPlatformSize = 64;
    public static final int MaxPlatformHeight = 128;

    private AssemblerState state = AssemblerState.JUST_PLACED;
    private ProblemsMgr problemsMgr;
    private Player menuOpenedPlayer;

    BoundingBox assemblyBoundingBox = null;

    public VehicleAssemblerEntity( BlockPos pPos, BlockState pBlockState) {
        super(NSPBlockEntities.VEHICLE_ASSEMBLER_BE.get(), pPos, pBlockState);
        problemsMgr = new ProblemsMgr();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.voxelspaceprogram.vehicle_assembler");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        this.menuOpenedPlayer = pPlayer;
        problemsMgr.setUpdated(true);
        return new VehicleAssemblerMenu(pContainerId, this, pPlayer);
    }

    public void removeMenuOpenedPlayer(Player pPlayer) {
        this.menuOpenedPlayer = null;
    }

    public AssemblerState getState() {
        return state;
    }

    public void setState(AssemblerState state, boolean clearProblems) {
        this.state = state;
        if (clearProblems) {
            this.problemsMgr.clearProblems();
        }
        this.updateBlock();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null) {
            if (!this.level.isClientSide()) {
                BlockFindingStorage.makeBlockEntityFindable(getBlockPos(), level);
            }
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
            if (this.state == AssemblerState.ASSEMBLY_AREA_SEARCHING) {
                if (assemblyBoundingBox == null) {
                    calculateBoundingBox();
                    if (assemblyBoundingBox == null) {
                        setState(AssemblerState.JUST_PLACED, false);
                        return;
                    }

                    long time = Util.getNanos();
                    if (chackIfBoundingBoxIsAir()) {
                        problemsMgr.setProblem(ProblemsStorage.PlatformNotAllAir, false);
                        setState(AssemblerState.ASSEMBLY_AREA_READY, true);
                    } else {
                        problemsMgr.setProblem(ProblemsStorage.PlatformNotAllAir, true);
                        setState(AssemblerState.JUST_PLACED, false);
                    }

                    long diff = Util.getNanos() - time;
                    VoxelSpaceProgram.log("is this faster, Nano: " + diff);
                }
            }
        }
        if (menuOpenedPlayer != null) {
            problemsMgr.sendPacketIfUpdated(menuOpenedPlayer);
        }
    }

    public void calculateBoundingBox() {
        if (getLevel().isClientSide()) {
            return;
        }
        BlockPos startingPos = AssemblerUtil.getBlockAroundMeHorizontal(getBlockPos(), NSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get(), level);
        if (startingPos != null) {
            long beforeTimes = Util.getNanos();

            BlockState blockState = level.getBlockState(startingPos);
            this.assemblyBoundingBox = AssemblerUtil.calculateBoundingBox(blockState.getBlock(), NSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get(), startingPos, MaxPlatformSize, getLevel(), this.problemsMgr);

            long diff = Util.getNanos() - beforeTimes;
            VoxelSpaceProgram.log("flood fill time: " + diff);
        }
    }

    private boolean chackIfBoundingBoxIsAir() {
        Stream<BlockState> blockStateStream = level.getBlockStatesIfLoaded(new AABB(assemblyBoundingBox.minX(), assemblyBoundingBox.minY(), assemblyBoundingBox.minZ()
                ,assemblyBoundingBox.maxX(), assemblyBoundingBox.maxY(), assemblyBoundingBox.maxZ()));
        return blockStateStream.allMatch(Predicate.isEqual(Blocks.AIR.defaultBlockState()));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putString("vsp.assembler_state", this.state.getSerializedName());

        if (assemblyBoundingBox != null) {
            pTag.putInt("vsp.bounding_box_min_x", this.assemblyBoundingBox.minX());
            pTag.putInt("vsp.bounding_box_min_y", this.assemblyBoundingBox.minY());
            pTag.putInt("vsp.bounding_box_min_z", this.assemblyBoundingBox.minZ());
            pTag.putInt("vsp.bounding_box_max_x", this.assemblyBoundingBox.maxX());
            pTag.putInt("vsp.bounding_box_max_y", this.assemblyBoundingBox.maxY());
            pTag.putInt("vsp.bounding_box_max_z", this.assemblyBoundingBox.maxZ());
        }

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        if (pTag.contains("vsp.bounding_box_min_x")) {
            this.assemblyBoundingBox = new BoundingBox(
                    pTag.getInt("vsp.bounding_box_min_x"),
                    pTag.getInt("vsp.bounding_box_min_y"),
                    pTag.getInt("vsp.bounding_box_min_z"),
                    pTag.getInt("vsp.bounding_box_max_x"),
                    pTag.getInt("vsp.bounding_box_max_y"),
                    pTag.getInt("vsp.bounding_box_max_z")
            );
        }

        if (pTag.contains("vsp.assembler_state")) {
           this.state = AssemblerState.getByValue(pTag.getString("vsp.assembler_state"));
        }

        super.load(pTag);
    }

    public void startAssemblyAreaSearch() {
        assemblyBoundingBox = null;
        setState(AssemblerState.ASSEMBLY_AREA_SEARCHING, false);
    }

    public void buttonPress(VehicleAssemblerMenu.ButtonType buttonType) {
        if (buttonType.equals(VehicleAssemblerMenu.ButtonType.CREATE_ASSEMBLY_AREA)) {
            if (this.state == AssemblerState.JUST_PLACED) {
                startAssemblyAreaSearch();
            }
        }
    }

    public void platformChanged() {

    }

    public void updateBlock(){
        if (level == null){
            return;
        }
        else if (!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
