package com.nythicalnorm.nythicalSpaceProgram.block.entity;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.recipe.MagnetizerRecipe;
import com.nythicalnorm.nythicalSpaceProgram.screen.MagnetizerMenu;
import com.nythicalnorm.nythicalSpaceProgram.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MagnetizerEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler InputItemHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return getCurrentRecipe(stack).isPresent();
        }

        @Override
        protected void onContentsChanged(int slot) {
            MagnetizerEntity.this.updateBlock();
        }
    };

    private final ItemStackHandler OutputItemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            MagnetizerEntity.this.updateBlock();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }
    };

    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage(10000, 50, 0, 0);

    private LazyOptional<IItemHandler> lazyInputItemHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyOutputItemHandler = LazyOptional.empty();
    private LazyOptional<CustomEnergyStorage> energyStorageLazyOptional = LazyOptional.empty();

    private Direction Facing = Direction.NORTH;
    private boolean isCrafting = false;
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    //used clientside only for rendering the magnet table
    private float magnetTableYrot = 0f;

    public MagnetizerEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MAGNETIZER_BE.get(), pPos, pBlockState);
        Facing = pBlockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> MagnetizerEntity.this.progress;
                    case 1 -> MagnetizerEntity.this.maxProgress;
                    case 2 -> MagnetizerEntity.this.energyStorage.getEnergyStored();
                    case 3 -> MagnetizerEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                 switch (pIndex) {
                    case 0 -> MagnetizerEntity.this.progress = pValue;
                    case 1 -> MagnetizerEntity.this.maxProgress = pValue;
                    case 2 -> MagnetizerEntity.this.energyStorage.setEnergy(pValue);
                 }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public ItemStack getOutputStack() {
        return OutputItemHandler.getStackInSlot(0);
    }
    public ItemStack getInputStack() {
        return InputItemHandler.getStackInSlot(0);
    }

    public float getRenderFacing() {
        return -Facing.toYRot();
    }

    public boolean isCrafting() {
        return isCrafting;
    }

    private void setIsCrafting(boolean setValue) {
        if (isCrafting != setValue) {
            isCrafting = setValue;
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


    public float getMagnetTableYrot() {
        return magnetTableYrot;
    }

    public void setMagnetTableYrot(float yRot) {
        magnetTableYrot = yRot;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.DOWN || side == Facing.getOpposite()) {
                return lazyOutputItemHandler.cast();
            }
            else {
                return lazyInputItemHandler.cast();
            }
        }

        else if (cap == ForgeCapabilities.ENERGY) {
            return this.energyStorageLazyOptional.cast();
        }
        else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyInputItemHandler = LazyOptional.of(() ->  InputItemHandler);
        lazyOutputItemHandler = LazyOptional.of(() ->  OutputItemHandler);
        energyStorageLazyOptional = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyStorageLazyOptional.invalidate();
        lazyInputItemHandler.invalidate();
        lazyOutputItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(InputItemHandler.getSlots() + OutputItemHandler.getSlots());

        inventory.setItem(0, InputItemHandler.getStackInSlot(0));
        inventory.setItem(1, OutputItemHandler.getStackInSlot(0));

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.nythicalspaceprogram.magnetizer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int ContainerId, Inventory inventory, Player player) {
        return new MagnetizerMenu(ContainerId, inventory, this, this.data);
    }

    public <T> LazyOptional<T> getSlotForDisplay(int id) {
        if (id == 0) {
            return lazyInputItemHandler.cast();
        }
        else {
            return lazyOutputItemHandler.cast();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventoryIn", this.InputItemHandler.serializeNBT());
        pTag.put("inventoryOut", this.OutputItemHandler.serializeNBT());
        pTag.put("magnetizer.energyStorage", this.energyStorage.serializeNBT());
        pTag.putInt("magnetizer.progress", progress);
        pTag.putBoolean("magnetizer.crafting", isCrafting);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        if (!pTag.contains("inventoryIn") || !pTag.contains("inventoryOut") || !pTag.contains("magnetizer.energyStorage") || !pTag.contains("magnetizer.progress") || !pTag.contains("magnetizer.crafting")) {
            NythicalSpaceProgram.LOGGER.error("Expected NBT tag is missing.");
            return;
        }
        this.InputItemHandler.deserializeNBT(pTag.getCompound("inventoryIn"));
        this.OutputItemHandler.deserializeNBT(pTag.getCompound("inventoryOut"));
        this.energyStorage.deserializeNBT(pTag.get("magnetizer.energyStorage"));
        this.progress = pTag.getInt("magnetizer.progress");
        this.isCrafting = pTag.getBoolean("magnetizer.crafting");

        super.load(pTag);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        if (hasRecipe()) {
            setIsCrafting(true);
            if (energyStorage.ConsumeEnergy(20)) {
                progress++;
                setChanged(pLevel, pPos, pState);
                if (progress >= maxProgress) {
                    craftItem();
                    resetProgress();
                }
            }
            else {
                setIsCrafting(false);
            }
        } else {
            setIsCrafting(false);
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 0;
    }

    private void craftItem() {
        Optional<MagnetizerRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(null);
        this.InputItemHandler.extractItem(0, 1, false);

        this.OutputItemHandler.setStackInSlot(0, new ItemStack(result.getItem(),
                this.OutputItemHandler.getStackInSlot(0).getCount() + result.getCount()));
    }

    private boolean hasRecipe() {
        Optional<MagnetizerRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack result = recipe.get().getResultItem(null);
        maxProgress = recipe.get().getCraftTime();
        return camInsertAmountIntoOutputSlot(result.getCount()) && camInsertItemIntoOutputSlot((result.getItem()));
    }

    public Optional<MagnetizerRecipe> getCurrentRecipe(ItemStack item) {
        SimpleContainer inventory = new SimpleContainer(InputItemHandler.getSlots() + OutputItemHandler.getSlots());
        inventory.setItem(0, item);
        inventory.setItem(1, OutputItemHandler.getStackInSlot(0));
        return this.level.getRecipeManager().getRecipeFor(MagnetizerRecipe.Type.INSTANCE, inventory, level);
    }

    private Optional<MagnetizerRecipe> getCurrentRecipe() {
        return getCurrentRecipe(InputItemHandler.getStackInSlot(0));
    }

    private boolean camInsertItemIntoOutputSlot(Item item) {
        return this.OutputItemHandler.getStackInSlot(0).isEmpty() || this.OutputItemHandler.getStackInSlot(0).is(item);
    }

    private boolean camInsertAmountIntoOutputSlot(int count) {
        return this.OutputItemHandler.getStackInSlot(0).getCount() + count  <= this.OutputItemHandler.getStackInSlot(0).getMaxStackSize();
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
