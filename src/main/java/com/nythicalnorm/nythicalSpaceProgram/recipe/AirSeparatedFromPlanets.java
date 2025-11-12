package com.nythicalnorm.nythicalSpaceProgram.recipe;

import com.nythicalnorm.nythicalSpaceProgram.fluid.ModFluids;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class AirSeparatedFromPlanets {

    public static class ManufacturingFluid {
        public Fluid resultingFluid;
        public int airSeparatorHeightNeeded;
        public int tankCapacity;
        public int creationRate;

        public ManufacturingFluid(Fluid ResultingFluid, int AirSeparatorHeightNeeded, int TankCapacity, int CreationRate) {
            this.resultingFluid = ResultingFluid;
            this.airSeparatorHeightNeeded = AirSeparatorHeightNeeded;
            this.tankCapacity = TankCapacity;
            this.creationRate = CreationRate;
        }
    }

    private static final ManufacturingFluid[] OVERWORLD_FLUIDS = new ManufacturingFluid[]{
            new ManufacturingFluid(Fluids.LAVA, 0, 20000, 10),
            new ManufacturingFluid(Fluids.WATER, 10, 10000, 25),
            new ManufacturingFluid(ModFluids.LIQUID_OXYGEN.source.get(), 20, 5000, 5)
    };

    public static ManufacturingFluid[] getRecipe(Level level) {
        if (level.dimensionTypeId() == BuiltinDimensionTypes.OVERWORLD) {
            return OVERWORLD_FLUIDS;
        }
        else {
            return null;
        }
    }
}
