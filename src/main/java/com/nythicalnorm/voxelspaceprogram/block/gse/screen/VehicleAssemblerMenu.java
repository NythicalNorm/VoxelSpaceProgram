package com.nythicalnorm.voxelspaceprogram.block.gse.screen;

import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.gse.entity.VehicleAssemblerEntity;
import com.nythicalnorm.voxelspaceprogram.gui.NSPMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class VehicleAssemblerMenu extends AbstractContainerMenu {
    private final VehicleAssemblerEntity vehicleAssemblerBE;
    private final Player player;
    private Component[] problems;

    public static enum ButtonType {
        CREATE_ASSEMBLY_AREA
    }

    public VehicleAssemblerMenu(int pContainerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(pContainerId, inventory.player.level().getBlockEntity(extraData.readBlockPos()), inventory.player);
    }

    public VehicleAssemblerMenu(int pContainerId, BlockEntity blockEntity, Player player) {
        super(NSPMenuTypes.VEHICLE_ASSEMBLER_MENU.get(), pContainerId);
        this.player = player;

        if (blockEntity instanceof VehicleAssemblerEntity vehicleAssembler) {
            vehicleAssemblerBE = vehicleAssembler;
        } else {
            vehicleAssemblerBE = null;
        }
    }

    public VehicleAssemblerEntity getVehicleAssemblerBE() {
        return vehicleAssemblerBE;
    }

    @Override
    public void removed(Player pPlayer) {
        if (vehicleAssemblerBE != null) {
            vehicleAssemblerBE.removeMenuOpenedPlayer(pPlayer);
        }
        super.removed(pPlayer);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (vehicleAssemblerBE.getLevel() != null) {
            return stillValid(ContainerLevelAccess.create(vehicleAssemblerBE.getLevel(), vehicleAssemblerBE.getBlockPos()),
                    pPlayer, NSPBlocks.VEHICLE_ASSEMBLER.get());
        }
        return false;
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        if (vehicleAssemblerBE != null && !pPlayer.level().isClientSide()) {
            vehicleAssemblerBE.buttonPress(ButtonType.CREATE_ASSEMBLY_AREA);
            return true;
        }
        return false;
    }

    public void setProblems(Component[] problemComponents) {
        this.problems = problemComponents;
    }

    public Component[] getProblems() {
        return problems;
    }
}
