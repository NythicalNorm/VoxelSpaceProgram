package com.nythicalnorm.nythicalSpaceProgram.block.entity;

import com.nythicalnorm.nythicalSpaceProgram.recipe.AirSeparatedFromPlanets;
import com.nythicalnorm.nythicalSpaceProgram.screen.CryogenicAirSeparatorMenu;
import com.nythicalnorm.nythicalSpaceProgram.util.CryogenicAirSeparatorFluidTank;
import com.nythicalnorm.nythicalSpaceProgram.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CryogenicAirSeparatorEntity extends BlockEntity implements MenuProvider {

    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage(50000, 250, 0, 0);
    private final CryogenicAirSeparatorFluidTank[] fluidTank =  new CryogenicAirSeparatorFluidTank[]{
            new CryogenicAirSeparatorFluidTank(10000),
            new CryogenicAirSeparatorFluidTank(10000),
            new CryogenicAirSeparatorFluidTank(10000)
    };

    private LazyOptional<CustomEnergyStorage> energyStorageLazyOptional = LazyOptional.empty();
    private LazyOptional<CryogenicAirSeparatorFluidTank> LazyOptionalfluidTank0 = LazyOptional.empty();
    private LazyOptional<CryogenicAirSeparatorFluidTank> LazyOptionalfluidTank1 = LazyOptional.empty();
    private LazyOptional<CryogenicAirSeparatorFluidTank> LazyOptionalfluidTank2 = LazyOptional.empty();

    protected final ContainerData data;
    private boolean isCrafting = false;

    private AirSeparatedFromPlanets.ManufacturingFluid[] manufacturingFluids = new AirSeparatedFromPlanets.ManufacturingFluid[3];

    public CryogenicAirSeparatorEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CRYOGENIC_AIR_SEPARATOR_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CryogenicAirSeparatorEntity.this.energyStorage.getEnergyStored();
                    case 1 -> CryogenicAirSeparatorEntity.this.energyStorage.getMaxEnergyStored();
                    case 2 -> CryogenicAirSeparatorEntity.this.fluidTank[0].getFluidAmount();
                    case 3 -> CryogenicAirSeparatorEntity.this.fluidTank[1].getFluidAmount();
                    case 4 -> CryogenicAirSeparatorEntity.this.fluidTank[2].getFluidAmount();
                    case 5 -> CryogenicAirSeparatorEntity.this.fluidTank[0].getCapacity();
                    case 6 -> CryogenicAirSeparatorEntity.this.fluidTank[1].getCapacity();
                    case 7 -> CryogenicAirSeparatorEntity.this.fluidTank[2].getCapacity();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                if (pIndex == 0) {
                    CryogenicAirSeparatorEntity.this.energyStorage.setEnergy(pValue);
                }
            }

            @Override
            public int getCount() {
                return 8;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.nythicalspaceprogram.cryogenic_air_separator");
    }

    private void setIsCrafting(boolean setValue) {
        if (isCrafting != setValue) {
            isCrafting = setValue;
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyStorageLazyOptional.cast();
        }
        else if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.LazyOptionalfluidTank0.cast();
        }
        else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        UpdateFluidTanks();
        energyStorageLazyOptional = LazyOptional.of(() -> energyStorage);
        LazyOptionalfluidTank0 = LazyOptional.of(() -> fluidTank[0]);
        LazyOptionalfluidTank1 = LazyOptional.of(() -> fluidTank[1]);
        LazyOptionalfluidTank2 = LazyOptional.of(() -> fluidTank[2]);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyStorageLazyOptional.invalidate();
        LazyOptionalfluidTank0.invalidate();
        LazyOptionalfluidTank1.invalidate();
        LazyOptionalfluidTank2.invalidate();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CryogenicAirSeparatorMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("cryogenic_air_separator.energyStorage", this.energyStorage.serializeNBT());
        pTag.put("cryogenic_air_separator.fluidTank0", this.fluidTank[0].writeToNBT(new CompoundTag()));
        pTag.put("cryogenic_air_separator.fluidTank1", this.fluidTank[1].writeToNBT(new CompoundTag()));
        pTag.put("cryogenic_air_separator.fluidTank2", this.fluidTank[2].writeToNBT(new CompoundTag()));
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        this.energyStorage.deserializeNBT(pTag.get("cryogenic_air_separator.energyStorage"));
        this.fluidTank[0].readFromNBT(pTag.getCompound("cryogenic_air_separator.fluidTank0"));
        this.fluidTank[1].readFromNBT(pTag.getCompound("cryogenic_air_separator.fluidTank1"));
        this.fluidTank[2].readFromNBT(pTag.getCompound("cryogenic_air_separator.fluidTank2"));
        super.load(pTag);
    }

    public void UpdateFluidTanks() {
        if (level != null) {
            manufacturingFluids = AirSeparatedFromPlanets.getRecipe(level);
            if (manufacturingFluids == null) {
                return;
            }
            for (int i = 0; i < manufacturingFluids.length; i++) {
                Fluid fl = manufacturingFluids[i].resultingFluid;
                fluidTank[i].setValidator(fluidStack -> fluidStack.getFluid() == fl);
                if (fluidTank[i].getFluid() == FluidStack.EMPTY || fluidTank[i].getFluid().getFluid() == manufacturingFluids[i].resultingFluid) {
                    fluidTank[i].setCapacity(manufacturingFluids[i].tankCapacity);
                }
            }
            updateBlock();
        }
    }

    public void updateBlock(){
        if (level == null){
            return;
        }
        else if (!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (this.level == null || this.level.isClientSide() || manufacturingFluids == null) {
            return;
        }
        if (energyStorage.ConsumeEnergy(150)) {
            for (int i = 0; i < fluidTank.length; i++) {
                if (fluidTank[i].getFluidAmount() >= fluidTank[i].getCapacity() || manufacturingFluids[i].resultingFluid == Fluids.EMPTY) {
                    continue;
                }
                int fluidToPut = fluidTank[i].getFluidAmount() + manufacturingFluids[i].creationRate;
                if (fluidToPut > fluidTank[i].getCapacity()) {
                    fluidToPut = fluidTank[i].getCapacity();
                }
                FluidStack fl = new FluidStack(manufacturingFluids[i].resultingFluid, fluidToPut);
                fluidTank[i].setFluid(fl);
            }
        }
    }

    public Fluid getFluidManufacture(int index){
        if (manufacturingFluids != null) {
            return manufacturingFluids[index].resultingFluid;
        }
        else {
            return Fluids.EMPTY;
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
