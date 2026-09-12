package com.nythicalnorm.voxelspaceprogram.fluid.containers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;

public class GasContainerFluidHandler implements IFluidHandlerItem {

    private final ItemStack container;

    public GasContainerFluidHandler(ItemStack container) {
        this.container = container;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != 0) {
            return FluidStack.EMPTY;
        }

        CompoundTag tag = container.getTag();
        if (tag == null || !tag.contains("Fluid")) {
            return FluidStack.EMPTY;
        }

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(tag.getString("Fluid")));
        if (fluid == null) {
            return FluidStack.EMPTY;
        }
        int amount = tag.getInt("Amount");
        return new FluidStack(fluid, amount);
    }

    @Override
    public int getTankCapacity(int tank) {
        return GasContainerItem.CAPACITY;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }

        FluidStack current = getFluidInTank(0);
        // Don't allow different fluids to mix
        if (!current.isEmpty() && !current.getFluid().isSame(resource.getFluid())) {
            return 0;
        }

        int space = GasContainerItem.CAPACITY - current.getAmount();
        int amount = Math.min(space, resource.getAmount());
        if (amount <= 0) {
            return 0;
        }

        if (action.execute()) {
            CompoundTag tag = container.getOrCreateTag();
            tag.putString("Fluid", ForgeRegistries.FLUIDS.getKey(resource.getFluid()).toString());
            tag.putInt("Amount", current.getAmount() + amount);
        }

        return amount;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }
        FluidStack current = getFluidInTank(0);
        if (current.isEmpty() ||
                !current.getFluid().isSame(resource.getFluid())) {
            return FluidStack.EMPTY;
        }

        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int amount, FluidAction action) {
        FluidStack current = getFluidInTank(0);

        if (current.isEmpty() || amount <= 0) {
            return FluidStack.EMPTY;
        }

        int drainedAmount = Math.min(amount, current.getAmount());
        FluidStack result = new FluidStack(current.getFluid(), drainedAmount);

        if (action.execute()) {
            int remaining = current.getAmount() - drainedAmount;
            CompoundTag tag = container.getOrCreateTag();

            if (remaining <= 0) {
                tag.remove("Fluid");
                tag.remove("Amount");
            } else {
                tag.putInt("Amount", remaining);
            }
        }

        return result;
    }
}