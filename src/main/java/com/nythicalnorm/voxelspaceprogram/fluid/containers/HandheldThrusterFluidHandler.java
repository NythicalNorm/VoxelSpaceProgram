package com.nythicalnorm.voxelspaceprogram.fluid.containers;

import com.nythicalnorm.voxelspaceprogram.Item.custom.HandheldThrusterItem;
import com.nythicalnorm.voxelspaceprogram.fluid.VSPFluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

public class HandheldThrusterFluidHandler implements IFluidHandlerItem {
    private final ItemStack container;

    public HandheldThrusterFluidHandler(ItemStack container) {
        this.container = container;
    }

    public static ItemStack getFullFilledItemStack(@NotNull Item item) {
        ItemStack itemStack = new ItemStack(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putInt("vsp_nitrogen_amount", HandheldThrusterItem.CAPACITY);

        return itemStack;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return this.container;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank != 0) {
            return FluidStack.EMPTY;
        }

        CompoundTag tag = container.getTag();
        int amount = 0;
        if (tag != null && tag.contains("vsp_nitrogen_amount")) {
            amount = tag.getInt("vsp_nitrogen_amount");
        }

        if (amount > 0) {
            return new FluidStack(VSPFluids.NITROGEN.source.get(), amount);
        }

        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        if (tank == 0) {
            return HandheldThrusterItem.CAPACITY;
        }
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return stack.getFluid().isSame(VSPFluids.NITROGEN.source.get());
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        FluidStack current = getFluidInTank(0);

        // Don't allow different fluids to mix
        if (resource.isEmpty() && !resource.getFluid().isSame(VSPFluids.NITROGEN.source.get())) {
            return 0;
        }

        int space = this.getTankCapacity(0) - current.getAmount();
        int amount = Math.min(space, resource.getAmount());

        if (amount <= 0) {
            return 0;
        }

        if (action.execute()) {
            CompoundTag tag = container.getOrCreateTag();
            tag.putInt("vsp_nitrogen_amount", current.getAmount() + amount);
        }

        return amount;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack current = getFluidInTank(0);

        // Don't allow different fluids to mix
        if (resource.isEmpty() && !resource.getFluid().isSame(VSPFluids.NITROGEN.source.get())) {
            return FluidStack.EMPTY;
        }

        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int amount, FluidAction action) {
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
                tag.remove("vsp_nitrogen_amount");
            } else {
                tag.putInt("vsp_nitrogen_amount", remaining);
            }
        }

        return result;
    }
}
