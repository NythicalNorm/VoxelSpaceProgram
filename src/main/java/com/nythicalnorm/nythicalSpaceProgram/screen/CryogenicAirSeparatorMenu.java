package com.nythicalnorm.nythicalSpaceProgram.screen;

import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import com.nythicalnorm.nythicalSpaceProgram.block.entity.CryogenicAirSeparatorEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

public class CryogenicAirSeparatorMenu extends AbstractContainerMenu {
    public final CryogenicAirSeparatorEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public CryogenicAirSeparatorMenu(int pContainerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(pContainerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(8));
    }

    public CryogenicAirSeparatorMenu(int pContainerId, Inventory inventory, BlockEntity entity, ContainerData simpleContainerData) {
        super(ModMenuTypes.CRYOGENIC_AIR_SEPARATOR_MENU.get(), pContainerId);
        blockEntity = ((CryogenicAirSeparatorEntity) entity);
        this.level = blockEntity.getLevel();
        this.data = simpleContainerData;

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.CRYOGENIC_AIR_SEPARATOR.get());
    }

    public Fluid getFluidManufacture(int index) {
        return blockEntity.getFluidManufacture(index);
    }

    public int getFluidAmount(int index) {
        return this.data.get(2 + index);
    }
    public int getFluidCapacity(int index) {
        return this.data.get(5 + index);
    }

    public int getFluidProgress(int index) {
        int amount = getFluidAmount(index);
        int maxAmount = getFluidCapacity(index);
        int fluidBarSize = 56;
        return amount != 0 ? amount * fluidBarSize / maxAmount : 0;
    }

    public int getCurrentEnergy() {
        return this.data.get(0);
    }
    public int getMaxEnergy() {
        return this.data.get(1);
    }

    public int getEnergyProgress() {
        int amount = this.data.get(0);
        int maxAmount = this.data.get(1);
        int EnergyBarSize = 56;
        return amount != 0 ? amount * EnergyBarSize / maxAmount : 0;
    }

    public void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 88 + i * 18));
            }
        }
    }

    public void addPlayerHotbar(Inventory playerInventory) {
        for (int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 146));
        }
    }
}
