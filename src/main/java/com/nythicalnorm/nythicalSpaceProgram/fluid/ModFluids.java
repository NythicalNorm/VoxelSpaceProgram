package com.nythicalnorm.nythicalSpaceProgram.fluid;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, NythicalSpaceProgram.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, NythicalSpaceProgram.MODID);

    public static final FluidRegistryContainer LIQUID_OXYGEN = new FluidRegistryContainer("liquid_oxygen_fluid",
            FluidType.Properties.create()
                    .canDrown(true)
                    .canSwim(false)
                    .canPushEntity(true)
                    .canExtinguish(true),
            () -> FluidRegistryContainer.createExtension(
                    new FluidRegistryContainer.ClientExtensions(NythicalSpaceProgram.MODID, "liquid_oxygen_fluid")
                    .fogColor(0f,0f,1f)
                    .tint(0xFF0091c1)
                    .flowing("cryogenic_fluid")
                    .still("cryogenic_fluid")
                    .overlay("cryogenic_fluid")),
            BlockBehaviour.Properties.copy(Blocks.WATER), new Item.Properties(), true, false);
}
