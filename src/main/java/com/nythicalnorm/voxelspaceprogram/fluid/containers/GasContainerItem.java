package com.nythicalnorm.voxelspaceprogram.fluid.containers;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class GasContainerItem extends Item {
    public static final int CAPACITY = 1000;

    public GasContainerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            private final LazyOptional<IFluidHandlerItem> handler = LazyOptional.of(() -> new GasContainerFluidHandler(stack));

            @Override
            public <T> @NotNull LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
                if (capability == ForgeCapabilities.FLUID_HANDLER_ITEM) {
                    return handler.cast();
                }

                return LazyOptional.empty();
            }
        };
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getFluidAmount(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0f * getFluidAmount(stack) / CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb((float) getFluidAmount(stack) / CAPACITY * 0.33f, 1.0f, 1.0f);
    }

    public int getFluidAmount(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);

        if (handler == null) {
            return 0;
        }

        return handler.getFluidInTank(0).getAmount();
    }
}
