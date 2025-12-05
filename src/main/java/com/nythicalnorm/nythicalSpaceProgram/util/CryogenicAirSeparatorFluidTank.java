package com.nythicalnorm.nythicalSpaceProgram.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CryogenicAirSeparatorFluidTank extends FluidTank {
    public CryogenicAirSeparatorFluidTank(int capacity) {
        super(capacity);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }
}
